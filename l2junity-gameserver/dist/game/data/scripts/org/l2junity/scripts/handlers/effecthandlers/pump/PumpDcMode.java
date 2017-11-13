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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BooleanStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.scripts.handlers.effecthandlers.AbstractBooleanStatEffect;

/**
 * Double Casting effect implementation.
 * @author Nik
 */
public final class PumpDcMode extends AbstractBooleanStatEffect
{
	private static final SkillHolder[] TOGGLE_SKILLS = new SkillHolder[]
	{
		new SkillHolder(11007, 1),
		new SkillHolder(11009, 1),
		new SkillHolder(11008, 1),
		new SkillHolder(11010, 1)
	};
	
	private final Map<Integer, List<SkillHolder>> _addedToggles = new HashMap<>();
	
	public PumpDcMode(StatsSet params)
	{
		super(BooleanStat.DOUBLE_CAST);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			for (SkillHolder holder : TOGGLE_SKILLS)
			{
				skill = holder.getSkill();
				if ((skill != null) && !target.isAffectedBySkill(holder))
				{
					_addedToggles.computeIfAbsent(target.getObjectId(), v -> new ArrayList<>()).add(holder);
					skill.applyEffects(target, target);
				}
			}
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			_addedToggles.computeIfPresent(target.getObjectId(), (k, v) ->
			{
				v.forEach(h -> target.stopSkillEffects(h.getSkill()));
				return null;
			});
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_dc_mode", PumpDcMode::new);
	}
}