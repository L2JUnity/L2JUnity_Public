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

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Lethal effect implementation.
 * @author Adry_85
 */
public final class InstantDeath extends AbstractEffect
{
	private final double _fullLethal;
	private final double _halfLethal;
	
	public InstantDeath(StatsSet params)
	{
		_fullLethal = params.getDouble("fullLethal");
		_halfLethal = params.getDouble("halfLethal");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (caster.isPlayer() && !caster.getAccessLevel().canGiveDamage())
		{
			return;
		}
		
		if (skill.getMagicLevel() < (targetCreature.getLevel() - 6))
		{
			return;
		}
		
		if (!targetCreature.isLethalable() || targetCreature.isHpBlocked())
		{
			return;
		}
		
		if (caster.isPlayer() && targetCreature.isPlayer() && targetCreature.getStat().has(BooleanStat.FACE_OFF) && (targetCreature.asPlayer().getAttackerObjId() != caster.getObjectId()))
		{
			return;
		}
		
		double chanceMultiplier = Formulas.calcAttributeBonus(caster, targetCreature, skill) * Formulas.calcGeneralTraitBonus(caster, targetCreature, skill.getTraitType(), false) * targetCreature.getStat().getValue(DoubleStat.INSTANT_KILL_RESIST, 1);
		// Lethal Strike
		if (Rnd.get(100) < (_fullLethal * chanceMultiplier))
		{
			// for Players CP and HP is set to 1.
			if (targetCreature.isPlayer())
			{
				targetCreature.setCurrentCp(1);
				targetCreature.setCurrentHp(1);
				targetCreature.sendPacket(SystemMessageId.LETHAL_STRIKE);
			}
			// for Monsters HP is set to 1.
			else if (targetCreature.isMonster() || targetCreature.isSummon())
			{
				targetCreature.setCurrentHp(1);
			}
			caster.sendPacket(SystemMessageId.HIT_WITH_LETHAL_STRIKE);
		}
		// Half-Kill
		else if (Rnd.get(100) < (_halfLethal * chanceMultiplier))
		{
			// for Players CP is set to 1.
			if (targetCreature.isPlayer())
			{
				targetCreature.setCurrentCp(1);
				targetCreature.sendPacket(SystemMessageId.HALF_KILL);
				targetCreature.sendPacket(SystemMessageId.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_HALF_KILL_SKILL);
			}
			// for Monsters HP is set to 50%.
			else if (targetCreature.isMonster() || targetCreature.isSummon())
			{
				targetCreature.setCurrentHp(targetCreature.getCurrentHp() * 0.5);
			}
			caster.sendPacket(SystemMessageId.HALF_KILL);
		}
		
		// No matter if lethal succeeded or not, its reflected.
		Formulas.calcCounterAttack(caster, targetCreature, skill, false);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_death", InstantDeath::new);
	}
}
