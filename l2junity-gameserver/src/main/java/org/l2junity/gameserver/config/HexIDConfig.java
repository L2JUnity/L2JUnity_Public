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

import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.l2junity.commons.config.IConfigLoader;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public final class HexIDConfig implements IConfigLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(HexIDConfig.class);
	
	public static final Path HEXID_FILE = BasePathProvider.resolvePath(Paths.get("config", "hexid.txt"));
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	public HexIDConfig()
	{
		// visibility
	}
	
	@Override
	public void load(PropertiesParser override)
	{
		if (Files.exists(HEXID_FILE))
		{
			final PropertiesParser hexId = new PropertiesParser(HEXID_FILE);
			
			if (hexId.containsKey("ServerID") && hexId.containsKey("HexID"))
			{
				SERVER_ID = hexId.getInt("ServerID", 1);
				try
				{
					HEX_ID = new BigInteger(hexId.getString("HexID", null), 16).toByteArray();
				}
				catch (Exception e)
				{
					LOGGER.warn("HexID file format is invalid!", e);
				}
			}
			
			LOGGER.info("HexID file '{}' loaded.", HEXID_FILE);
		}
		
		if (HEX_ID == null)
		{
			LOGGER.warn("Could not load HexID file ({}). Hopefully login will give us one.", HEXID_FILE);
		}
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.<br>
	 * Check {@link HexIDConfig#HEXID_FILE}.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 */
	public static void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 * @param hexidFile name of the L2Properties file
	 */
	public static void saveHexid(int serverId, String hexId, Path hexidFile)
	{
		try
		{
			final Properties hexSetting = new Properties();
			if (!Files.exists(hexidFile))
			{
				Files.createFile(hexidFile);
			}
			
			try (final OutputStream out = Files.newOutputStream(hexidFile))
			{
				hexSetting.setProperty("ServerID", String.valueOf(serverId));
				hexSetting.setProperty("HexID", hexId);
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to save hex id to {} File.", hexidFile, e);
		}
	}
}
