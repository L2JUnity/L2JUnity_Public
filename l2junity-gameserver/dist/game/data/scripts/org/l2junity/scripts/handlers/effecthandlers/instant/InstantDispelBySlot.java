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

import java.util.HashMap;
import java.util.Map;

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
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 */
public final class InstantDispelBySlot extends AbstractEffect
{
	private final Map<AbnormalType, Short> _dispelAbnormals;
	
	public InstantDispelBySlot(StatsSet params)
	{
		final String[] dispelEffects = params.getString("dispel").split(";");
		_dispelAbnormals = new HashMap<>(dispelEffects.length);
		for (String ngtStack : dispelEffects)
		{
			String[] ngt = ngtStack.split(",");
			_dispelAbnormals.put(Enum.valueOf(AbnormalType.class, ngt[0]), Short.parseShort(ngt[1]));
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
		
		// Continue only if target has any of the abnormals. Save useless cycles.
		if (targetCreature.getEffectList().hasAbnormalType(_dispelAbnormals.keySet()))
		{
			// Dispel transformations (buff and by GM)
			final Short transformToDispel = _dispelAbnormals.get(AbnormalType.TRANSFORM);
			if ((transformToDispel != null) && ((transformToDispel == targetCreature.getTransformationId()) || (transformToDispel < 0)))
			{
				targetCreature.stopTransformation(true);
			}
			
			targetCreature.getEffectList().stopEffects(info ->
			{
				// We have already dealt with transformation from above.
				if (info.isAbnormalType(AbnormalType.TRANSFORM))
				{
					return false;
				}
				
				final Short abnormalLevel = _dispelAbnormals.get(info.getSkill().getAbnormalType());
				return (abnormalLevel != null) && ((abnormalLevel < 0) || (abnormalLevel >= info.getSkill().getAbnormalLvl()));
			}, true, true);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_dispel_by_slot", InstantDispelBySlot::new);
	}
}
