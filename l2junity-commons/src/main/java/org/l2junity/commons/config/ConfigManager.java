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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.l2junity.commons.lang.mutable.MutableInt;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.ClassPathUtil;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public final class ConfigManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
	
	private final AtomicBoolean _reloading = new AtomicBoolean(false);
	private final Map<String, Map<Path, Set<String>>> _propertiesRegistry = new TreeMap<>();
	private final Path _overridePath;
	private PropertiesParser _overridenProperties;
	
	protected ConfigManager()
	{
		_overridePath = BasePathProvider.resolvePath(Paths.get("config", "override.properties"));
		if (Files.notExists(_overridePath))
		{
			try
			{
				Files.createDirectories(_overridePath.getParent());
				Files.createFile(_overridePath);
			}
			catch (final IOException e)
			{
				// Disaster, disaster! Read-only FS alert! NOW!!
				throw new Error("Failed to create override config and/or its directory!", e);
			}
		}
		
		_overridenProperties = new PropertiesParser(_overridePath);
	}
	
	/**
	 * Loads overridden properties, or creates an empty override.properties file if doesn't exist.
	 * @return overridden properties
	 */
	public PropertiesParser getOverridenProperties()
	{
		return _overridenProperties;
	}
	
	/**
	 * Registers a configuration property into this manager.
	 * @param packageName
	 * @param configFile
	 * @param propertyKey
	 */
	public void registerProperty(String packageName, Path configFile, String propertyKey)
	{
		if (!_propertiesRegistry.containsKey(packageName))
		{
			_propertiesRegistry.put(packageName, new HashMap<>());
		}
		
		if (!_propertiesRegistry.get(packageName).containsKey(configFile))
		{
			_propertiesRegistry.get(packageName).put(configFile, new TreeSet<>());
		}
		
		_propertiesRegistry.get(packageName).entrySet().forEach(entry ->
		{
			final Path entryConfigFile = entry.getKey();
			final Set<String> entryProperties = entry.getValue();
			
			if (entryProperties.contains(propertyKey))
			{
				LOGGER.warn("Property key '{}' is already defined in config file '{}', so now '{}' overwrites that! Please fix this!", propertyKey, entryConfigFile, configFile);
			}
		});
		
		_propertiesRegistry.get(packageName).get(configFile).add(propertyKey);
	}
	
	/**
	 * Loads all config classes from the specified package and overwrites their configs according to override config if necessary.
	 * @param packageName the name of the package which contains the config classes
	 */
	public void load(String packageName)
	{
		if (_overridenProperties == null)
		{
			throw new NullPointerException("Override properties is missing!");
		}
		
		final MutableInt configCount = new MutableInt();
		try
		{
			ClassPathUtil.getAllClassesExtending(packageName, IConfigLoader.class).forEach(clazz ->
			{
				try
				{
					final IConfigLoader configLoader = clazz.newInstance();
					configLoader.load(_overridenProperties);
					configCount.increment();
				}
				catch (InstantiationException | IllegalAccessException e)
				{
					LOGGER.warn("Failed to load config.", e);
				}
			});
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to load class path.", e);
		}
		
		LOGGER.info("Loaded {} config file(s).", configCount.getValue());
	}
	
	/**
	 * Reloads configurations by package name.
	 * @param packageName
	 */
	public void reload(String packageName)
	{
		// overridden properties will always be reloaded
		// as it is path, and not package based, and so not need to be
		// though any package might use override.properties
		_overridenProperties = new PropertiesParser(_overridePath);
		
		if (_propertiesRegistry.containsKey(packageName))
		{
			_propertiesRegistry.get(packageName).clear();
		}
		
		_reloading.set(true);
		try
		{
			load(packageName);
		}
		finally
		{
			_reloading.set(false);
		}
	}
	
	/**
	 * Checks whether reload is in progress or not.
	 * @return {@code true} if reload is in progress, otherwise {@code false}
	 */
	public boolean isReloading()
	{
		return _reloading.get();
	}
	
	private static final class SingletonHolder
	{
		protected static final ConfigManager INSTANCE = new ConfigManager();
	}
	
	public static ConfigManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
