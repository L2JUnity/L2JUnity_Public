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
package org.l2junity.scripts.ai.uncategorized;

import java.util.ArrayList;
import java.util.List;

import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2junity.gameserver.model.variables.PlayerVariables;
import org.l2junity.gameserver.network.client.send.ability.ExAcquireAPSkillList;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * @author Sdw
 */
public class AbilityPointsReward extends AbstractNpcAI
{
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getActiveChar();
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((oldLevel > newLevel) || (newLevel < 85) || (player.isSubClassActive() && !player.isDualClassActive()) || (player.getAbilityPoints() >= PlayerConfig.ABILITY_MAX_POINTS))
		{
			return;
		}
		
		final List<Integer> handleLevel = player.getVariables().getList(player.isDualClassActive() ? PlayerVariables.ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED : PlayerVariables.ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED, Integer.class, new ArrayList<>());
		if (!handleLevel.contains(newLevel))
		{
			handleLevel.add(newLevel);
			player.getVariables().set(player.isDualClassActive() ? PlayerVariables.ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED : PlayerVariables.ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED, handleLevel);
			player.setAbilityPoints(player.getAbilityPoints() + 1);
			player.broadcastUserInfo();
			player.sendPacket(new ExAcquireAPSkillList(player));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getActiveChar();
		if ((player.getLevel() < 85) || (player.isSubClassActive() && !player.isDualClassActive()))
		{
			return;
		}
		
		final List<Integer> handleLevel = player.getVariables().getList(player.isDualClassActive() ? PlayerVariables.ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED : PlayerVariables.ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED, Integer.class, new ArrayList<>());
		
		int abilityPointsToAdd = 0;
		
		for (int i = 85; i <= player.getLevel(); i++)
		{
			if (!handleLevel.contains(i))
			{
				handleLevel.add(i);
				abilityPointsToAdd++;
			}
		}
		
		player.getVariables().set(player.isDualClassActive() ? PlayerVariables.ABILITY_POINTS_DUAL_CLASS_LEVEL_HANDLED : PlayerVariables.ABILITY_POINTS_MAIN_CLASS_LEVEL_HANDLED, handleLevel);
		player.setAbilityPoints(player.getAbilityPoints() + abilityPointsToAdd);
		player.broadcastUserInfo();
		player.sendPacket(new ExAcquireAPSkillList(player));
	}
	
	@GameScript
	public static void main()
	{
		new AbilityPointsReward();
	}
}
