/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.cache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.PathProvider;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.ServerConfig.OverrideMode;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Layane
 */
public final class HtmCache
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HtmCache.class);
	
	private static final Pattern EXTEND_PATTERN = Pattern.compile("<extend template=\"([a-zA-Z0-9-_./\\ ]*)\">(.*?)</extend>", Pattern.DOTALL);
	private static final Pattern ABSTRACT_BLOCK_PATTERN = Pattern.compile("<abstract block=\"([a-zA-Z0-9-_. ]*)\" ?/>", Pattern.DOTALL);
	private static final Pattern BLOCK_PATTERN = Pattern.compile("<block name=\"([a-zA-Z0-9-_. ]*)\">(.*?)</block>", Pattern.DOTALL);
	
	private final Map<String, String> _cache = GeneralConfig.LAZY_CACHE ? new ConcurrentHashMap<>() : new HashMap<>();
	
	private int _loadedFiles;
	private long _bytesBuffLen;
	private final PathProvider _pathProvider = new PathProvider().setAcceptFilter(path -> Files.isDirectory(path) || isLoadable(path.toString())).setOverrideLogging(false);
	
	protected HtmCache()
	{
	}
	
	@Reload("html")
	@Load(group = LoadGroup.class)
	public void load()
	{
		if (!GeneralConfig.LAZY_CACHE)
		{
			LOGGER.info("HTML caching started...");
			
			try
			{
				_pathProvider.resolveOverriddenDirectory(Paths.get("data"), true, OverrideMode.OVERLAYING).forEach(this::loadFile);
			}
			catch (final IOException e)
			{
				LOGGER.error("Exception during: ", e);
			}
			
			LOGGER.info(toString());
		}
		else
		{
			_cache.clear();
			_loadedFiles = 0;
			_bytesBuffLen = 0;
			
			LOGGER.info("Running lazy cache.");
		}
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format("%.3f", (float) _bytesBuffLen / 1_048_576));
		sb.append(" megabyte(s) on ");
		sb.append(_loadedFiles);
		sb.append(" file(s) loaded.");
		return sb.toString();
	}
	
	public boolean isLoadable(String pathName)
	{
		return pathName.endsWith(".html") || pathName.endsWith(".htm");
	}
	
	public Path getHtmFilePath(String path)
	{
		return _pathProvider.resolvePath(Paths.get(path));
	}
	
	public String loadFile(Path file)
	{
		final String lines;
		try
		{
			lines = Util.readAllLines(file, StandardCharsets.UTF_8, null);
		}
		catch (IOException e)
		{
			return null;
		}
		
		final String content = processHtml(lines);
		final String oldContent = _cache.put(_pathProvider.relativePath(file).toString(), content);
		if (oldContent == null)
		{
			_bytesBuffLen += content.length() * 2;
			_loadedFiles++;
		}
		else
		{
			_bytesBuffLen = (_bytesBuffLen - oldContent.length()) + (content.length() * 2);
		}
		
		return content;
	}
	
	public String getHtmForce(String prefix, String path)
	{
		String content = getHtm(prefix, path);
		if (content == null)
		{
			content = "<html><body>My text is missing:<br>" + path + "</body></html>";
			LOGGER.warn("Missing HTML page: {}", path);
		}
		return content;
	}
	
	public String getHtm(String prefix, String path)
	{
		String newPath = null;
		String content;
		if ((prefix != null) && !prefix.isEmpty())
		{
			newPath = prefix + "/" + path;
			content = getHtm(newPath);
			if (content != null)
			{
				return content;
			}
		}
		
		content = getHtm(path);
		if ((content != null) && (newPath != null))
		{
			_cache.put(newPath, content);
		}
		
		return content;
	}
	
	private String getHtm(String path)
	{
		return (path == null) || path.isEmpty() ? null : _cache.getOrDefault(path, GeneralConfig.LAZY_CACHE ? loadFile(getHtmFilePath(path)) : null);
	}
	
	public boolean contains(String path)
	{
		return _cache.containsKey(path);
	}
	
	private String parseTemplateName(String name)
	{
		if (!name.startsWith("data/"))
		{
			if (name.startsWith("html/"))
			{
				return "data/" + name;
			}
			else if (name.startsWith("CommunityBoard/"))
			{
				return "data/html/" + name;
			}
			else if (name.startsWith("scripts/"))
			{
				return "data/scripts/" + name;
			}
		}
		return name;
	}
	
	private String processHtml(String result)
	{
		final Matcher extendMatcher = EXTEND_PATTERN.matcher(result);
		if (extendMatcher.find())
		{
			// If extend matcher finds something, process template
			final String templateName = parseTemplateName(extendMatcher.group(1));
			
			// Generate block name -> content map
			final Map<String, String> blockMap = generateBlockMap(result);
			
			// Attempt to find the template
			String template = getHtm(templateName + "-template.htm");
			if (template != null)
			{
				// Attempt to find the abstract blocks
				final Matcher blockMatcher = ABSTRACT_BLOCK_PATTERN.matcher(template);
				while (blockMatcher.find())
				{
					final String name = blockMatcher.group(1);
					if (!blockMap.containsKey(name))
					{
						LOGGER.warn("Abstract block definition [" + name + "] is not implemented!");
						continue;
					}
					
					// Replace the matched content with the block.
					template = template.replace(blockMatcher.group(0), blockMap.get(name));
				}
				
				// Replace the entire extend block
				result = result.replace(extendMatcher.group(0), template);
			}
			else
			{
				LOGGER.warn("Missing template: " + templateName + "-template.htm !");
			}
		}
		
		result = result.replaceAll("(?s)<!--.*?-->", ""); // Remove html comments
		// result = result.replaceAll("\r", "").replaceAll("\n", ""); // Remove new lines
		return result;
	}
	
	private Map<String, String> generateBlockMap(String data)
	{
		final Map<String, String> blockMap = new LinkedHashMap<>();
		final Matcher blockMatcher = BLOCK_PATTERN.matcher(data);
		while (blockMatcher.find())
		{
			final String name = blockMatcher.group(1);
			final String content = blockMatcher.group(2);
			blockMap.put(name, content);
		}
		return blockMap;
	}
	
	@InstanceGetter
	public static HtmCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final HtmCache INSTANCE = new HtmCache();
	}
}
