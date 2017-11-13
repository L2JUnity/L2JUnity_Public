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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.enums.ShotType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Backstab effect implementation.
 * @author Adry_85
 */
public final class InstantBackstab extends AbstractEffect
{
	private final double _power;
	private final double _chanceBoost;
	private final double _criticalChance;
	private final boolean _overHit;
	
	public InstantBackstab(StatsSet params)
	{
		_power = params.getDouble("power");
		_chanceBoost = params.getDouble("chanceBoost");
		_criticalChance = params.getDouble("criticalChance", 0);
		_overHit = params.getBoolean("overHit", false);
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && !caster.isInFrontOf(target.asCreature()) && !Formulas.calcPhysicalSkillEvasion(caster, target.asCreature(), skill) && Formulas.calcBlowSuccess(caster, target.asCreature(), skill, _chanceBoost);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (caster.isAlikeDead())
		{
			return;
		}
		
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (_overHit && target.isAttackable())
		{
			target.asAttackable().overhitEnabled(true);
		}
		
		boolean ss = skill.useSoulShot() && caster.isChargedShot(ShotType.SOULSHOTS);
		byte shld = Formulas.calcShldUse(caster, targetCreature);
		double damage = Formulas.calcBlowDamage(caster, targetCreature, skill, true, _power, shld, ss);
		
		if (Formulas.calcCrit(_criticalChance, caster, targetCreature, skill))
		{
			damage *= 2;
		}
		
		caster.doAttack(damage, targetCreature, skill, false, true, true, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_backstab", InstantBackstab::new);
	}
}