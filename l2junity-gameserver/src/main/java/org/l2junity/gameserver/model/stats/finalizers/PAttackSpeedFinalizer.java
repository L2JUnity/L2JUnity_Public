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

import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.IStatsFunction;

/**
 * @author UnAfraid
 */
public class PAttackSpeedFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, DoubleStat stat)
	{
		throwIfPresent(base);
		double baseValue = calcWeaponBaseValue(creature, stat);
		if (L2JModsConfig.L2JMOD_CHAMPION_ENABLE && creature.isChampion())
		{
			baseValue *= L2JModsConfig.L2JMOD_CHAMPION_SPD_ATK;
		}
		final double chaBonus = creature.isPlayer() ? BaseStats.CHA.calcBonus(creature) : 1.;
		final double dexBonus = creature.getDEX() > 0 ? BaseStats.DEX.calcBonus(creature) : 1.;
		baseValue *= dexBonus * chaBonus;
		return validateValue(creature, defaultValue(creature, stat, baseValue), 1, PlayerConfig.MAX_PATK_SPEED);
	}
	
	private double defaultValue(Creature creature, DoubleStat stat, double baseValue)
	{
		final double mul = Math.max(creature.getStat().getMul(stat), 0.7);
		final double add = creature.getStat().getAdd(stat);
		return (baseValue * mul) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
	}
}
