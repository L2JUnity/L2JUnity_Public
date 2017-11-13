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
package org.l2junity.scripts.handlers.effecthandlers.tick;

import org.l2junity.gameserver.enums.StatModifierType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Mana Heal Over Time effect implementation.
 */
public final class TickMp extends AbstractEffect
{
	private final double _power;
	private final StatModifierType _mode;
	
	public TickMp(StatsSet params)
	{
		_power = params.getDouble("power");
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void tick(Creature caster, Creature target, Skill skill)
	{
		if (target.isDead())
		{
			return;
		}
		
		double mp = target.getCurrentMp();
		double maxMp = target.getMaxRecoverableMp();
		double power = 0;
		
		switch (_mode)
		{
			case DIFF:
			{
				power = _power * getTicksMultiplier();
				break;
			}
			case PER:
			{
				power = mp * _power * getTicksMultiplier();
				break;
			}
		}
		
		if ((power > 0) && (mp > maxMp))
		{
			return;
		}
		
		if ((power < 0) && ((mp + power) <= 0))
		{
			power = -mp;
		}
		
		target.setCurrentMp(Math.min(target.getCurrentMp() + power, maxMp));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("t_mp", TickMp::new);
	}
}
