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
 * Magical Attack effect implementation.
 * @author Adry_85
 */
public final class InstantMAttackRange extends AbstractEffect
{
	private final double _power;
	private final double _shieldDefPercent;
	
	public InstantMAttackRange(StatsSet params)
	{
		_power = params.getDouble("power");
		_shieldDefPercent = params.getDouble("shieldDefPercent");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isPlayer() && targetCreature.asPlayer().isFakeDeath())
		{
			targetCreature.asPlayer().stopFakeDeath(true);
		}
		
		double mDef = targetCreature.getMDef();
		switch (Formulas.calcShldUse(caster, targetCreature))
		{
			case Formulas.SHIELD_DEFENSE_SUCCEED:
			{
				mDef += ((targetCreature.getShldDef() * _shieldDefPercent) / 100);
				break;
			}
			case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK:
			{
				mDef = -1;
				break;
			}
		}
		
		double damage = 1;
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), caster, targetCreature, skill);
		if (mDef != -1)
		{
			boolean sps = skill.useSpiritShot() && caster.isChargedShot(ShotType.SPIRITSHOTS);
			boolean bss = skill.useSpiritShot() && caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
			damage = Formulas.calcMagicDam(caster, targetCreature, skill, caster.getMAtk(), _power, mDef, sps, bss, mcrit);
		}
		
		caster.doAttack(damage, targetCreature, skill, false, false, mcrit, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_m_attack_range", InstantMAttackRange::new);
	}
}