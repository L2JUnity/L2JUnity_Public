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
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Physical Attack effect implementation. <br>
 * <b>Note</b>: Initial formula taken from PhysicalAttack.
 * @author Adry_85, Nik
 */
public final class InstantPhysicalAttackSaveHp extends AbstractEffect
{
	private final double _power;
	private final double _criticalChance;
	private final boolean _ignoreShieldDefence;
	private final boolean _overHit;
	private final double _saveHp;
	
	public InstantPhysicalAttackSaveHp(StatsSet params)
	{
		_power = params.getDouble("power");
		_criticalChance = params.getDouble("criticalChance", 0);
		_ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
		_overHit = params.getBoolean("overHit", false);
		_saveHp = params.getDouble("saveHp", 0);
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && !Formulas.calcPhysicalSkillEvasion(caster, target.asCreature(), skill);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (caster.isAlikeDead())
		{
			return;
		}
		
		if (targetCreature.isPlayer() && targetCreature.asPlayer().isFakeDeath())
		{
			targetCreature.asPlayer().stopFakeDeath(true);
		}
		
		if (_overHit && targetCreature.isAttackable())
		{
			targetCreature.asAttackable().overhitEnabled(true);
		}
		
		final double attack = caster.getPAtk();
		double defence = targetCreature.getPDef();
		if (!_ignoreShieldDefence)
		{
			switch (Formulas.calcShldUse(caster, targetCreature))
			{
				case Formulas.SHIELD_DEFENSE_SUCCEED:
				{
					defence += targetCreature.getShldDef();
					break;
				}
				case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK:
				{
					defence = -1;
					break;
				}
			}
		}
		
		double damage = 1;
		final boolean critical = Formulas.calcCrit(_criticalChance, caster, targetCreature, skill);
		
		if (defence != -1)
		{
			// Trait, elements
			final double weaponTraitMod = Formulas.calcWeaponTraitBonus(caster, targetCreature);
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(caster, targetCreature, skill.getTraitType(), true);
			final double attributeMod = Formulas.calcAttributeBonus(caster, targetCreature, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(caster, targetCreature, skill, true);
			final double randomMod = caster.getRandomDamageMultiplier();
			
			// Skill specific mods.
			final double wpnMod = caster.getAttackType().isRanged() ? 70 : (70 * 1.10113);
			final double rangedBonus = caster.getAttackType().isRanged() ? (attack + _power) : 0;
			final double critMod = critical ? (2 * Formulas.calcCritDamage(caster, targetCreature, skill)) : 1;
			final double ssmod = (skill.useSoulShot() && caster.isChargedShot(ShotType.SOULSHOTS)) ? (2 * caster.getStat().getValue(DoubleStat.SOULSHOTS_BONUS)) : 1; // 2.04 for dual weapon?
			
			// ...................____________Melee Damage_____________......................................___________________Ranged Damage____________________
			// ATTACK CALCULATION 77 * ((pAtk * lvlMod) + power) / pdef            RANGED ATTACK CALCULATION 70 * ((pAtk * lvlMod) + power + patk + power) / pdef
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^``````````````````````````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (wpnMod * ((attack * caster.getLevelMod()) + _power + rangedBonus)) / defence;
			damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * attributeMod * pvpPveMod * randomMod;
			damage = caster.getStat().getValue(DoubleStat.PHYSICAL_SKILL_POWER, damage);
		}
		
		final double minHp = (targetCreature.getMaxHp() * _saveHp) / 100;
		
		if ((targetCreature.getCurrentHp() - damage) < minHp)
		{
			damage = targetCreature.getCurrentHp() - minHp;
		}
		
		caster.doAttack(damage, targetCreature, skill, false, false, critical, false);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_p_attack_save_hp", InstantPhysicalAttackSaveHp::new);
	}
}
