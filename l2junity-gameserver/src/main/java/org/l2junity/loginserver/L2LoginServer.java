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
import java.sql.SQLException;

import org.l2junity.Config;
import org.l2junity.DatabaseFactory;
import org.l2junity.Server;
import org.l2junity.UPnPService;
import org.l2junity.loginserver.mail.MailSystem;
import org.l2junity.loginserver.network.L2LoginClient;
import org.l2junity.loginserver.network.L2LoginPacketHandler;
import org.mmocore.network.SelectorConfig;
import org.mmocore.network.SelectorThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public final class L2LoginServer
{
	private final Logger _log = LoggerFactory.getLogger(L2LoginServer.class);
	
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
		Server.serverMode = Server.MODE_LOGINSERVER;
		
		// Load Config
		Config.load();
		
		// Prepare Database
		try
		{
			DatabaseFactory.getInstance();
		}
		catch (SQLException e)
		{
			_log.error("FATAL: Failed initializing database. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		
		try
		{
			LoginController.load();
		}
		catch (GeneralSecurityException e)
		{
			_log.error("FATAL: Failed initializing LoginController. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		
		GameServerTable.getInstance();
		
		loadBanFile();
		
		if (Config.EMAIL_SYS_ENABLED)
		{
			MailSystem.getInstance();
		}
		
		InetAddress bindAddress = null;
		if (!Config.LOGIN_BIND_ADDRESS.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.LOGIN_BIND_ADDRESS);
			}
			catch (UnknownHostException e)
			{
				_log.warn("WARNING: The LoginServer bind address is invalid, using all avaliable IPs. Reason: " + e.getMessage(), e);
			}
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final L2LoginPacketHandler lph = new L2LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			_selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException e)
		{
			_log.error("FATAL: Failed to open Selector. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		
		try
		{
			_gameServerListener = new GameServerListener();
			_gameServerListener.start();
			_log.info("Listening for GameServers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
		}
		catch (IOException e)
		{
			_log.error("FATAL: Failed to start the Game Server Listener. Reason: " + e.getMessage(), e);
			System.exit(1);
		}

		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_LOGIN);
			_selectorThread.start();
			_log.info(getClass().getSimpleName() + ": is now listening on: " + Config.LOGIN_BIND_ADDRESS + ":" + Config.PORT_LOGIN);
		}
		catch (IOException e)
		{
			_log.error("FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		
		UPnPService.getInstance();
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
								_log.warn("Skipped: Incorrect ban duration (" + parts[1] + ") on (" + bannedFile.getName() + "). Line: " + lnr.getLineNumber());
								return;
							}
						}
						
						try
						{
							LoginController.getInstance().addBanForAddress(address, duration);
						}
						catch (UnknownHostException e)
						{
							_log.warn("Skipped: Invalid address (" + address + ") on (" + bannedFile.getName() + "). Line: " + lnr.getLineNumber());
						}
					});
				//@formatter:on
			}
			catch (IOException e)
			{
				_log.warn("Error while reading the bans file (" + bannedFile.getName() + "). Details: " + e.getMessage(), e);
			}
			_log.info("Loaded " + LoginController.getInstance().getBannedIps().size() + " IP Bans.");
		}
		else
		{
			_log.warn("IP Bans file (" + bannedFile.getName() + ") is missing or is a directory, skipped.");
		}
		
		if (Config.LOGIN_SERVER_SCHEDULE_RESTART)
		{
			_log.info("Scheduled LS restart after " + Config.LOGIN_SERVER_SCHEDULE_RESTART_TIME + " hours");
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
					Thread.sleep(Config.LOGIN_SERVER_SCHEDULE_RESTART_TIME * 3600000);
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