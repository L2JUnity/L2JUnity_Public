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

import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.impl.PetDataTable;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.PetLevelData;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.stats.BaseStats;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.model.stats.IStatsFunction;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.model.zone.type.SwampZone;

/**
 * @author UnAfraid
 */
public class SpeedFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, DoubleStat stat)
	{
		throwIfPresent(base);
		
		double baseValue = getBaseSpeed(creature, stat);
		if (creature.isPlayer())
		{
			// Enchanted feet bonus
			baseValue += calcEnchantBodyPart(creature, L2Item.SLOT_FEET);
		}
		
		final byte speedStat = (byte) creature.getStat().getAdd(DoubleStat.STAT_BONUS_SPEED);
		if ((speedStat >= 0) && (speedStat < BaseStats.values().length))
		{
			final BaseStats baseStat = BaseStats.values()[speedStat];
			final double bonusDex = Math.max(0, baseStat.calcValue(creature) - 55);
			baseValue += bonusDex;
		}
		
		// In retail maximum run speed is 350 for summons (and every other npc?) and 300 for players
		return validateValue(creature, DoubleStat.defaultValue(creature, stat, baseValue), 1, creature.isPlayer() ? PlayerConfig.MAX_RUN_SPEED : (PlayerConfig.MAX_RUN_SPEED + 50));
	}
	
	@Override
	public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		if (isBlessed)
		{
			return (1 * Math.max(enchantLevel - 3, 0)) + (1 * Math.max(enchantLevel - 6, 0));
		}
		
		return (0.6 * Math.max(enchantLevel - 3, 0)) + (0.6 * Math.max(enchantLevel - 6, 0));
	}
	
	private double getBaseSpeed(Creature creature, DoubleStat stat)
	{
		double baseValue = calcEquippedItemsBaseValue(creature, stat);
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			if (player.isMounted())
			{
				final PetLevelData data = PetDataTable.getInstance().getPetLevelData(player.getMountNpcId(), player.getMountLevel());
				if (data != null)
				{
					baseValue = data.getSpeedOnRide(stat);
					// if level diff with mount >= 10, it decreases move speed by 50%
					if ((player.getMountLevel() - creature.getLevel()) >= 10)
					{
						baseValue /= 2;
					}
					
					// if mount is hungry, it decreases move speed by 50%
					if (player.isHungry())
					{
						baseValue /= 2;
					}
				}
			}
		}
		if (creature.isPlayable() && creature.isInsideZone(ZoneId.SWAMP))
		{
			// Config based run speed boost for players and summons.
			baseValue += PlayerConfig.RUN_SPD_BOOST;
			
			final SwampZone zone = ZoneManager.getInstance().getZone(creature, SwampZone.class);
			if (zone != null)
			{
				baseValue *= zone.getMoveBonus();
			}
		}
		return baseValue;
	}
}
