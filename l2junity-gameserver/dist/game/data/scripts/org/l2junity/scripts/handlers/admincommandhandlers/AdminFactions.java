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

import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.l2junity.gameserver.cache.HtmCache;
import org.l2junity.gameserver.enums.Faction;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Admin Factions manage admin commands.
 * @author St3eT
 */
public final class AdminFactions implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_factions",
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_factions"))
		{
			if (st.hasMoreTokens())
			{
				final Faction faction = Faction.valueOf(st.nextToken());
				final String action = st.nextToken();
				
				final PlayerInstance target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens())
				{
					return false;
				}
				
				int value = 0;
				try
				{
					value = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					showMenuHtml(activeChar);
					activeChar.sendMessage("Invalid Value!");
					return false;
				}
				
				switch (action)
				{
					case "increase":
					{
						activeChar.addFactionPoints(faction, value);
						target.sendMessage("Admin increase your Faction " + faction + " Point(s) by " + value + "!");
						activeChar.sendMessage("You increased Faction " + faction + " Point(s) of " + target.getName() + " by " + value);
						break;
					}
					case "decrease":
					{
						activeChar.removeFactionPoints(faction, value);
						target.sendMessage("Admin decreased your Faction " + faction + " Point(s) by " + value + "!");
						activeChar.sendMessage("You decreased Faction " + faction + " Point(s) of " + target.getName() + " by " + value);
						break;
					}
					case "rewardOnline":
					{
						int range = 0;
						try
						{
							range = Integer.parseInt(st.nextToken());
						}
						catch (Exception e)
						{
							
						}
						
						if (range <= 0)
						{
							final int count = increaseForAll(World.getInstance().getPlayers(), faction, value);
							activeChar.sendMessage("You increased Faction " + faction + " Point(s) of all online players (" + count + ") by " + value + ".");
						}
						else if (range > 0)
						{
							final int count = increaseForAll(World.getInstance().getVisibleObjects(activeChar, PlayerInstance.class, range), faction, value);
							activeChar.sendMessage("You increased Faction " + faction + " Point(s) of all players (" + count + ") in range " + range + " by " + value + ".");
						}
						break;
					}
				}
				showMenuHtml(activeChar);
			}
			else
			{
				showMenuHtml(activeChar);
			}
		}
		return true;
	}
	
	private int increaseForAll(Collection<PlayerInstance> playerList, Faction faction, int value)
	{
		int counter = 0;
		for (PlayerInstance temp : playerList)
		{
			if ((temp != null) && (temp.isOnlineInt() == 1))
			{
				temp.addFactionPoints(faction, value);
				temp.sendMessage("Admin increase your Faction " + faction + " Point(s) by " + value + "!");
				counter++;
			}
		}
		return counter;
	}
	
	private PlayerInstance getTarget(PlayerInstance activeChar)
	{
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private void showMenuHtml(PlayerInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		final PlayerInstance target = getTarget(activeChar);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/factions.htm"));
		html.replace("%targetName%", target.getName());
		
		final StringBuilder sb = new StringBuilder();
		target.getFactionsPoints().entrySet().forEach(entry ->
		{
			sb.append("<tr><td>");
			sb.append(entry.getKey());
			sb.append("</td><td>");
			sb.append(entry.getValue());
			sb.append("</td></tr>");
		});
		html.replace("%currentPoints%", sb.toString());
		
		html.replace("%factions%", Arrays.stream(Faction.values()).map(Faction::name).collect(Collectors.joining(";")));
		
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
		AdminCommandHandler.getInstance().registerHandler(new AdminFactions());
	}
}