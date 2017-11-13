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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.config.converter.IConfigConverter;
import org.l2junity.commons.config.generator.AbstractConfigGenerator;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NB4L1 (L2JFree concept)
 * @author _dev_ (L2Emu concept)
 * @author lord_rex (L2JUnity concept)
 */
public abstract class ConfigPropertiesLoader implements IConfigLoader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropertiesLoader.class);
	
	public ConfigPropertiesLoader()
	{
		// visibility
	}
	
	/**
	 * Loads the config file and overrides its properties from override config if necessary.
	 * @param override the properties from override config
	 */
	@Override
	public final void load(PropertiesParser override)
	{
		final Class<?> clazz = getClass();
		final ConfigClass configClass = clazz.getAnnotation(ConfigClass.class);
		if (configClass == null)
		{
			LOGGER.warn("Class {} doesn't have @ConfigClass annotation!", clazz);
			return;
		}
		
		final Path configPath = BasePathProvider.resolvePath(Paths.get("", configClass.pathNames()).resolve(configClass.fileName() + configClass.fileExtension()));
		if (Files.notExists(configPath))
		{
			LOGGER.warn("Config File {} doesn't exist! Generating ...", configPath);
			
			try
			{
				AbstractConfigGenerator.printConfigClass(clazz);
			}
			catch (IOException e)
			{
				LOGGER.warn("Failed to generate config!", e);
			}
		}
		
		final PropertiesParser properties = new PropertiesParser(configPath);
		for (Field field : clazz.getFields())
		{
			if (field == null)
			{
				continue;
			}
			
			// Skip inappropriate fields.
			if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
			{
				continue;
			}
			
			final ConfigField configField = field.getAnnotation(ConfigField.class);
			if (configField != null)
			{
				// If field is just a comment holder, then do not try to load it.
				if (configField.onlyComment())
				{
					continue;
				}
				
				try
				{
					final String propertyKey = configField.name();
					final String propertyValue = configField.value();
					ConfigManager.getInstance().registerProperty(clazz.getPackage().getName(), configPath, propertyKey);
					if (!configField.reloadable() && ConfigManager.getInstance().isReloading())
					{
						LOGGER.debug("Property '{}' retained with its previous value!", propertyKey);
						continue;
					}
					
					final String configProperty = getProperty(properties, override, propertyKey, propertyValue);
					final IConfigConverter converter = configField.converter().newInstance();
					field.set(null, converter.convertFromString(field, field.getType(), configProperty));
				}
				catch (Exception e)
				{
					LOGGER.warn("Failed to set field!", e);
				}
			}
		}
		
		loadImpl(properties, override);
		
		LOGGER.debug("Loaded '{}'", configPath);
	}
	
	/**
	 * Gets the right property. Its usage is mandatory for non-annotation based configs, without it the override function won't work.<br>
	 * For non-annotation based configs please use {@link #loadImpl(PropertiesParser, PropertiesParser)}.
	 * @param properties the original properties
	 * @param override the properties from override config
	 * @param name the name of the property
	 * @param defaultValue a default value which will be used when the property is missing
	 * @return the property
	 */
	private String getProperty(PropertiesParser properties, PropertiesParser override, String name, String defaultValue)
	{
		String property = override.getValue(name);
		if (property == null)
		{
			property = properties.getValue(name);
			if (property == null)
			{
				LOGGER.warn("Property key '{}' is missing, using default value!", name);
				return defaultValue;
			}
		}
		return property;
	}
	
	/**
	 * Gets the result of two properties parser, where second is the override which overwrites the content of the first if necessary.
	 * @param properties
	 * @param override
	 * @return properties of the two properties parser
	 */
	public static Properties propertiesOf(PropertiesParser properties, PropertiesParser override)
	{
		final Properties result = new Properties();
		//@formatter:off
		Stream.concat(properties.entrySet().stream(), override.entrySet().stream())
				.forEach(e -> result.setProperty(e.getKey().toString(), e.getValue().toString()));
		//@formatter:on
		return result;
	}
	
	/**
	 * A properties loader method for alternative configs. Can be used for post hook events or for non-annotation based configs.
	 * @param properties the original properties
	 * @param override the properties from override config
	 */
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		// In case of need, it should be overridden.
	}
}
