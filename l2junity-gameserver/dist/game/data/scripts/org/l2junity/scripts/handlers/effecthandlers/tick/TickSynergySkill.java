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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synergy effect implementation.
 */
public final class TickSynergySkill extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TickSynergySkill.class);
	
	private final Set<AbnormalType> _requiredSlots;
	private final Set<AbnormalType> _optionalSlots;
	private final int _partyBuffSkillId;
	private final int _skillLevelScaleTo;
	private final int _minSlot;
	
	public TickSynergySkill(StatsSet params)
	{
		String requiredSlots = params.getString("requiredSlots", null);
		if ((requiredSlots != null) && !requiredSlots.isEmpty())
		{
			_requiredSlots = new HashSet<>();
			for (String slot : requiredSlots.split(";"))
			{
				_requiredSlots.add(Enum.valueOf(AbnormalType.class, slot));
			}
		}
		else
		{
			_requiredSlots = Collections.<AbnormalType> emptySet();
		}
		
		String optionalSlots = params.getString("optionalSlots", null);
		if ((optionalSlots != null) && !optionalSlots.isEmpty())
		{
			_optionalSlots = new HashSet<>();
			for (String slot : optionalSlots.split(";"))
			{
				_optionalSlots.add(Enum.valueOf(AbnormalType.class, slot));
			}
		}
		else
		{
			_optionalSlots = Collections.<AbnormalType> emptySet();
		}
		
		_partyBuffSkillId = params.getInt("partyBuffSkillId");
		_skillLevelScaleTo = params.getInt("skillLevelScaleTo", 1);
		_minSlot = params.getInt("minSlot", 2);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void tick(Creature caster, Creature effected, Skill skill)
	{
		if (caster.isDead())
		{
			return;
		}
		
		for (AbnormalType required : _requiredSlots)
		{
			if (!caster.hasAbnormalType(required))
			{
				return;
			}
		}
		
		final int abnormalCount = (int) _optionalSlots.stream().filter(caster::hasAbnormalType).count();
		
		if (abnormalCount >= _minSlot)
		{
			final SkillHolder partyBuff = new SkillHolder(_partyBuffSkillId, Math.min(abnormalCount - 1, _skillLevelScaleTo));
			final Skill partyBuffSkill = partyBuff.getSkill();
			
			if (partyBuffSkill != null)
			{
				final WorldObject target = partyBuffSkill.getTarget(caster, effected, false, false, false);
				
				if ((target != null) && target.isCreature())
				{
					SkillCaster.triggerCast(caster, target, partyBuffSkill);
				}
			}
			else
			{
				LOGGER.warn("Skill not found effect called from {}", skill);
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("t_synergy", TickSynergySkill::new);
	}
}
