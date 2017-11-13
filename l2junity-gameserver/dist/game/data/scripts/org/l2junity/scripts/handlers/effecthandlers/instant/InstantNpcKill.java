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
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Unsummon effect implementation.
 * @author Adry_85
 */
public final class InstantNpcKill extends AbstractEffect
{
	private final int _chance;
	
	public InstantNpcKill(StatsSet params)
	{
		_chance = params.getInt("chance");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		if (!target.isCreature())
		{
			return false;
		}
		else if (_chance < 0)
		{
			return true;
		}
		
		final Creature targetCreature = target.asCreature();
		int magicLevel = skill.getMagicLevel();
		if ((magicLevel <= 0) || ((targetCreature.getLevel() - 9) <= magicLevel))
		{
			double chance = _chance * Formulas.calcAttributeBonus(caster, targetCreature, skill) * Formulas.calcGeneralTraitBonus(caster, targetCreature, skill.getTraitType(), false);
			if ((chance >= 100) || (chance > (Rnd.nextDouble() * 100)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (target.isServitor())
		{
			final Summon servitor = target.asSummon();
			final PlayerInstance summonOwner = servitor.getOwner();
			
			servitor.stopActions();
			servitor.stopAllEffects();
			
			servitor.unSummon(summonOwner);
			summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED_YOU_LL_NEED_TO_SUMMON_A_NEW_ONE);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_npc_kill", InstantNpcKill::new);
	}
}
