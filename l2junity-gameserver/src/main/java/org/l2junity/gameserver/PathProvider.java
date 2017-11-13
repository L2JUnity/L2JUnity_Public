/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.gameserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.gameserver.config.ServerConfig;
import org.l2junity.gameserver.config.ServerConfig.OverrideMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class PathProvider
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PathProvider.class);
	
	private final Path _serverName = Paths.get(ServerConfig.SERVERS_PATH, ServerConfig.SERVER_NAME);
	private Predicate<Path> _acceptFilter = path -> true;
	private boolean _overrideLogging;
	
	public PathProvider()
	{
	}
	
	public PathProvider setAcceptFilter(Predicate<Path> filter)
	{
		_acceptFilter = Objects.requireNonNull(filter);
		return this;
	}
	
	public PathProvider setOverrideLogging(boolean value)
	{
		_overrideLogging = value;
		return this;
	}
	
	/**
	 * Constructs an absolute path between datapack root or override root path and a given path.
	 * @param path the absolute path against datapack root or override root
	 * @return the resulting absolute path, or an empty path if both paths are equal
	 */
	public Path resolvePath(Path path)
	{
		final Path datapackOverridePath = ServerConfig.DATAPACK_ROOT.resolve(_serverName).resolve(path).normalize();
		final Path baseOverridePath = BasePathProvider.resolvePath(_serverName).resolve(path).normalize();
		final Path datapackPath = ServerConfig.DATAPACK_ROOT.resolve(path).normalize();
		final Path basePath = BasePathProvider.resolvePath(path).normalize();
		
		if (Files.exists(datapackOverridePath))
		{
			return datapackOverridePath;
		}
		else if (Files.exists(baseOverridePath))
		{
			return baseOverridePath;
		}
		else if (Files.exists(datapackPath))
		{
			return datapackPath;
		}
		return basePath;
	}
	
	/**
	 * Constructs a relative path between datapack root or override root path and a given path.
	 * @param path the path to relativize against datapack root or override root
	 * @return the resulting relative path, or an empty path if both paths are equal
	 */
	public Path relativePath(Path path)
	{
		final Path datapackOverridePath = ServerConfig.DATAPACK_ROOT.resolve(_serverName).resolve(path).normalize();
		final Path baseOverridePath = BasePathProvider.resolvePath(_serverName).resolve(path).normalize();
		final Path datapackPath = ServerConfig.DATAPACK_ROOT.resolve(path).normalize();
		final Path basePath = BasePathProvider.resolvePath(path).normalize();
		
		if (Files.exists(datapackOverridePath))
		{
			return ServerConfig.DATAPACK_ROOT.resolve(_serverName).relativize(datapackOverridePath);
		}
		else if (Files.exists(baseOverridePath))
		{
			BasePathProvider.resolvePath(_serverName).relativize(baseOverridePath);
		}
		else if (Files.exists(datapackPath))
		{
			return ServerConfig.DATAPACK_ROOT.relativize(datapackPath);
		}
		else if (Files.exists(basePath))
		{
			return BasePathProvider.getBasePath().relativize(basePath);
		}
		return path;
	}
	
	/**
	 * Resolves file using the override system either incrementally or overlaying.<br>
	 * Used for single DataPack file loading. Will load a list of 2 files in case of incremental load, otherwise a single file is loaded.
	 * @param path
	 * @param overrideMode
	 * @return overridden file(s)
	 */
	public Set<Path> resolveOverriddenFile(Path path, OverrideMode overrideMode)
	{
		final Set<Path> files = new LinkedHashSet<>();
		switch (overrideMode)
		{
			case INCREMENTAL:
			{
				checkAndAddPath(files, ServerConfig.DATAPACK_ROOT.resolve(_serverName).resolve(path).normalize(), overrideMode, true);
				checkAndAddPath(files, BasePathProvider.resolvePath(_serverName).resolve(path).normalize(), overrideMode, true);
				checkAndAddPath(files, ServerConfig.DATAPACK_ROOT.resolve(path).normalize(), overrideMode, false);
				checkAndAddPath(files, BasePathProvider.resolvePath(path).normalize(), overrideMode, false);
				break;
			}
			case OVERLAYING:
			{
				final Path resolvedPath = resolvePath(path);
				if (Files.exists(resolvedPath))
				{
					files.add(resolvedPath);
				}
				break;
			}
			default:
			{
				throw new InternalError(); // never happens
			}
		}
		return files;
	}
	
	/**
	 * Resolves the directory using override system and its files either incrementally or overlaying.<br>
	 * Used for DataPack directory loading.
	 * @param dir
	 * @param recursive
	 * @param overrideMode
	 * @return list of the paths in the overridden directory
	 * @throws IOException
	 */
	public Set<Path> resolveOverriddenDirectory(Path dir, boolean recursive, OverrideMode overrideMode) throws IOException
	{
		final Set<Path> files = new LinkedHashSet<>();
		final Path datapackOverridePath = ServerConfig.DATAPACK_ROOT.resolve(_serverName).resolve(dir).normalize();
		final Path baseOverridePath = BasePathProvider.resolvePath(_serverName).resolve(dir).normalize();
		final Path datapackPath = ServerConfig.DATAPACK_ROOT.resolve(dir).normalize();
		final Path basePath = BasePathProvider.resolvePath(dir).normalize();
		
		switch (overrideMode)
		{
			case INCREMENTAL:
			{
				checkAndAddDir(files, basePath, recursive, overrideMode, Files.exists(basePath) && Files.exists(datapackPath) && !Files.isSameFile(basePath, datapackPath), path -> true);
				checkAndAddDir(files, datapackPath, recursive, overrideMode, false, path -> true);
				checkAndAddDir(files, baseOverridePath, recursive, overrideMode, true, path -> true);
				checkAndAddDir(files, datapackOverridePath, recursive, overrideMode, true, path -> true);
				break;
			}
			case OVERLAYING:
			{
				final Map<String, Path> paths = new LinkedHashMap<>();
				checkAndAddDir(files, basePath, recursive, overrideMode, Files.exists(basePath) && Files.exists(datapackPath) && !Files.isSameFile(basePath, datapackPath), path -> paths.putIfAbsent(basePath.relativize(path).toString(), path) == null);
				checkAndAddDir(files, datapackPath, recursive, overrideMode, false, path -> paths.putIfAbsent(datapackPath.relativize(path).toString(), path) == null);
				checkAndAddDir(files, baseOverridePath, recursive, overrideMode, true, path -> paths.putIfAbsent(baseOverridePath.relativize(path).toString(), path) == null);
				checkAndAddDir(files, datapackOverridePath, recursive, overrideMode, true, path -> paths.putIfAbsent(datapackOverridePath.relativize(path).toString(), path) == null);
				break;
			}
			default:
			{
				throw new InternalError(); // never happens
			}
		}
		
		return files;
	}
	
	/**
	 * @param paths
	 * @param path
	 * @param overrideMode
	 * @param override
	 */
	private void checkAndAddPath(Set<Path> paths, Path path, OverrideMode overrideMode, boolean override)
	{
		if (Files.exists(path) && _acceptFilter.test(path))
		{
			if (_overrideLogging && override)
			{
				LOGGER.info("override system [{}] - using: '{}'", overrideMode, path);
			}
			paths.add(path);
		}
	}
	
	/**
	 * @param paths
	 * @param dir
	 * @param recursive
	 * @param overrideMode
	 * @param override
	 * @param filter
	 * @throws IOException
	 */
	private void checkAndAddDir(Set<Path> paths, Path dir, boolean recursive, OverrideMode overrideMode, boolean override, Predicate<Path> filter) throws IOException
	{
		if (Files.exists(dir) && _acceptFilter.test(dir))
		{
			Files.walk(dir, recursive ? Integer.MAX_VALUE : 1).forEach(path ->
			{
				if (filter.test(path) && _acceptFilter.test(path))
				{
					if (_overrideLogging && override)
					{
						LOGGER.info("override system [{}] - using: '{}'", overrideMode, path);
					}
					paths.add(path);
				}
			});
		}
	}
}
