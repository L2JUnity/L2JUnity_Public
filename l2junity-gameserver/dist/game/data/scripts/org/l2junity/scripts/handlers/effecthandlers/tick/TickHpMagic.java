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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * MagicalAttack-damage over time effect implementation.
 * @author Nik
 */
public final class TickHpMagic extends AbstractEffect
{
	private final double _power;
	
	public TickHpMagic(StatsSet params)
	{
		_power = params.getDouble("power");
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void tick(Creature caster, Creature effected, Skill skill)
	{
		final Creature target = effected;
		
		if (target.isDead())
		{
			return;
		}
		
		double damage = Formulas.calcMagicDam(caster, target, skill, caster.getMAtk(), Math.abs(_power), target.getMDef(), false, false, false); // In retail spiritshots change nothing.
		damage *= getTicksMultiplier();
		if (damage >= (effected.getCurrentHp() - 1))
		{
			damage = effected.getCurrentHp() - 1;
		}
		caster.doAttack(damage, effected, skill, true, false, false, false);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("t_hp_magic", TickHpMagic::new);
	}
}
