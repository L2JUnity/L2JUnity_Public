/*
 * Copyright (C) 2004-2013 L2J Unity
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
package org.l2junity.scripts.handlers.telnethandlers.server;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

import org.l2junity.commons.config.ConfigManager;
import org.l2junity.commons.loader.LoadHolder;
import org.l2junity.gameserver.GameServer;
import org.l2junity.gameserver.cache.HtmCache;
import org.l2junity.gameserver.config.GameConfigMarker;
import org.l2junity.gameserver.data.sql.impl.CrestTable;
import org.l2junity.gameserver.data.xml.impl.AdminData;
import org.l2junity.gameserver.data.xml.impl.EnchantItemGroupsData;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.network.telnet.ITelnetCommand;
import org.l2junity.gameserver.network.telnet.TelnetServer;
import org.l2junity.gameserver.scripting.ScriptsManager;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class Reload implements ITelnetCommand
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Reload.class);

	private Reload()
	{
	}

	@Override
	public String getCommand()
	{
		return "reload";
	}

	@Override
	public String getUsage()
	{
		final StringJoiner sj = new StringJoiner("|", "Reload <", ">");
		sj.add("config");
		sj.add("quest");
		sj.add("html");
		sj.add("item");
		sj.add("crest");
		sj.add("effect");
		sj.add("enchant");
		sj.add("script");
		GameServer.getInstance().getLoader().getReloads().keySet().forEach(sj::add);
		return sj.toString();
	}

	@Override
	public String handle(String ipAddress, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		switch (args[0])
		{
			case "config":
			{
				// Load Configs
				ConfigManager.getInstance().reload(GameConfigMarker.class.getPackage().getName());
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Configs.");
			}
			case "quest":
			{
				if (args.length > 1)
				{
					String value = args[1];
					if (!Util.isDigit(value))
					{
						QuestManager.getInstance().reload(value);
						return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quest Name:" + value + ".");
					}
					final int questId = Integer.parseInt(value);
					QuestManager.getInstance().reload(questId);
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quest ID:" + questId + ".");
				}
				QuestManager.getInstance().reloadAllScripts();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quests.");
			}
			case "htm":
			case "html":
			{
				if (args.length > 1)
				{
					final String path = args[1];
					final Path file = HtmCache.getInstance().getHtmFilePath(path);
					if (Files.exists(file))
					{
						HtmCache.getInstance().loadFile(file);
						return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded HTML File: '" + file + "'.");
					}
					return "File or Directory does not exist.";
				}
				HtmCache.getInstance().load();
				AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Htms.");
				return "Cache[HTML]: " + HtmCache.getInstance().toString();
			}
			case "item":
			{
				ItemTable.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Items.");
			}
			case "crest":
			{
				CrestTable.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Crests.");
			}
			case "enchant":
			{
				try
				{
					EnchantItemGroupsData.getInstance().load();
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded item enchanting data.");
				}
				catch (Exception e)
				{
					LOGGER.warn("Failed reloading item enchanting data!", e);
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloading item enchanting data failed.");
				}
			}
			case "script":
			{
				if (args.length < 2)
				{
					return "Syntax: reload script <path>";
				}
				try
				{
					ScriptsManager.getInstance().runScript(args[1], GameScript.class);
					return "Script " + args[1] + " has been reloaded successfuly.";
				}
				catch (Exception e)
				{
					return "Couldn't reload script: " + args[1] + " err: " + e.getMessage();
				}
			}
			default:
			{
				final LoadHolder loadHolder = GameServer.getInstance().getLoader().getReloads().get(args[0]);
				if (loadHolder != null)
				{
					try
					{
						loadHolder.call();
						AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded " + args[0] + ".");
					}
					catch (InvocationTargetException | IllegalAccessException e)
					{
						LOGGER.warn("Reloading " + args[0] + " failed.", e);
						AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Failed reloading " + args[0] + ".");
					}
				}
			}
		}
		return null;
	}

	@GameScript
	public static void main()
	{
		TelnetServer.getInstance().addHandler(new Reload());
	}
}