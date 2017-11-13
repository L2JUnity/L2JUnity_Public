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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.enums.DispelSlotType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpResistAbnormalByCategory extends AbstractEffect
{
	private final DispelSlotType _slot;
	private final double _amount;
	
	public PumpResistAbnormalByCategory(StatsSet params)
	{
		_amount = params.getDouble("amount");
		_slot = params.getEnum("slot", DispelSlotType.class, DispelSlotType.DEBUFF);
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		switch (_slot)
		{
			// Only this one is in use it seems
			case DEBUFF:
			{
				target.getStat().mergeMul(DoubleStat.RESIST_ABNORMAL_DEBUFF, 1 + (_amount / 100));
				break;
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_resist_abnormal_by_category", PumpResistAbnormalByCategory::new);
	}
}
