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
package org.l2junity.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.IStatsFunction;

/**
 * @author Sdw
 */
public class ShieldDefenceRateFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, DoubleStat stat)
	{
		throwIfPresent(base);
		
		return DoubleStat.defaultValue(creature, stat, calcEquippedItemsBaseValue(creature, stat) * (creature.getCON() > 0 ? BaseStats.CON.calcBonus(creature) : 1.));
	}
}
