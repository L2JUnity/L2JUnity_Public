/*
 * Copyright (C) 2004-2016 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.enums.ReduceDropType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpReduceDropPenalty extends AbstractEffect
{
	private final double _exp;
	private final double _deathPenalty;
	private final ReduceDropType _type;
	
	public PumpReduceDropPenalty(StatsSet params)
	{
		_exp = params.getDouble("exp");
		_deathPenalty = params.getDouble("deathPenalty");
		_type = params.getEnum("type", ReduceDropType.class);
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		switch (_type)
		{
			case MOB:
			{
				target.getStat().mergeMul(DoubleStat.REDUCE_EXP_LOST_BY_MOB, (_exp / 100) + 1);
				target.getStat().mergeMul(DoubleStat.REDUCE_DEATH_PENALTY_BY_MOB, (_deathPenalty / 100) + 1);
				break;
			}
			case PK:
			{
				target.getStat().mergeMul(DoubleStat.REDUCE_EXP_LOST_BY_PVP, (_exp / 100) + 1);
				target.getStat().mergeMul(DoubleStat.REDUCE_DEATH_PENALTY_BY_PVP, (_deathPenalty / 100) + 1);
				break;
			}
			case RAID:
			{
				target.getStat().mergeMul(DoubleStat.REDUCE_EXP_LOST_BY_RAID, (_exp / 100) + 1);
				target.getStat().mergeMul(DoubleStat.REDUCE_DEATH_PENALTY_BY_RAID, (_deathPenalty / 100) + 1);
				break;
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_reduce_drop_penalty", PumpReduceDropPenalty::new);
	}
}
