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

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Dispel effects by skill id and level defined.
 * @author Nik
 */
public final class InstantDispelByName extends AbstractEffect
{
	private final Set<SkillHolder> _dispelSkills = new HashSet<>();
	
	public InstantDispelByName(StatsSet params)
	{
		if (params.isEmpty())
		{
			throw new IllegalArgumentException("Must have parameters!");
		}
		
		for (StatsSet group : params.getList("skills", StatsSet.class))
		{
			for (StatsSet skill : group.getList(".", StatsSet.class))
			{
				_dispelSkills.add(new SkillHolder(skill.getInt(".id"), skill.getInt(".level")));
			}
		}
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DISPEL_BY_SLOT;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (_dispelSkills.isEmpty())
		{
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		targetCreature.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && _dispelSkills.stream().anyMatch(h -> (info.getSkill().getId() == h.getSkillId()) && ((h.getSkillLevel() == 0) || (info.getSkill().getLevel() == h.getSkillLevel()))), true, true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_dispel_by_name", InstantDispelByName::new);
	}
}
