/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.admincommandhandlers;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import org.l2junity.commons.config.ConfigManager;
import org.l2junity.commons.loader.LoadHolder;
import org.l2junity.gameserver.GameServer;
import org.l2junity.gameserver.cache.HtmCache;
import org.l2junity.gameserver.config.GameConfigMarker;
import org.l2junity.gameserver.data.sql.impl.CrestTable;
import org.l2junity.gameserver.data.xml.impl.AdminData;
import org.l2junity.gameserver.data.xml.impl.EnchantItemGroupsData;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NosBit
 */
public final class AdminReload implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminReload.class);

	private static final String[] ADMIN_COMMANDS =
			{
					"admin_reload"
			};

	private static final String RELOAD_USAGE;
	static
	{
		final StringJoiner sj = new StringJoiner("|", "Usage: //reload <", ">");
		sj.add("config");
		sj.add("quest");
		sj.add("html_file");
		sj.add("crest");
		sj.add("enchant");
		GameServer.getInstance().getLoader().getReloads().keySet().forEach(sj::add);
		RELOAD_USAGE = sj.toString();
	}

	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if (actualCommand.equalsIgnoreCase("admin_reload"))
		{
			if (!st.hasMoreTokens())
			{
				showPage(activeChar, "data/html/admin/reload.htm");
				activeChar.sendMessage(RELOAD_USAGE);
				return true;
			}

			final String type = st.nextToken().toLowerCase();
			switch (type)
			{
				case "config":
				{
					ConfigManager.getInstance().reload(GameConfigMarker.class.getPackage().getName());
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
					break;
				}
				case "quest":
				{
					if (st.hasMoreElements())
					{
						String value = st.nextToken();
						if (!Util.isDigit(value))
						{
							QuestManager.getInstance().reload(value);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
						}
						else
						{
							final int questId = Integer.parseInt(value);
							QuestManager.getInstance().reload(questId);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
						}
					}
					else
					{
						QuestManager.getInstance().reloadAllScripts();
						activeChar.sendMessage("All scripts have been reloaded.");
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
					}
					break;
				}
				case "html_file":
				{
					if (st.hasMoreElements())
					{
						final String path = st.nextToken();
						final Path file = HtmCache.getInstance().getHtmFilePath(path);
						if (Files.exists(file))
						{
							HtmCache.getInstance().loadFile(file);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded HTML File: '" + file + "'.");
						}
						else
						{
							activeChar.sendMessage("File or Directory does not exist.");
						}
					}
					break;
				}
				case "crest":
				{
					CrestTable.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
					break;
				}
				case "enchant":
				{
					try
					{
						EnchantItemGroupsData.getInstance().load();
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item enchanting data.");
					}
					catch (Exception e)
					{
						LOGGER.warn("Failed reloading item enchanting data!", e);
						activeChar.sendMessage("Error reloading item enchanting data!");
					}
					break;
				}
				default:
				{
					final LoadHolder loadHolder = GameServer.getInstance().getLoader().getReloads().get(type);
					if (loadHolder != null)
					{
						try
						{
							loadHolder.call();
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded " + type + ".");
						}
						catch (InvocationTargetException | IllegalAccessException e)
						{
							LOGGER.warn("Reloading " + type + " failed.", e);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Failed reloading " + type + ".");
						}
						break;
					}

					activeChar.sendMessage(RELOAD_USAGE);
					return true;
				}
			}
			activeChar.sendMessage("WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
		}
		return true;
	}

	private static void showPage(PlayerInstance activeChar, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), filename);
		final StringJoiner sj = new StringJoiner(";");
		GameServer.getInstance().getLoader().getReloads().keySet().forEach(sj::add);
		html.replace("%reloadables%", sj.toString());
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminReload());
	}
}