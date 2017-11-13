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

import org.l2junity.gameserver.data.xml.impl.SkillData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.skills.SkillCaster;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Call Skill effect implementation.
 * @author NosBit
 */
public final class InstantCallSkill extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InstantCallSkill.class);
	
	private final SkillHolder _skillHolder;
	private final int _skillLevelScaleTo;
	
	public InstantCallSkill(StatsSet params)
	{
		_skillHolder = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1), params.getInt("skillSubLevel", 0));
		_skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Skill triggerSkill;
		if (_skillLevelScaleTo <= 0)
		{
			triggerSkill = _skillHolder.getSkill();
		}
		else
		{
			final Creature targetCreature = target.asCreature();
			if (targetCreature != null)
			{
				final BuffInfo buffInfo = targetCreature.getEffectList().getBuffInfoBySkillId(_skillHolder.getSkillId());
				if (buffInfo != null)
				{
					triggerSkill = SkillData.getInstance().getSkill(_skillHolder.getSkillId(), Math.min(_skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
				}
				else
				{
					triggerSkill = _skillHolder.getSkill();
				}
			}
			else
			{
				triggerSkill = _skillHolder.getSkill();
			}
		}
		
		if (triggerSkill != null)
		{
			SkillCaster.triggerCast(caster, target, triggerSkill);
		}
		else
		{
			LOGGER.warn("Skill not found effect called from {}", skill);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_call_skill", InstantCallSkill::new);
	}
}
