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
package org.l2junity.gameserver.model.stats;

import java.util.OptionalDouble;

import org.l2junity.gameserver.config.OlympiadConfig;
import org.l2junity.gameserver.model.PcCondOverride;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2PetInstance;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.CrystalType;

/**
 * @author UnAfraid
 */
@FunctionalInterface
public interface IStatsFunction
{
	default void throwIfPresent(OptionalDouble base)
	{
		if (base.isPresent())
		{
			throw new IllegalArgumentException("base should not be set for " + getClass().getSimpleName());
		}
	}
	
	default double calcEnchantBodyPart(Creature creature, int... slots)
	{
		double value = 0;
		for (int slot : slots)
		{
			final ItemInstance item = creature.getInventory().getPaperdollItemByL2ItemId(slot);
			if ((item != null) && (item.getEnchantLevel() >= 4) && (item.getItem().getCrystalTypePlus() == CrystalType.R))
			{
				value += calcEnchantBodyPartBonus(item.getEnchantLevel(), item.getItem().isBlessed());
			}
		}
		return value;
	}
	
	default double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		return 0;
	}
	
	default double calcWeaponBaseValue(Creature creature, DoubleStat stat)
	{
		final double baseTemplateBalue = creature.getTemplate().getBaseValue(stat, 0);
		final double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateBalue)).orElseGet(() ->
		{
			if (creature.isPet())
			{
				final L2PetInstance pet = (L2PetInstance) creature;
				final ItemInstance weapon = pet.getActiveWeaponInstance();
				final double baseVal = stat == DoubleStat.PHYSICAL_ATTACK ? pet.getPetLevelData().getPetPAtk() : stat == DoubleStat.MAGIC_ATTACK ? pet.getPetLevelData().getPetMAtk() : baseTemplateBalue;
				return baseVal + (weapon != null ? weapon.getItem().getStats(stat, baseVal) : 0);
			}
			else if (creature.isPlayer())
			{
				final ItemInstance weapon = creature.getActiveWeaponInstance();
				return (weapon != null ? weapon.getItem().getStats(stat, baseTemplateBalue) : baseTemplateBalue);
			}
			
			return baseTemplateBalue;
		});
		
		return baseValue;
	}
	
	default double calcEquippedItemsBaseValue(Creature creature, DoubleStat stat)
	{
		final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
		double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
		
		if (creature.isPlayable())
		{
			final Inventory inv = creature.getInventory();
			if (inv != null)
			{
				for (ItemInstance item : inv.getPaperdollItems(ItemInstance::isEquipped))
				{
					baseValue += item.getItem().getStats(stat, 0);
				}
			}
		}
		
		return baseValue;
	}
	
	default double calcEnchantedItemBonus(Creature creature, DoubleStat stat)
	{
		if (!creature.isPlayer())
		{
			return 0;
		}
		
		double value = 0;
		for (ItemInstance item : creature.getInventory().getPaperdollItems(ItemInstance::isEquipped, ItemInstance::isEnchanted))
		{
			if (item.getItem().getStats(stat, 0) <= 0)
			{
				continue;
			}
			
			final double blessedBonus = item.getItem().isBlessed() ? 1.5 : 1;
			int enchant = item.getEnchantLevel();
			
			if (creature.getActingPlayer().isInOlympiadMode() && (OlympiadConfig.ALT_OLY_ENCHANT_LIMIT >= 0) && (enchant > OlympiadConfig.ALT_OLY_ENCHANT_LIMIT))
			{
				enchant = OlympiadConfig.ALT_OLY_ENCHANT_LIMIT;
			}
			
			if ((stat == DoubleStat.MAGICAL_DEFENCE) || (stat == DoubleStat.PHYSICAL_DEFENCE))
			{
				value += calcEnchantDefBonus(item, blessedBonus, enchant);
			}
			else if (stat == DoubleStat.MAGIC_ATTACK)
			{
				value += calcEnchantMatkBonus(item, blessedBonus, enchant);
			}
			else if ((stat == DoubleStat.PHYSICAL_ATTACK) && item.isWeapon())
			{
				value += calcEnchantedPAtkBonus(item, blessedBonus, enchant);
			}
		}
		return value;
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantDefBonus(ItemInstance item, double blessedBonus, int enchant)
	{
		switch (item.getItem().getCrystalTypePlus())
		{
			case R:
			{
				return ((2 * blessedBonus * enchant) + (6 * blessedBonus * Math.max(0, enchant - 3)));
			}
			default:
			{
				return enchant + (3 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantMatkBonus(ItemInstance item, double blessedBonus, int enchant)
	{
		switch (item.getItem().getCrystalTypePlus())
		{
			case R:
			{
				return ((5 * blessedBonus * enchant) + (10 * blessedBonus * Math.max(0, enchant - 3)));
			}
			case S:
			{
				// M. Atk. increases by 4 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case A:
			case B:
			case C:
			{
				// M. Atk. increases by 3 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				// M. Atk. increases by 2 for all weapons. Starting at +4, M. Atk. bonus double.
				// Starting at +4, M. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantedPAtkBonus(ItemInstance item, double blessedBonus, int enchant)
	{
		switch (item.getItem().getCrystalTypePlus())
		{
			case R:
			{
				if (item.getWeaponItem().getBodyPart() == L2Item.SLOT_LR_HAND)
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						return (12 * blessedBonus * enchant) + (24 * blessedBonus * Math.max(0, enchant - 3));
					}
					return (7 * blessedBonus * enchant) + (14 * blessedBonus * Math.max(0, enchant - 3));
				}
				return (6 * blessedBonus * enchant) + (12 * blessedBonus * Math.max(0, enchant - 3));
			}
			case S:
			{
				if (item.getWeaponItem().getBodyPart() == L2Item.SLOT_LR_HAND)
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 10 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (10 * enchant) + (20 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 6 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (6 * enchant) + (12 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 5 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (5 * enchant) + (10 * Math.max(0, enchant - 3));
			}
			case A:
			{
				if (item.getWeaponItem().getBodyPart() == L2Item.SLOT_LR_HAND)
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 8 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (8 * enchant) + (16 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 5 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (5 * enchant) + (10 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 4 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case B:
			case C:
			{
				if (item.getWeaponItem().getBodyPart() == L2Item.SLOT_LR_HAND)
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 6 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (6 * enchant) + (12 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 4 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 3 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				if (item.getWeaponItem().getItemType().isRanged())
				{
					// Bows increase by 4.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 2 for all weapons with the exception of bows.
				// Starting at +4, P. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}
	
	default double validateValue(Creature creature, double value, double minValue, double maxValue)
	{
		if ((value > maxValue) && !creature.canOverrideCond(PcCondOverride.MAX_STATS_VALUE))
		{
			return maxValue;
		}
		
		return Math.max(minValue, value);
	}
	
	double calc(Creature creature, OptionalDouble base, DoubleStat stat);
}
