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

import org.l2junity.gameserver.enums.StorageType;
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
public class PumpEnlargeStorage extends AbstractEffect
{
	private final StorageType _type;
	private final double _amount;
	
	public PumpEnlargeStorage(StatsSet params)
	{
		_amount = params.getDouble("amount");
		_type = params.getEnum("type", StorageType.class, StorageType.INVENTORY_NORMAL);
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		DoubleStat stat = DoubleStat.INVENTORY_NORMAL;
		
		switch (_type)
		{
			case TRADE_BUY:
			{
				stat = DoubleStat.TRADE_BUY;
				break;
			}
			case TRADE_SELL:
			{
				stat = DoubleStat.TRADE_SELL;
				break;
			}
			case RECIPE_DWARVEN:
			{
				stat = DoubleStat.RECIPE_DWARVEN;
				break;
			}
			case RECIPE_COMMON:
			{
				stat = DoubleStat.RECIPE_COMMON;
				break;
			}
			case STORAGE_PRIVATE:
			{
				stat = DoubleStat.STORAGE_PRIVATE;
				break;
			}
		}
		target.getStat().mergeAdd(stat, _amount);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_enlarge_storage", PumpEnlargeStorage::new);
	}
}
