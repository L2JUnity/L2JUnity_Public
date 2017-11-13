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

import java.util.List;

import org.l2junity.gameserver.enums.DispelSlotType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.effects.L2EffectType;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85
 */
public final class InstantDispelByCategory extends AbstractEffect
{
	private final DispelSlotType _slot;
	private final int _rate;
	private final int _max;
	
	public InstantDispelByCategory(StatsSet params)
	{
		_slot = params.getEnum("slot", DispelSlotType.class, DispelSlotType.BUFF);
		_rate = params.getInt("rate");
		_max = params.getInt("max");
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
		
		if (targetCreature.isDead())
		{
			return;
		}
		
		final List<BuffInfo> canceled = Formulas.calcCancelStealEffects(caster, targetCreature, skill, _slot, _rate, _max);
		for (BuffInfo can : canceled)
		{
			targetCreature.getEffectList().stopSkillEffects(true, can.getSkill());
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_dispel_by_category", InstantDispelByCategory::new);
	}
}