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
package org.l2junity.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.IStatsFunction;

/**
 * @author Sdw
 */
public class VampiricChanceFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, DoubleStat stat)
	{
		throwIfPresent(base);
		
		final double amount = creature.getStat().getValue(DoubleStat.ABSORB_DAMAGE_PERCENT, 0) * 100;
		final double vampiricSum = creature.getStat().getVampiricSum();
		
		return amount > 0 ? DoubleStat.defaultValue(creature, stat, Math.min(1.0, vampiricSum / amount / 100)) : 0;
	}
}
