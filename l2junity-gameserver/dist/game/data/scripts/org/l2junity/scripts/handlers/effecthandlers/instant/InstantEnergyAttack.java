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
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Energy Attack effect implementation.
 * @author NosBit
 */
public final class InstantEnergyAttack extends AbstractEffect
{
	private final double _power;
	private final int _criticalChance;
	private final boolean _ignoreShieldDefence;
	private final boolean _overHit;
	private final double _pDefMod;
	
	public InstantEnergyAttack(StatsSet params)
	{
		_power = params.getDouble("power");
		_criticalChance = params.getInt("criticalChance", 0);
		_ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
		_overHit = params.getBoolean("overHit", false);
		_pDefMod = params.getDouble("pDefMod", 1.0);
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		// TODO: Verify this on retail
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
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (casterPlayer.distance3d(target) > targetCreature.getStat().getValue(DoubleStat.SPHERIC_BARRIER_RANGE, Integer.MAX_VALUE))
		{
			return;
		}
		
		if (_overHit && targetCreature.isAttackable())
		{
			targetCreature.asAttackable().overhitEnabled(true);
		}
		
		double defence = targetCreature.getPDef() * _pDefMod;
		if (!_ignoreShieldDefence)
		{
			byte shield = Formulas.calcShldUse(casterPlayer, targetCreature);
			switch (shield)
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
			final double weaponTraitMod = Formulas.calcWeaponTraitBonus(casterPlayer, targetCreature);
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(casterPlayer, targetCreature, skill.getTraitType(), true);
			final double attributeMod = Formulas.calcAttributeBonus(casterPlayer, targetCreature, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(casterPlayer, targetCreature, skill, true);
			
			// Skill specific mods.
			final double energyChargesBoost = 1 + (Math.min(skill.getChargeConsume(), casterPlayer.getCharges()) * 0.1); // 10% bonus damage for each charge used.
			final double critMod = critical ? (2 * Formulas.calcCritDamage(casterPlayer, targetCreature, skill)) : 1;
			final double ssmod = (skill.useSoulShot() && casterPlayer.isChargedShot(ShotType.SOULSHOTS)) ? (2 * casterPlayer.getStat().getValue(DoubleStat.SOULSHOTS_BONUS)) : 1; // 2.04 for dual weapon?
			
			// ...................________Initial Damage_________...__Charges Additional Damage__...____________________________________
			// ATTACK CALCULATION ((77 * ((pAtk * lvlMod) + power) * (1 + (0.1 * chargesConsumed)) / pdef) * skillPower) + skillPowerAdd
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (77 * ((casterPlayer.getPAtk() * casterPlayer.getLevelMod()) + _power)) / defence;
			damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * attributeMod * energyChargesBoost * pvpPveMod;
			damage = casterPlayer.getStat().getValue(DoubleStat.PHYSICAL_SKILL_POWER, damage);
		}
		
		damage = Math.max(0, damage);
		
		casterPlayer.doAttack(damage, targetCreature, skill, false, false, critical, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_energy_attack", InstantEnergyAttack::new);
	}
}