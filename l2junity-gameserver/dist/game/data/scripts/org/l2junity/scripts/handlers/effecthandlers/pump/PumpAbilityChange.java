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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public final class PumpAbilityChange extends AbstractEffect
{
	private final Map<DoubleStat, Float> _sharedStats = new HashMap<>();
	
	public PumpAbilityChange(StatsSet params)
	{
		if (params.isEmpty())
		{
			throw new IllegalArgumentException("Must have parameters!");
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet())
		{
			_sharedStats.put(DoubleStat.valueOf(param.getKey()), (Float.parseFloat((String) param.getValue())) / 100);
		}
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return target.isSummon();
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		final PlayerInstance owner = target.getActingPlayer();
		if (owner != null)
		{
			for (Entry<DoubleStat, Float> stats : _sharedStats.entrySet())
			{
				target.getStat().mergeAdd(stats.getKey(), owner.getStat().getValue(stats.getKey()) * stats.getValue());
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_ability_change", PumpAbilityChange::new);
	}
}
