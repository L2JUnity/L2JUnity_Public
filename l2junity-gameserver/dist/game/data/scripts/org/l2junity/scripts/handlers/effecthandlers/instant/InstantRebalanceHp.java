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
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.gameserver.util.Util;

/**
 * Rebalance HP effect implementation.
 * @author Adry_85, earendil
 */
public final class InstantRebalanceHp extends AbstractEffect
{
	public InstantRebalanceHp(StatsSet params)
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
		final Party party = caster.getParty();
		if (party != null)
		{
			for (PlayerInstance member : party.getMembers())
			{
				if (!member.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, member, true))
				{
					fullHP += member.getMaxHp();
					currentHPs += member.getCurrentHp();
				}
				
				final Summon summon = member.getPet();
				if ((summon != null) && (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, summon, true)))
				{
					fullHP += summon.getMaxHp();
					currentHPs += summon.getCurrentHp();
				}
				
				for (Summon servitors : member.getServitors().values())
				{
					if (!servitors.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, servitors, true))
					{
						fullHP += servitors.getMaxHp();
						currentHPs += servitors.getCurrentHp();
					}
				}
			}
			
			double percentHP = currentHPs / fullHP;
			for (PlayerInstance member : party.getMembers())
			{
				if (!member.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, member, true))
				{
					double newHP = member.getMaxHp() * percentHP;
					if (newHP > member.getCurrentHp()) // The target gets healed
					{
						// The heal will be blocked if the current hp passes the limit
						if (member.getCurrentHp() > member.getMaxRecoverableHp())
						{
							newHP = member.getCurrentHp();
						}
						else if (newHP > member.getMaxRecoverableHp())
						{
							newHP = member.getMaxRecoverableHp();
						}
					}
					
					member.setCurrentHp(newHP);
				}
				
				final Summon summon = member.getPet();
				if ((summon != null) && (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, summon, true)))
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
				
				for (Summon servitors : member.getServitors().values())
				{
					if (!servitors.isDead() && Util.checkIfInRange(skill.getAffectRange(), caster, servitors, true))
					{
						double newHP = servitors.getMaxHp() * percentHP;
						if (newHP > servitors.getCurrentHp()) // The target gets healed
						{
							// The heal will be blocked if the current hp passes the limit
							if (servitors.getCurrentHp() > servitors.getMaxRecoverableHp())
							{
								newHP = servitors.getCurrentHp();
							}
							else if (newHP > servitors.getMaxRecoverableHp())
							{
								newHP = servitors.getMaxRecoverableHp();
							}
						}
						servitors.setCurrentHp(newHP);
					}
				}
			}
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_rebalance_hp", InstantRebalanceHp::new);
	}
}
