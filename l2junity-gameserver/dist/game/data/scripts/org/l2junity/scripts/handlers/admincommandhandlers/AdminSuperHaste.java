/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.scripts.handlers.admincommandhandlers;

import java.util.StringTokenizer;

import org.l2junity.gameserver.data.xml.impl.SkillData;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * The classical custom L2J implementation of the old //gmspeed GM command.
 * @author lord_rex (No, it wasn't me at all. Eclipse added my name there.)
 */
public final class AdminSuperHaste implements IAdminCommandHandler
{
	public static final String[] ADMIN_COMMANDS =
	{
		"admin_superhaste",
		"admin_superhaste_menu",
		"admin_speed",
		"admin_speed_menu",
	};
	
	private static final int SUPER_HASTE_ID = 7029;
	
	@Override
	public boolean useAdminCommand(final String command, final PlayerInstance player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_superhaste":
			case "admin_speed":
			{
				try
				{
					final int val = Integer.parseInt(st.nextToken());
					final boolean sendMessage = player.isAffectedBySkill(SUPER_HASTE_ID);
					player.stopSkillEffects((val == 0) && sendMessage, SUPER_HASTE_ID);
					if ((val >= 1) && (val <= 4))
					{
						int time = 0;
						if (st.hasMoreTokens())
						{
							time = Integer.parseInt(st.nextToken());
						}
						
						final Skill superHasteSkill = SkillData.getInstance().getSkill(SUPER_HASTE_ID, val);
						superHasteSkill.applyEffects(player, player, true, time);
					}
				}
				catch (final Exception e)
				{
					player.sendMessage("Usage: //superhaste <Effect level (0-4)> <Time in seconds>");
				}
				
				break;
			}
			
			case "admin_superhaste_menu":
			case "admin_speed_menu":
			{
				AdminHtml.showAdminHtml(player, "gm_menu.htm");
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminSuperHaste());
	}
}
