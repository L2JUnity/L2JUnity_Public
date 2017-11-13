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

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Dispel By Slot Probability effect implementation.
 * @author Adry_85, Zoey76
 */
public final class InstantDispelBySlotProbability extends AbstractEffect
{
	private final Set<AbnormalType> _dispelAbnormals;
	private final int _rate;
	
	public InstantDispelBySlotProbability(StatsSet params)
	{
		final String[] dispelEffects = params.getString("dispel").split(";");
		_rate = params.getInt("rate", 100);
		_dispelAbnormals = new HashSet<>(dispelEffects.length);
		for (String slot : dispelEffects)
		{
			_dispelAbnormals.add(Enum.valueOf(AbnormalType.class, slot));
		}
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DISPEL;
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		targetCreature.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && (Rnd.get(100) < _rate) && _dispelAbnormals.contains(info.getSkill().getAbnormalType()), true, true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_dispel_by_slot_probability", InstantDispelBySlotProbability::new);
	}
}