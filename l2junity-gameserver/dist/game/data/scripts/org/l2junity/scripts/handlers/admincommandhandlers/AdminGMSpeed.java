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

import java.util.EnumSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.l2junity.gameserver.config.AdminConfig;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.gameserver.util.BuilderUtil;
import org.l2junity.gameserver.util.Util;

/**
 * A retail-like implementation of //gmspeed builder command.
 * @author lord_rex
 */
public final class AdminGMSpeed implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gmspeed",
	};
	
	private static final Set<DoubleStat> SPEED_STATS = EnumSet.of(DoubleStat.RUN_SPEED, DoubleStat.WALK_SPEED, DoubleStat.SWIM_RUN_SPEED, DoubleStat.SWIM_WALK_SPEED, DoubleStat.FLY_RUN_SPEED, DoubleStat.FLY_WALK_SPEED);
	
	@Override
	public boolean useAdminCommand(final String command, final PlayerInstance player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_gmspeed":
			{
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
					return false;
				}
				final String token = st.nextToken();
				
				// Rollback feature for old custom way, in order to make everyone happy.
				if (AdminConfig.USE_SUPER_HASTE_AS_GM_SPEED)
				{
					AdminCommandHandler.getInstance().useAdminCommand(player, AdminSuperHaste.ADMIN_COMMANDS[0] + " " + token, false);
					return true;
				}
				
				if (!Util.isDouble(token))
				{
					BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
					return false;
				}
				final double runSpeedBoost = Double.parseDouble(token);
				if ((runSpeedBoost < 0) || (runSpeedBoost > 10))
				{
					// Custom limit according to SDW's request - real retail limit is unknown.
					BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
					return false;
				}
				
				final Creature targetCreature;
				final WorldObject target = player.getTarget();
				if ((target != null) && target.isCreature())
				{
					targetCreature = target.asCreature();
				}
				else
				{
					// If there is no target, let's use the command executer.
					targetCreature = player;
				}
				
				SPEED_STATS.forEach(speedStat -> targetCreature.getStat().removeFixedValue(speedStat));
				if (runSpeedBoost > 0)
				{
					SPEED_STATS.forEach(speedStat -> targetCreature.getStat().addFixedValue(speedStat, targetCreature.getTemplate().getBaseValue(speedStat, 120) * runSpeedBoost));
				}
				
				targetCreature.getStat().recalculateStats(false);
				if (targetCreature.isPlayer())
				{
					targetCreature.asPlayer().broadcastUserInfo();
				}
				else
				{
					targetCreature.broadcastInfo();
				}
				
				BuilderUtil.sendSysMessage(player, "[" + targetCreature.getName() + "] speed is [" + (runSpeedBoost * 100) + "0]% fast.");
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
		AdminCommandHandler.getInstance().registerHandler(new AdminGMSpeed());
	}
}
