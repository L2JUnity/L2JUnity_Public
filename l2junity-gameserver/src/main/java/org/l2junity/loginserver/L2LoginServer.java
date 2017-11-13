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
package org.l2junity.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import org.l2junity.commons.lang.management.ShutdownManager;
import org.l2junity.commons.lang.management.TerminationStatus;
import org.l2junity.commons.util.AppInit;
import org.l2junity.commons.util.AppInit.ApplicationMode;
import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.jar.VersionInfo;
import org.l2junity.loginserver.config.EmailConfig;
import org.l2junity.loginserver.config.LoginConfigMarker;
import org.l2junity.loginserver.config.LoginServerConfig;
import org.l2junity.loginserver.config.MMOCoreConfig;
import org.l2junity.loginserver.mail.MailSystem;
import org.l2junity.loginserver.network.L2LoginClient;
import org.l2junity.loginserver.network.L2LoginPacketHandler;
import org.mmocore.network.MMOClient;
import org.mmocore.network.SelectorConfig;
import org.mmocore.network.SelectorThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public final class L2LoginServer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(L2LoginServer.class);
	
	public static final int PROTOCOL_REV = 0x0106;
	private static L2LoginServer _instance;
	private GameServerListener _gameServerListener;
	private SelectorThread<L2LoginClient> _selectorThread;
	private Thread _restartLoginServer;
	
	public static void main(String[] args)
	{
		new L2LoginServer();
	}
	
	public static L2LoginServer getInstance()
	{
		return _instance;
	}
	
	private L2LoginServer()
	{
		_instance = this;
		
		AppInit.defaultInit(ApplicationMode.LOGIN, LoginConfigMarker.class.getPackage().getName(), LoginThreadPools.class, LoginDatabaseConfig.class);
		
		CommonUtil.printSection("Manager");
		
		try
		{
			LoginController.load();
		}
		catch (GeneralSecurityException e)
		{
			LOGGER.error("FATAL: Failed initializing LoginController. Reason: " + e.getMessage(), e);
			ShutdownManager.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
			return;
		}
		
		GameServerTable.getInstance();
		
		loadBanFile();
		
		if (EmailConfig.EMAIL_SYS_ENABLED)
		{
			MailSystem.getInstance();
		}
		
		InetAddress bindAddress = null;
		if (!LoginServerConfig.LOGIN_BIND_ADDRESS.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(LoginServerConfig.LOGIN_BIND_ADDRESS);
			}
			catch (UnknownHostException e)
			{
				LOGGER.warn("WARNING: The LoginServer bind address is invalid, using all avaliable IPs. Reason: " + e.getMessage(), e);
			}
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = MMOCoreConfig.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = MMOCoreConfig.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = MMOCoreConfig.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = MMOCoreConfig.MMO_HELPER_BUFFER_COUNT;
		
		final L2LoginPacketHandler lph = new L2LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			_selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException e)
		{
			LOGGER.error("FATAL: Failed to open Selector. Reason: " + e.getMessage(), e);
			ShutdownManager.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
			return;
		}
		
		try
		{
			_gameServerListener = new GameServerListener();
			_gameServerListener.start();
			LOGGER.info("Listening for GameServers on " + LoginServerConfig.GAME_SERVER_LOGIN_HOST + ":" + LoginServerConfig.GAME_SERVER_LOGIN_PORT);
		}
		catch (IOException e)
		{
			LOGGER.error("FATAL: Failed to start the Game Server Listener. Reason: " + e.getMessage(), e);
			ShutdownManager.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
			return;
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, LoginServerConfig.PORT_LOGIN);
			_selectorThread.start();
			LOGGER.info(getClass().getSimpleName() + ": is now listening on: " + LoginServerConfig.LOGIN_BIND_ADDRESS + ":" + LoginServerConfig.PORT_LOGIN);
		}
		catch (IOException e)
		{
			LOGGER.error("FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			ShutdownManager.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
			return;
		}
		
		AppInit.defaultPostInit(VersionInfo.of(AppInit.class, L2LoginServer.class, MMOClient.class), LoginUPnPConfig.class);
	}
	
	public GameServerListener getGameServerListener()
	{
		return _gameServerListener;
	}
	
	private void loadBanFile()
	{
		final File bannedFile = new File("./banned_ip.cfg");
		if (bannedFile.exists() && bannedFile.isFile())
		{
			try (FileInputStream fis = new FileInputStream(bannedFile);
				InputStreamReader is = new InputStreamReader(fis);
				LineNumberReader lnr = new LineNumberReader(is))
			{
				//@formatter:off
				lnr.lines()
					.map(String::trim)
					.filter(l -> !l.isEmpty() && (l.charAt(0) != '#'))
					.forEach(line -> {
						String[] parts = line.split("#", 2); // address[ duration][ # comments]
						line = parts[0];
						parts = line.split("\\s+"); // durations might be aligned via multiple spaces
						String address = parts[0];
						long duration = 0;
						
						if (parts.length > 1)
						{
							try
							{
								duration = Long.parseLong(parts[1]);
							}
							catch (NumberFormatException nfe)
							{
								LOGGER.warn("Skipped: Incorrect ban duration (" + parts[1] + ") on (" + bannedFile.getName() + "). Line: " + lnr.getLineNumber());
								return;
							}
						}
						
						try
						{
							LoginController.getInstance().addBanForAddress(address, duration);
						}
						catch (UnknownHostException e)
						{
							LOGGER.warn("Skipped: Invalid address (" + address + ") on (" + bannedFile.getName() + "). Line: " + lnr.getLineNumber());
						}
					});
				//@formatter:on
			}
			catch (IOException e)
			{
				LOGGER.warn("Error while reading the bans file (" + bannedFile.getName() + "). Details: " + e.getMessage(), e);
			}
			LOGGER.info("Loaded " + LoginController.getInstance().getBannedIps().size() + " IP Bans.");
		}
		else
		{
			LOGGER.warn("IP Bans file (" + bannedFile.getName() + ") is missing or is a directory, skipped.");
		}
		
		if (LoginServerConfig.LOGIN_SERVER_SCHEDULE_RESTART)
		{
			LOGGER.info("Scheduled LS restart after " + LoginServerConfig.LOGIN_SERVER_SCHEDULE_RESTART_TIME + " hours");
			_restartLoginServer = new LoginServerRestart();
			_restartLoginServer.setDaemon(true);
			_restartLoginServer.start();
		}
	}
	
	class LoginServerRestart extends Thread
	{
		public LoginServerRestart()
		{
			setName("LoginServerRestart");
		}
		
		@Override
		public void run()
		{
			while (!isInterrupted())
			{
				try
				{
					Thread.sleep(LoginServerConfig.LOGIN_SERVER_SCHEDULE_RESTART_TIME * 3600000);
				}
				catch (InterruptedException e)
				{
					return;
				}
				shutdown(true);
			}
		}
	}
	
	public void shutdown(boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}
