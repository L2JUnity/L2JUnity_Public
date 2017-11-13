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
package org.l2junity.gameserver.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.config.annotation.ConfigGroupBeginning;
import org.l2junity.commons.config.annotation.ConfigGroupEnding;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Server")
public final class ServerConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);
	
	@ConfigGroupBeginning(name = "Network")
	@ConfigField(name = "EnableUPnP", value = "false", comment =
	{
		"Enables automatic port mapping for telnet server.",
		"If you have a router telnet server will request for port forwarding."
	}, reloadable = false)
	public static boolean ENABLE_UPNP;
	
	@ConfigField(name = "GameserverHostname", value = "0.0.0.0", comment =
	{
		"Bind address for gameserver. You should not need to change it in most cases.",
		"WARNING: <u><b><font color='red'>Please don't change default IPs here if you don't know what are you doing!</font></b></u>",
		"WARNING: <u><b><font color='red'>External/Internal IPs are now inside 'ipconfig.xml' file.</font></b></u>"
	}, reloadable = false)
	public static String GAMESERVER_HOSTNAME;
	
	@ConfigField(name = "GameserverPort", value = "7777", reloadable = false)
	public static int PORT_GAME;
	
	@ConfigField(name = "MaximumOnlineUsers", value = "100", comment =
	{
		"Define how many players are allowed to play simultaneously on your server."
	}, reloadable = false)
	public static int MAXIMUM_ONLINE_USERS;
	
	@ConfigField(name = "LoginPort", value = "9014", comment =
	{
		"TCP port the login server listen to for gameserver connection requests"
	}, reloadable = false)
	public static int GAME_SERVER_LOGIN_PORT;
	
	@ConfigField(name = "LoginHost", value = "127.0.0.1", comment =
	{
		"Where's the Login server this gameserver should connect to",
		"WARNING: <u><b><font color='red'>Please don't change default IPs here if you don't know what are you doing!</font></b></u>",
		"WARNING: <u><b><font color='red'>External/Internal IPs are now inside 'ipconfig.xml' file.</font></b></u>"
	}, reloadable = false)
	public static String GAME_SERVER_LOGIN_HOST;
	
	@ConfigGroupEnding(name = "Network")
	@ConfigField(name = "AllowedProtocolRevisions", value = "110", comment =
	{
		"Numbers of protocol revisions that server allows to connect.",
		"WARNING: <u><b><font color=\"red\">Changing the protocol revision may result in incompatible communication and many errors in game!</font></b></u>",
	})
	public static List<Integer> PROTOCOL_LIST;
	
	@ConfigGroupBeginning(name = "Database")
	@ConfigField(name = "URL", value = "jdbc:mysql://localhost/l2jgs?useSSL=false&serverTimezone=Europe/Paris", comment =
	{
		"Database URL",
		"URL = jdbc:mysql://localhost/l2jgs?useSSL=false&serverTimezone=Europe/Paris (default)",
		"URL = jdbc:hsqldb:hsql://localhost/l2jgs",
		"URL = jdbc:sqlserver://localhost/database = l2jgs/user = sa/password = ",
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
	
	@ConfigField(name = "MaximumDbConnections", value = "100", reloadable = false)
	public static int DATABASE_MAX_CONNECTIONS;
	
	@ConfigField(name = "MaximumDbIdleTime", value = "0", reloadable = false)
	public static int DATABASE_MAX_IDLE_TIME;
	
	@ConfigGroupEnding(name = "Database")
	@ConfigField(name = "ConnectionCloseTime", value = "60000", comment =
	{
		"Connection close time."
	}, reloadable = false)
	public static long CONNECTION_CLOSE_TIME;
	
	@ConfigField(name = "CnameTemplate", value = ".*", comment =
	{
		"Character name template.",
		"Examples:",
		"CnameTemplate = [A-Z][a-z]{3,3}[A-Za-z0-9]*",
		"The above setting will allow names with first capital letter, next three small letters,",
		"and any letter (case insensitive) or number, like OmfgWTF1",
		"CnameTemplate = [A-Z][a-z]*",
		"The above setting will allow names only of letters with first one capital, like Omfgwtf",
		"The .* (allows any symbol)",
	})
	public static Pattern CHARNAME_TEMPLATE_PATTERN;
	
	@ConfigField(name = "PetNameTemplate", value = ".*", comment =
	{
		"This setting restricts names players can give to their pets.",
		"See CnameTemplate for details"
	})
	public static String PET_NAME_TEMPLATE;
	
	@ConfigField(name = "ClanNameTemplate", value = ".*", comment =
	{
		"This setting restricts clan/subpledge names players can set.",
		"See CnameTemplate for details"
	})
	public static String CLAN_NAME_TEMPLATE;
	
	@ConfigField(name = "CharMaxNumber", value = "7", comment =
	{
		"Maximum number of characters per account."
	})
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	
	@ConfigField(name = "AcceptAlternateID", value = "true", comment =
	{
		"True = The Login Server will give an other ID to the server if the requested ID is already reserved."
	}, reloadable = false)
	public static boolean ACCEPT_ALTERNATE_ID;
	
	@ConfigField(name = "RequestServerID", value = "1", comment =
	{
		"This is the server ID that the Game Server will request.",
		"Example: 1 = Bartz"
	}, reloadable = false)
	public static int REQUEST_ID;
	
	@ConfigField(name = "DatapackRoot", value = ".", comment =
	{
		"Datapack root directory.",
		"Defaults to current directory from which the server is started unless the below line is uncommented.",
		"WARNING: <u><b><font color='red'>If the specified path is invalid, it will lead to multiple errors!</font></b></u>"
	}, reloadable = false)
	public static Path DATAPACK_ROOT;
	
	@ConfigField(name = "MultiThreadedLoading", value = "true", comment =
	{
		"If true, multithreaded loading is used",
	}, reloadable = false)
	public static boolean MULTI_THREADED_LOADING;
	
	/**
	 * Creates a path that points to a resource in the datapack.<BR>
	 * <BR>
	 * The path is {@link #DATAPACK_ROOT}/data/[{@code elements}].<BR>
	 * The root may be accessed by pasing {@code ..} as the first element.<BR>
	 * <BR>
	 * This method does not access the file system.
	 * @param elements file tree nodes relative to the datapack location
	 * @return path to a datapack resource
	 */
	public static final Path getDatapackResource(String... elements)
	{
		return BasePathProvider.resolveDatapackPath(DATAPACK_ROOT, Paths.get("data", elements));
	}
	
	// hardcoded
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	
	// hardcoded
	public static boolean CHECK_KNOWN = false;
	
	@ConfigGroupBeginning(name = "Override System")
	@ConfigField(name = "ServersPath", value = "servers", comment =
	{
		"The path where servers will be.",
		"Example: gameserver/<path where servers will be>/${ServerName}/data/stats/skills/super_haste.xml",
	}, reloadable = false)
	public static String SERVERS_PATH;
	
	@ConfigField(name = "ServerName", value = "MyL2JUnityServer", comment =
	{
		"Please provide the name of your server.",
		"It is going to be used for server-name based override system's path calculation. The system provides multiply server management possibility for one GIT repository.",
		"According to the example provided above, in case you want to override a skill, your path will look like:",
		"gameserver/${ServersPath}/${ServerName}/data/stats/skills/super_haste.xml",
	}, reloadable = false)
	public static String SERVER_NAME;
	
	public enum OverrideMode
	{
		INCREMENTAL,
		OVERLAYING;
	}
	
	@ConfigGroupEnding(name = "Override System")
	@ConfigField(name = "DefaultOverrideMode", value = "INCREMENTAL", comment =
	{
		"It either is INCREMENTAL or OVERLAYING.",
		" INCREMENTAL = default files will be loaded along with custom files on an incremental way.",
		" OVERLAYING = custom files will be loaded instead of default files when they are overlaying.",
		"",
		"This config is used when you haven't specified an override mode for a DataHolder. This is the default standard.",
		"If you want to specify an override mode for a DataHolder, all you need to do is to add properties like:",
		"OverrideModeSimpleClassNameOfTheDataHolder = WHATEVER",
		"Here are few examples:",
		" OverrideModeSkillData = OVERLAYING",
		" OverrideModeMultisellData = OVERLAYING",
		" OverrideModeRecipeData = INCREMENTAL",
		"",
		"According to this example above, skills and multisell will use overlaying mode, but RecipeData will use incremental mode.",
	}, reloadable = false)
	public static OverrideMode DEFAULT_OVERRIDE_MODE;
	
	private static final String OVERRIDE_MODE_PREFIX = "OverrideMode";
	public static final Map<String, OverrideMode> OVERRIDE_MODES = new HashMap<>();
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		// Load DataHolder's simple class name based override configurations.
		final Properties props = propertiesOf(properties, override);
		props.entrySet().forEach(entry ->
		{
			final String key = String.valueOf(entry.getKey());
			final String value = String.valueOf(entry.getValue());
			if (key.startsWith(OVERRIDE_MODE_PREFIX))
			{
				try
				{
					OVERRIDE_MODES.put(key.replace(OVERRIDE_MODE_PREFIX, ""), OverrideMode.valueOf(value.toUpperCase()));
				}
				catch (Exception e)
				{
					LOGGER.warn("On property key " + key + " invalid OverrideMode is specified by user!", e);
				}
			}
		});
	}
}
