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
package org.l2junity.commons.util;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.l2junity.commons.config.ConfigManager;
import org.l2junity.commons.lang.management.DeadlockDetector;
import org.l2junity.commons.lang.management.ShutdownManager;
import org.l2junity.commons.lang.management.TerminationStatus;
import org.l2junity.commons.network.IUPnPConfig;
import org.l2junity.commons.network.UPnPService;
import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.sql.IDatabaseConfig;
import org.l2junity.commons.util.concurrent.IThreadPoolInitializer;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author _dev_
 */
public final class AppInit
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AppInit.class);
	
	/** A way to determine whether the running application is LS, GS or other. */
	public static ApplicationMode MODE = null;
	
	/** JUnit scripts run at once. JVM is not terminated, therefore this boolean is necessary. */
	public static boolean INITIALIZED = false;
	
	public enum ApplicationMode
	{
		LOGIN,
		GAME,
		OTHER;
		
		public String getModeName()
		{
			return name().toLowerCase();
		}
	}
	
	private AppInit()
	{
		// utility class
	}
	
	/**
	 * A dedicated method for all unity based applications initialization process. Prints useful system information and loads the commonly used functionalities.
	 * @param mode the application mode, used for {@link BasePathProvider}
	 * @param configPackageName the package that holds config classes
	 * @param classes be careful: order is important, and some of the modules cannot be used without each-other!
	 */
	public static void defaultInit(final ApplicationMode mode, final String configPackageName, final Class<?>... classes)
	{
		if (INITIALIZED)
		{
			return;
		}
		
		MODE = mode;
		
		Thread.setDefaultUncaughtExceptionHandler((t, e) ->
		{
			LOGGER.warn("Exception occured on thread: {}", t, e);
			
			// restart automatically
			if ((e instanceof Error) && !(e instanceof StackOverflowError))
			{
				ShutdownManager.halt(TerminationStatus.RUNTIME_UNCAUGHT_ERROR);
			}
		});
		
		if (StringUtil.isNotEmpty(configPackageName))
		{
			CommonUtil.printSection("Configuration");
			ConfigManager.getInstance().load(configPackageName);
		}
		
		CommonUtil.printSection("System Info");
		SystemUtil.printGeneralSystemInfo();
		
		CommonUtil.printSection("Shutdown Hook");
		ShutdownManager.initShutdownHook();
		
		CommonUtil.printSection("Deadlock Detector");
		DeadlockDetector.getInstance();
		
		for (final Class<?> clazz : classes)
		{
			if (IThreadPoolInitializer.class.isAssignableFrom(clazz))
			{
				CommonUtil.printSection("Thread Pool(s)");
				
				try
				{
					ThreadPool.initThreadPools((IThreadPoolInitializer) clazz.newInstance());
				}
				catch (final Exception e)
				{
					LOGGER.error("Could not setup thread pools!", e);
					ShutdownManager.exit(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
					return;
				}
			}
			
			if (IDatabaseConfig.class.isAssignableFrom(clazz))
			{
				CommonUtil.printSection("Database");
				
				try
				{
					DatabaseFactory.getInstance().setDatabaseConfig((IDatabaseConfig) clazz.newInstance());
					DatabaseFactory.getInstance().init();
				}
				catch (Exception e)
				{
					LOGGER.error("Could not initialize database!", e);
					ShutdownManager.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
					return;
				}
			}
		}
		
		// Just a splitter, since GS won't use printSection, but commons has to, so it will make it look prettier.
		CommonUtil.printSection("Application");
		
		INITIALIZED = true;
	}
	
	/**
	 * The default post-init for all unity based applications. This method finishes the initialization.
	 * @param dependencyInfo version info mostly, but can be anything else
	 * @param classes post init classes
	 */
	public static void defaultPostInit(final List<String> dependencyInfo, final Class<?>... classes)
	{
		for (final Class<?> clazz : classes)
		{
			if (IUPnPConfig.class.isAssignableFrom(clazz))
			{
				CommonUtil.printSection("UPnP");
				
				try
				{
					UPnPService.getInstance().setUPnPConfig((IUPnPConfig) clazz.newInstance());
					UPnPService.getInstance().init();
				}
				catch (Exception e)
				{
					LOGGER.warn("Failed to initialize UPnP!", e);
				}
			}
		}
		
		// Version Info here...
		if ((dependencyInfo != null) && !dependencyInfo.isEmpty())
		{
			CommonUtil.printSection("Execution Environment");
			for (final String line : dependencyInfo)
			{
				LOGGER.info(line);
			}
		}
		
		// Memory statistics below.
		CommonUtil.printSection("Memory (Heap)");
		
		final long gcBeginTime = System.nanoTime();
		System.gc();
		System.runFinalization();
		final long gcEndTime = System.nanoTime();
		
		// Yes, there actually is a machine where it has to be measured.
		LOGGER.info("GC done in {}.", TimeAmountInterpreter.consolidateNanos(gcEndTime - gcBeginTime));
		
		for (final String line : SystemUtil.getMemoryUsageStatistics())
		{
			LOGGER.info(line);
		}
		
		// Pretty initialization time.
		CommonUtil.printSection("Summary");
		LOGGER.info("Initialized in {}.", TimeAmountInterpreter.consolidateMillis(ManagementFactory.getRuntimeMXBean().getUptime()));
		
		// To split everything that comes after boot.
		CommonUtil.printSection("ApplicationLog");
	}
}
