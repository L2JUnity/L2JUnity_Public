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
package org.l2junity.gameserver.data.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2junity.commons.util.IXmlReader;
import org.l2junity.commons.util.XmlReaderException;
import org.l2junity.gameserver.PathProvider;
import org.l2junity.gameserver.config.ServerConfig;
import org.l2junity.gameserver.config.ServerConfig.OverrideMode;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.holders.MinionHolder;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Interface for XML parsers.<br>
 * @author Zoey76 (l2j concept)
 * @author UnAfraid (BasePathProvider concept)
 */
public interface IGameXmlReader extends IXmlReader
{
	static final Logger LOGGER = LoggerFactory.getLogger(IGameXmlReader.class);
	
	/**
	 * Whether logging is allowed or not.
	 * @return logging
	 */
	default boolean overrideSystemLogging()
	{
		return true;
	}
	
	/**
	 * The override mode of the class specified by configuration on user-side, or the default override mode also specified by the user.
	 * @return override mode
	 */
	default OverrideMode overrideMode()
	{
		return ServerConfig.OVERRIDE_MODES.getOrDefault(getClass().getSimpleName(), ServerConfig.DEFAULT_OVERRIDE_MODE);
	}
	
	/**
	 * A simple accepter filter for override system.
	 * @param path
	 * @return whether file is accepted by the filter or not
	 */
	default boolean acceptFilter(Path path)
	{
		return true;
	}
	
	/**
	 * Wrapper for {@link #parseFile(Path)} method.
	 * @param path the relative path to the datapack root of the XML file to parse.
	 * @throws IOException
	 * @throws XmlReaderException
	 */
	default void parseDatapackFile(final String path) throws IOException, XmlReaderException
	{
		final PathProvider pathProvider = new PathProvider().setAcceptFilter(this::acceptFilter).setOverrideLogging(overrideSystemLogging());
		for (final Path file : pathProvider.resolveOverriddenFile(Paths.get(path), overrideMode()))
		{
			parseFile(file);
		}
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(Path, boolean)}.
	 * @param path the path to the directory where the XML files are
	 * @param recursive parses all sub folders if there is
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise
	 * @throws IOException
	 */
	default boolean parseDatapackDirectory(final String path, final boolean recursive) throws IOException
	{
		return parseDirectory(Paths.get(path), recursive);
	}
	
	/**
	 * Loads all XML files from {@code path} and calls {@link #parseFile(Path)} for each one of them.
	 * @param dir the directory object to scan.
	 * @param recursive parses all sub folders if there is.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 * @throws IOException
	 */
	@Override
	default boolean parseDirectory(final Path dir, final boolean recursive) throws IOException
	{
		final PathProvider pathProvider = new PathProvider().setAcceptFilter(this::acceptFilter).setOverrideLogging(overrideSystemLogging());
		pathProvider.resolveOverriddenDirectory(dir, recursive, overrideMode()).forEach(file ->
		{
			try
			{
				parseFile(file);
			}
			catch (final IOException | XmlReaderException e)
			{
				LOGGER.warn("Failed to load file: '" + file + "'", e);
			}
		});
		
		return true;
	}
	
	/**
	 * @param n
	 * @return a map of parameters
	 */
	default Map<String, Object> parseParameters(Node n)
	{
		final Map<String, Object> parameters = new HashMap<>();
		for (Node parameters_node = n.getFirstChild(); parameters_node != null; parameters_node = parameters_node.getNextSibling())
		{
			NamedNodeMap attrs = parameters_node.getAttributes();
			switch (parameters_node.getNodeName().toLowerCase())
			{
				case "param":
				{
					parameters.put(parseString(attrs, "name"), parseString(attrs, "value"));
					break;
				}
				case "skill":
				{
					parameters.put(parseString(attrs, "name"), new SkillHolder(parseInteger(attrs, "id"), parseInteger(attrs, "level")));
					break;
				}
				case "location":
				{
					parameters.put(parseString(attrs, "name"), new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z"), parseInteger(attrs, "heading", 0)));
					break;
				}
				case "minions":
				{
					final List<MinionHolder> minions = new ArrayList<>(1);
					for (Node minions_node = parameters_node.getFirstChild(); minions_node != null; minions_node = minions_node.getNextSibling())
					{
						if (minions_node.getNodeName().equalsIgnoreCase("npc"))
						{
							attrs = minions_node.getAttributes();
							minions.add(new MinionHolder(parseInteger(attrs, "id"), parseInteger(attrs, "count"), parseInteger(attrs, "respawnTime"), parseInteger(attrs, "weightPoint")));
						}
					}
					
					if (!minions.isEmpty())
					{
						parameters.put(parseString(parameters_node.getAttributes(), "name"), minions);
					}
					break;
				}
			}
		}
		return parameters;
	}
	
	default Location parseLocation(Node n)
	{
		final NamedNodeMap attrs = n.getAttributes();
		final int x = parseInteger(attrs, "x");
		final int y = parseInteger(attrs, "y");
		final int z = parseInteger(attrs, "z");
		final int heading = parseInteger(attrs, "heading", 0);
		return new Location(x, y, z, heading);
	}
}
