/*
 * Copyright (C) 2004-2015 L2J Unity
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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpStatUp extends AbstractEffect
{
	private final BaseStats _stat;
	private final double _amount;
	
	public PumpStatUp(StatsSet params)
	{
		_amount = params.getDouble("amount");
		_stat = params.getEnum("stat", BaseStats.class);
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		final DoubleStat stat;
		
		switch (_stat)
		{
			case STR:
			{
				stat = DoubleStat.STAT_STR;
				break;
			}
			case INT:
			{
				stat = DoubleStat.STAT_INT;
				break;
			}
			case DEX:
			{
				stat = DoubleStat.STAT_DEX;
				break;
			}
			case WIT:
			{
				stat = DoubleStat.STAT_WIT;
				break;
			}
			case CON:
			{
				stat = DoubleStat.STAT_CON;
				break;
			}
			case MEN:
			{
				stat = DoubleStat.STAT_MEN;
				break;
			}
			case CHA:
			{
				stat = DoubleStat.STAT_CHA;
				break;
			}
			case LUC:
			{
				stat = DoubleStat.STAT_LUC;
				break;
			}
			default:
			{
				stat = null;
				break;
			}
		}
		target.getStat().mergeAdd(stat, _amount);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_stat_up", PumpStatUp::new);
	}
}
