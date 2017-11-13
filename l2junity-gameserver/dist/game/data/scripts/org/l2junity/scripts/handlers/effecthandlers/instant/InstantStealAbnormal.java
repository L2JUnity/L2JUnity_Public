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
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.BuffInfo;
import org.l2junity.gameserver.model.skills.EffectScope;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Steal Abnormal effect implementation.
 * @author Adry_85, Zoey76
 */
public final class InstantStealAbnormal extends AbstractEffect
{
	private final DispelSlotType _slot;
	private final int _rate;
	private final int _max;
	
	public InstantStealAbnormal(StatsSet params)
	{
		_slot = params.getEnum("slot", DispelSlotType.class, DispelSlotType.BUFF);
		_rate = params.getInt("rate");
		_max = params.getInt("max");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		if (casterPlayer.equals(targetPlayer))
		{
			return;
		}
		
		final List<BuffInfo> toSteal = Formulas.calcCancelStealEffects(casterPlayer, targetPlayer, skill, _slot, _rate, _max);
		if (toSteal.isEmpty())
		{
			return;
		}
		
		for (BuffInfo infoToSteal : toSteal)
		{
			// Invert effected and effector.
			final BuffInfo stolen = new BuffInfo(targetPlayer, casterPlayer, infoToSteal.getSkill(), false, null, null);
			stolen.setAbnormalTime(infoToSteal.getTime()); // Copy the remaining time.
			// To include all the effects, it's required to go through the template rather the buff info.
			infoToSteal.getSkill().applyContinuousEffects(EffectScope.GENERAL, stolen);
			targetPlayer.getEffectList().remove(infoToSteal, true, true, true);
			casterPlayer.getEffectList().add(stolen);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_steal_abnormal", InstantStealAbnormal::new);
	}
}