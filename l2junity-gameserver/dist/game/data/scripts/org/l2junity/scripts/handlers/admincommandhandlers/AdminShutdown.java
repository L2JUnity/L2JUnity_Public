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

import java.util.StringTokenizer;

import org.l2junity.commons.lang.management.ShutdownManager;
import org.l2junity.commons.lang.management.TerminationStatus;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.instancemanager.GameTimeManager;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * This class handles following admin commands: - server_shutdown [sec] = shows menu or shuts down server in sec seconds
 */
public class AdminShutdown implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_server_shutdown",
		"admin_server_restart",
		"admin_server_abort"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (command.startsWith("admin_server_shutdown"))
		{
			start(command, activeChar, TerminationStatus.MANUAL_SHUTDOWN);
		}
		else if (command.startsWith("admin_server_restart"))
		{
			start(command, activeChar, TerminationStatus.MANUAL_RESTART);
		}
		else if (command.startsWith("admin_server_abort"))
		{
			ShutdownManager.abort(activeChar.getName());
		}
		
		return true;
	}
	
	private void start(String command, PlayerInstance player, TerminationStatus mode)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		try
		{
			final int duration = Integer.parseInt(st.nextToken());
			int interval = 0;
			
			try
			{
				interval = Integer.parseInt(st.nextToken());
			}
			catch (final Exception e)
			{
				interval = 1;
			}
			
			ShutdownManager.start(mode, duration, interval, player.getName());
		}
		catch (final Exception e)
		{
			sendHtmlForm(player);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void sendHtmlForm(PlayerInstance activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/shutdown.htm");
		adminReply.replace("%count%", String.valueOf(World.getInstance().getPlayers().size()));
		adminReply.replace("%used%", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		adminReply.replace("%time%", GameTimeManager.getInstance().getGameTime().toString());
		activeChar.sendPacket(adminReply);
	}
	
	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminShutdown());
	}
}