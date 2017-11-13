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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.debugger.Debugger;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

public class AdminDebug implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_debug"
	};
	
	@Override
	public final boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_debug":
			{
				if ((activeChar.getTarget() == null) || !activeChar.getTarget().isCreature())
				{
					activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
					break;
				}
				
				final Creature character = (Creature) activeChar.getTarget();
				if (character.isDebug(activeChar))
				{
					character.removeDebugger(activeChar.getObjectId());
					activeChar.sendMessage("Stopped debuggin player: " + character);
					break;
				}
				
				final Debugger debugger = new Debugger(activeChar);
				final List<DebugType> debuggingTypes;
				if (!st.hasMoreTokens())
				{
					debuggingTypes = new ArrayList<>();
					debuggingTypes.add(DebugType.ITEMS);
					debuggingTypes.add(DebugType.SKILLS);
					debuggingTypes.add(DebugType.OPTIONS);
					debuggingTypes.add(DebugType.BYPASSES);
				}
				else
				{
					debuggingTypes = new ArrayList<>();
					while (st.hasMoreTokens())
					{
						final String token = st.nextToken();
						final DebugType type = DebugType.findByName(token);
						if (type != null)
						{
							debuggingTypes.add(type);
						}
						else
						{
							activeChar.sendMessage("Debug type: " + token + " doesn't exists!");
						}
					}
				}
				
				debuggingTypes.forEach(debugger::addDebugType);
				character.addDebugger(debugger);
				activeChar.sendMessage("Debugging " + character + " types: " + debuggingTypes);
				activeChar.sendMessage("Available debug types: " + Arrays.asList(DebugType.values()));
				break;
			}
		}
		return true;
	}
	
	@Override
	public final String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminDebug());
	}
}