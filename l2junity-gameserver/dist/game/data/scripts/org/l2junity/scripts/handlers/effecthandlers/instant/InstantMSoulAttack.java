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
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Magical Soul Attack effect implementation.
 * @author Adry_85
 */
public final class InstantMSoulAttack extends AbstractEffect
{
	private final double _power;
	
	public InstantMSoulAttack(StatsSet params)
	{
		_power = params.getDouble("power");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MAGICAL_ATTACK;
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
		
		if (casterPlayer.isAlikeDead())
		{
			return;
		}
		
		if (targetCreature.isPlayer() && targetCreature.asPlayer().isFakeDeath())
		{
			targetCreature.asPlayer().stopFakeDeath(true);
		}
		
		boolean sps = skill.useSpiritShot() && casterPlayer.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && casterPlayer.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), casterPlayer, targetCreature, skill);
		final double mAtk = casterPlayer.getMAtk() * Formulas.calcSoulBonus(casterPlayer, skill);
		double damage = Formulas.calcMagicDam(casterPlayer, targetCreature, skill, mAtk, _power, targetCreature.getMDef(), sps, bss, mcrit);
		
		casterPlayer.doAttack(damage, targetCreature, skill, false, false, mcrit, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_m_soul_attack", InstantMSoulAttack::new);
	}
}