/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.commons.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.IXmlReader;
import org.l2junity.commons.util.PropertiesParser;
import org.l2junity.commons.util.XmlReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public abstract class XmlConfigLoader implements IConfigLoader, IXmlReader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigLoader.class);
	
	public XmlConfigLoader()
	{
		load(null); // Override cannot be used with XMLs.
	}
	
	@Override
	public final void load(PropertiesParser override)
	{
		final Class<?> clazz = getClass();
		final ConfigClass configClass = clazz.getAnnotation(ConfigClass.class);
		
		if (configClass == null)
		{
			LOGGER.warn("Your class {} doesn't have @ConfigClass annotation!", clazz);
			return;
		}
		final Path configPath = BasePathProvider.resolvePath(Paths.get("", configClass.pathNames()).resolve(configClass.fileName() + ".xml"));
		
		if (Files.notExists(configPath))
		{
			pathCannotBeFound(configPath);
			return;
		}
		
		try
		{
			parseFile(configPath);
			loadImpl();
		}
		catch (XmlReaderException | IOException e)
		{
			LOGGER.error("Error loading the xml configs " + configPath, e);
		}
	}
	
	protected void loadImpl()
	{
		// In case of need, it should be overridden.
	}
	
	protected void pathCannotBeFound(Path path)
	{
		LOGGER.warn("Config File {} doesn't exist!", path);
	}
}
