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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import java.util.Collections;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aura effect implementation.
 */
public final class PumpAura extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PumpAura.class);
	
	private final SkillHolder _skill;
	
	public PumpAura(StatsSet params)
	{
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1), params.getInt("skillSubLevel", 0));
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		if (!_skill.getSkill().isSynergySkill())
		{
			target.getEffectList().stopEffects(Collections.singleton(_skill.getSkill().getAbnormalType()));
			target.getEffectList().addBlockedAbnormalTypes(Collections.singleton(_skill.getSkill().getAbnormalType()));
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (!_skill.getSkill().isSynergySkill())
		{
			target.getEffectList().removeBlockedAbnormalTypes(Collections.singleton(_skill.getSkill().getAbnormalType()));
		}
	}
	
	@Override
	public void tick(Creature caster, Creature effected, Skill skill)
	{
		if (caster.isDead())
		{
			return;
		}
		
		final Skill triggerSkill = _skill.getSkill();
		if (triggerSkill != null)
		{
			if (triggerSkill.isSynergySkill())
			{
				triggerSkill.applyEffects(caster, caster);
			}
			
			SkillCaster.triggerCast(caster, caster, triggerSkill);
		}
		else
		{
			LOGGER.warn("Skill not found effect called from {}", skill);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_aura", PumpAura::new);
	}
}
