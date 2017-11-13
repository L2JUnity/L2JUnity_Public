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
package org.l2junity.loginserver.config;

import java.nio.file.Path;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.config.annotation.ConfigGroupBeginning;
import org.l2junity.commons.config.annotation.ConfigGroupEnding;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "LoginServer", comment =
{
	"This is the server configuration file. Here you can set up the connection information for your server.",
	"This was written with the assumption that you are behind a router.",
	"Dumbed Down Definitions...",
	"LAN (LOCAL area network) - typically consists of computers connected to the same router as you.",
	"WAN (WIDE area network) - typically consists of computers OUTSIDE of your router (ie. the internet).",
	"x.x.x.x - Format of an IP address. Do not include the x'es into settings. Must be real numbers.",
})
public final class LoginServerConfig extends ConfigPropertiesLoader
{
	@ConfigGroupBeginning(name = "Network")
	@ConfigField(name = "EnableUPnP", value = "false", comment =
	{
		"Enables automatic port mapping for login server.",
		"If you have a router login server will request for port forwarding.",
	}, reloadable = false)
	public static boolean ENABLE_UPNP;
	
	@ConfigField(name = "LoginHostname", value = "127.0.0.1", comment =
	{
		"The address on which login will listen for GameServers, use * to bind on all available IPs",
		"WARNING: <u><b><font color=\"red\">Please don't change default IPs here if you don't know what are you doing!</font></b></u>",
		"WARNING: <u><b><font color=\"red\">External/Internal IPs are now inside \"ipconfig.xml\" file.</font></b></u>",
	}, reloadable = false)
	public static String GAME_SERVER_LOGIN_HOST;
	
	@ConfigField(name = "LoginPort", value = "9014", comment =
	{
		"The port on which login will listen for GameServers."
	}, reloadable = false)
	public static int GAME_SERVER_LOGIN_PORT;
	
	@ConfigField(name = "LoginserverHostname", value = "*", comment =
	{
		"Bind ip of the LoginServer, use * to bind on all available IPs",
		"WARNING: <u><b><font color='red'>Please don't change default IPs here if you don't know what are you doing!</font></b></u>",
		"WARNING: <u><b><font color='red'>External/Internal IPs are now inside 'ipconfig.xml' file.</font></b></u>"
	}, reloadable = false)
	public static String LOGIN_BIND_ADDRESS;
	
	@ConfigGroupEnding(name = "Network")
	@ConfigField(name = "LoginserverPort", value = "2106", reloadable = false)
	public static int PORT_LOGIN;
	
	@ConfigGroupBeginning(name = "Database")
	@ConfigField(name = "URL", value = "jdbc:mysql://localhost/l2jls?useSSL=false&serverTimezone=Europe/Paris", comment =
	{
		"Database URL",
		"URL = jdbc:mysql://localhost/l2jls?useSSL=false&serverTimezone=Europe/Paris (default)",
		"URL = jdbc:hsqldb:hsql://localhost/l2jls",
		"URL = jdbc:sqlserver://localhost/database = l2jls/user = sa/password = ",
	}, reloadable = false)
	public static String DATABASE_URL;
	
	@ConfigField(name = "Login", value = "root", comment =
	{
		"Database user info (default is \"root\" but it's not recommended)."
	}, reloadable = false)
	public static String DATABASE_LOGIN;
	
	@ConfigField(name = "Password", value = "", comment =
	{
		"Database connection password."
	}, reloadable = false)
	public static String DATABASE_PASSWORD;
	
	@ConfigField(name = "MaximumDbConnections", value = "10", reloadable = false)
	public static int DATABASE_MAX_CONNECTIONS;
	
	@ConfigField(name = "MaximumDbIdleTime", value = "0", reloadable = false)
	public static int DATABASE_MAX_IDLE_TIME;
	
	@ConfigGroupEnding(name = "Database")
	@ConfigField(name = "ConnectionCloseTime", value = "60000", comment =
	{
		"Connection close time."
	}, reloadable = false)
	public static long CONNECTION_CLOSE_TIME;
	
	@ConfigField(name = "ShowLicence", value = "true", comment =
	{
		"If False, the license (after the login) will not be shown."
	})
	public static boolean SHOW_LICENCE;
	
	@ConfigField(name = "ShowPIAgreement", value = "false", comment =
	{
		"Displays PI Agreement"
	})
	public static boolean SHOW_PI_AGREEMENT;
	
	@ConfigField(name = "AcceptNewGameServer", value = "true", comment =
	{
		"If set to True any GameServer can register on your login's free slots"
	}, reloadable = false)
	public static boolean ACCEPT_NEW_GAMESERVER;
	
	@ConfigField(name = "AutoCreateAccounts", value = "true")
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	@ConfigField(name = "AutoServerLogin", value = "False")
	public static AutoLoginType AUTO_SERVER_LOGIN;
	
	@ConfigField(name = "EnableFloodProtection", value = "true", comment =
	{
		"Flood Protection. All values are in MILISECONDS."
	})
	public static boolean FLOOD_PROTECTION;
	
	@ConfigField(name = "FastConnectionLimit", value = "15")
	public static int FAST_CONNECTION_LIMIT;
	
	@ConfigField(name = "NormalConnectionTime", value = "700")
	public static int NORMAL_CONNECTION_TIME;
	
	@ConfigField(name = "FastConnectionTime", value = "350")
	public static int FAST_CONNECTION_TIME;
	
	@ConfigField(name = "MaxConnectionPerIP", value = "50")
	public static int MAX_CONNECTION_PER_IP;
	
	@ConfigField(name = "LoginRestartSchedule", value = "false", comment =
	{
		"Enable disable scheduled login restart."
	})
	public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
	
	@ConfigField(name = "LoginRestartTime", value = "24", comment =
	{
		"Time in hours."
	})
	public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;
	
	@ConfigField(name = "LoginTryBeforeBan", value = "5", comment =
	{
		"How many times you can provide an invalid account/pass before the IP gets banned."
	})
	public static int LOGIN_TRY_BEFORE_BAN;
	
	@ConfigField(name = "LoginBlockAfterBan", value = "900", comment =
	{
		"Time you won't be able to login back again after LoginTryBeforeBan tries to login."
	})
	public static int LOGIN_BLOCK_AFTER_BAN;
	
	@ConfigField(name = "DatapackRoot", value = ".", comment =
	{
		"Datapack root directory.",
		"Defaults to current directory from which the server is started."
	}, reloadable = false)
	public static Path DATAPACK_ROOT;
	
	public enum AutoLoginType
	{
		True,
		False,
		Auto
	}
	
	@ConfigGroupBeginning(name = "Threads")
	@ConfigField(name = "ScheduledThreadPoolSize", value = "-1", comment =
	{
		"Specifies how many threads will be in scheduled thread pool.",
		"If set to -1 (which is recommended), the server will decide the amount depending on the available processors"
	})
	public static int SCHEDULED_THREAD_POOL_SIZE;
	
	@ConfigGroupEnding(name = "Threads")
	@ConfigField(name = "InstantThreadPoolSize", value = "-1", comment =
	{
		"Specifies how many threads will be in instant thread pool.",
		"If set to -1 (which is recommended), the server will decide the amount depending on the available processors"
	})
	public static int INSTANT_THREAD_POOL_SIZE;
}
