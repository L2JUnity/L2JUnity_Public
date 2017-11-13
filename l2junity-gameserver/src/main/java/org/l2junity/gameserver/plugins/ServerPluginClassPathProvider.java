/*
 * Copyright (C) 2004-2015 L2J United
 * 
 * This file is part of L2J United.
 * 
 * L2J United is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J United is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public final class ServerPluginClassPathProvider
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerPluginClassPathProvider.class);
	
	private final ClassLoader _classLoader;
	
	protected ServerPluginClassPathProvider()
	{
		final File pluginsDir = new File("plugins");
		if (pluginsDir.isDirectory())
		{
			final File[] pluginFiles = pluginsDir.listFiles(f -> f.getName().endsWith(".jar"));
			final URL[] urls = new URL[pluginFiles.length];
			for (int i = 0; i < pluginFiles.length; i++)
			{
				try
				{
					urls[i] = pluginFiles[i].toURI().toURL();
				}
				catch (Exception e)
				{
					LOGGER.warn("Failed to process plugin: {}", pluginFiles[i]);
				}
			}
			
			_classLoader = new URLClassLoader(urls);
		}
		else
		{
			_classLoader = Thread.currentThread().getContextClassLoader();
		}
	}
	
	public ClassLoader getClassLoader()
	{
		return _classLoader;
	}
	
	/**
	 * Gets the single instance of ServerPluginClassPathProvider.
	 * @return single instance of ServerPluginClassPathProvider
	 */
	public static ServerPluginClassPathProvider getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerPluginClassPathProvider INSTANCE = new ServerPluginClassPathProvider();
	}
}
