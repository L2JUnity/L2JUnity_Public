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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.gameserver.util.Util;

/**
 * Rebalance HP effect implementation.
 * @author Adry_85, earendil
 */
public final class InstantRebalanceHpSummon extends AbstractEffect
{
	public InstantRebalanceHpSummon(StatsSet params)
	{
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		if (!caster.isPlayer())
		{
			return;
		}
		
		double fullHP = 0;
		double currentHPs = 0;
		
		for (Summon summon : caster.getServitors().values())
		{
			if (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, summon, true))
			{
				fullHP += summon.getMaxHp();
				currentHPs += summon.getCurrentHp();
			}
		}
		
		fullHP += caster.getMaxHp();
		currentHPs += caster.getCurrentHp();
		
		double percentHP = currentHPs / fullHP;
		for (Summon summon : caster.getServitors().values())
		{
			if (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, summon, true))
			{
				double newHP = summon.getMaxHp() * percentHP;
				if (newHP > summon.getCurrentHp()) // The target gets healed
				{
					// The heal will be blocked if the current hp passes the limit
					if (summon.getCurrentHp() > summon.getMaxRecoverableHp())
					{
						newHP = summon.getCurrentHp();
					}
					else if (newHP > summon.getMaxRecoverableHp())
					{
						newHP = summon.getMaxRecoverableHp();
					}
				}
				
				summon.setCurrentHp(newHP);
			}
		}
		
		double newHP = caster.getMaxHp() * percentHP;
		if (newHP > caster.getCurrentHp()) // The target gets healed
		{
			// The heal will be blocked if the current hp passes the limit
			if (caster.getCurrentHp() > caster.getMaxRecoverableHp())
			{
				newHP = caster.getCurrentHp();
			}
			else if (newHP > caster.getMaxRecoverableHp())
			{
				newHP = caster.getMaxRecoverableHp();
			}
		}
		caster.setCurrentHp(newHP);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_rebalance_hp_summon", InstantRebalanceHpSummon::new);
	}
}
