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
package org.l2junity.gameserver;

import java.awt.Toolkit;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.l2junity.commons.lang.management.ShutdownManager;
import org.l2junity.commons.lang.management.TerminationStatus;
import org.l2junity.commons.loader.Loader;
import org.l2junity.commons.sql.migrations.DatabaseMigrationManager;
import org.l2junity.commons.util.AppInit;
import org.l2junity.commons.util.AppInit.ApplicationMode;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.jar.VersionInfo;
import org.l2junity.gameserver.config.GameConfigMarker;
import org.l2junity.gameserver.config.ServerConfig;
import org.l2junity.gameserver.data.sql.migrations.Migrations;
import org.l2junity.gameserver.idfactory.IdFactory;
import org.l2junity.gameserver.loader.ClientAccessLoadGroup;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.loader.PostLoadGroup;
import org.l2junity.gameserver.loader.PreLoadGroup;
import org.l2junity.gameserver.loader.ScriptLoadGroup;
import org.l2junity.gameserver.loader.SideLoadGroup;
import org.l2junity.gameserver.plugins.ServerPluginProvider;
import org.l2junity.geodriver.GeoDriver;
import org.l2junity.network.NetworkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameServer
{
	public static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);
	
	private static GameServer INSTANCE;
	
	private final Loader _loader;
	
	public GameServer() throws Exception
	{
		INSTANCE = this;
		
		AppInit.defaultInit(ApplicationMode.GAME, GameConfigMarker.class.getPackage().getName(), GameThreadPools.class, GameDatabaseConfig.class);
		
		if (!IdFactory.getInstance().isInitialized())
		{
			LOGGER.error("Could not read object IDs from DB. Please check your data.");
			throw new Exception("Could not initialize the ID factory");
		}
		
		DatabaseMigrationManager.getInstance().processPackage(Migrations.class.getPackage());
		
		// Process packages
		_loader = new Loader(getClass().getPackage().getName());
		CompletableFuture<Void> sideLoadCompletableFuture = null;
		try
		{
			if (ServerConfig.MULTI_THREADED_LOADING)
			{
				sideLoadCompletableFuture = _loader.runAsync(SideLoadGroup.class);
				_loader.runAsync(PreLoadGroup.class, LoadGroup.class, ScriptLoadGroup.class, ClientAccessLoadGroup.class, PostLoadGroup.class).join();
			}
			else
			{
				_loader.run(SideLoadGroup.class, PreLoadGroup.class, LoadGroup.class, ScriptLoadGroup.class, ClientAccessLoadGroup.class, PostLoadGroup.class);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("", e);
			ShutdownManager.halt(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
			return;
		}
		
		LOGGER.info("Maximum numbers of connected players: {}", ServerConfig.MAXIMUM_ONLINE_USERS);
		LOGGER.info("IdFactory: Free ObjectID's remaining: {}", IdFactory.getInstance().size());
		Toolkit.getDefaultToolkit().beep();
		
		ServerPluginProvider.getInstance().onStart();
		
		ShutdownHooks.init();
		
		AppInit.defaultPostInit(VersionInfo.of(AppInit.class, GameServer.class, NetworkManager.class, GeoDriver.class), GameUPnPConfig.class);
		
		if (sideLoadCompletableFuture != null)
		{
			try
			{
				sideLoadCompletableFuture.join();
			}
			catch (Exception e)
			{
				LOGGER.warn("", e);
			}
			
			try
			{
				_loader.writeDependencyTreeToFile(Files.createDirectories(BasePathProvider.resolvePath(Paths.get("log", "loader-stats"))).resolve(LocalDateTime.now().format(CommonUtil.getFilenameDateTimeFormatter()) + ".txt"), SideLoadGroup.class, PreLoadGroup.class, LoadGroup.class, ScriptLoadGroup.class, ClientAccessLoadGroup.class, PostLoadGroup.class);
			}
			catch (Exception e)
			{
				LOGGER.warn("", e);
			}
		}
	}
	
	public Loader getLoader()
	{
		return _loader;
	}
	
	public long getStartedTime()
	{
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}
	
	public static void main(String[] args) throws Exception
	{
		new GameServer();
	}
	
	public static GameServer getInstance()
	{
		return INSTANCE;
	}
}
