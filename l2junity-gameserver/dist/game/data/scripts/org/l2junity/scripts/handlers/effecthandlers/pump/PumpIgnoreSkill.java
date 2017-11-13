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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Resist Skill effect implementaion.
 * @author UnAfraid
 */
public final class PumpIgnoreSkill extends AbstractEffect
{
	private final Set<SkillHolder> _skills;
	
	public PumpIgnoreSkill(StatsSet params)
	{
		_skills = Arrays.stream(params.getString("skills", "").split(";")).map(s -> s.split(",")).map(s -> new SkillHolder(Integer.parseInt(s[0]), s.length > 1 ? Integer.parseInt(s[1]) : 0)).collect(Collectors.toSet());
		
		if (_skills.isEmpty())
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + ": Without parameters!");
		}
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		for (SkillHolder holder : _skills)
		{
			target.addIgnoreSkillEffects(holder);
			target.sendDebugMessage("Applying invul against " + holder.getSkill(), DebugType.SKILLS);
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		for (SkillHolder holder : _skills)
		{
			target.removeIgnoreSkillEffects(holder);
			target.sendDebugMessage("Removing invul against " + holder.getSkill(), DebugType.SKILLS);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_ignore_skill", PumpIgnoreSkill::new);
	}
}
