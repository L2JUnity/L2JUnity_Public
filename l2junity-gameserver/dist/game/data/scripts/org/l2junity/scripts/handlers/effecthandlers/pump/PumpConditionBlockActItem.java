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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Block Actions except item id effect implementation.
 * @author Sdw
 */
public final class PumpConditionBlockActItem extends AbstractEffect
{
	private final Set<Integer> _allowedItems;
	
	public PumpConditionBlockActItem(StatsSet params)
	{
		_allowedItems = Arrays.stream(params.getString("allowedItems", "").split(";")).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.stopActions();
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		target.getStat().set(BooleanStat.BLOCK_ACTIONS);
		_allowedItems.stream().forEach(target.getStat()::addBlockActionsAllowedItem);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BLOCK_ACTIONS;
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_condition_block_act_item", PumpConditionBlockActItem::new);
	}
}
