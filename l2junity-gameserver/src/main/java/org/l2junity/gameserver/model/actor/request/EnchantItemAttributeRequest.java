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
package org.l2junity.gameserver.model.actor.request;

import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

/**
 * @author UnAfraid
 */
public final class EnchantItemAttributeRequest extends AbstractRequest
{
	public static final int[] WEAPON_VALUES =
	{
		0, // Level 1
		25, // Level 2
		75, // Level 3
		150, // Level 4
		175, // Level 5
		225, // Level 6
		300, // Level 7
		325, // Level 8
		375, // Level 9
		450, // Level 10
		475, // Level 11
		525, // Level 12
		600, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	public static final int[] ARMOR_VALUES =
	{
		0, // Level 1
		12, // Level 2
		30, // Level 3
		60, // Level 4
		72, // Level 5
		90, // Level 6
		120, // Level 7
		132, // Level 8
		150, // Level 9
		180, // Level 10
		192, // Level 11
		210, // Level 12
		240, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	private volatile int _enchantingItemObjectId;
	private volatile int _enchantingStoneObjectId;
	final AttributeType _weaponAttribute;
	final AttributeType _armorAttribute;
	final int _maxLevel;
	final int _minValue;
	final int _maxValue;
	
	public EnchantItemAttributeRequest(PlayerInstance activeChar, int enchantingStoneObjectId, AttributeType weaponAttribute, AttributeType armorAttribute, int maxLevel, int minValue, int maxValue)
	{
		super(activeChar);
		_enchantingStoneObjectId = enchantingStoneObjectId;
		_weaponAttribute = weaponAttribute;
		_armorAttribute = armorAttribute;
		_maxLevel = maxLevel;
		_minValue = minValue;
		_maxValue = maxValue;
	}
	
	public ItemInstance getEnchantingItem()
	{
		return getActiveChar().getInventory().getItemByObjectId(_enchantingItemObjectId);
	}
	
	public void setEnchantingItem(int objectId)
	{
		_enchantingItemObjectId = objectId;
	}
	
	public ItemInstance getEnchantingStone()
	{
		return getActiveChar().getInventory().getItemByObjectId(_enchantingStoneObjectId);
	}
	
	public void setEnchantingStone(int objectId)
	{
		_enchantingStoneObjectId = objectId;
	}
	
	public AttributeType getWeaponAttribute()
	{
		return _weaponAttribute;
	}
	
	public AttributeType getArmorAttribute()
	{
		return _armorAttribute;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int getLimit(boolean isWeapon)
	{
		return isWeapon ? WEAPON_VALUES[_maxLevel] : ARMOR_VALUES[_maxLevel];
	}
	
	public int getMinValue()
	{
		return _minValue;
	}
	
	public int getMaxValue()
	{
		return _minValue;
	}
	
	@Override
	public boolean isItemRequest()
	{
		return true;
	}
	
	@Override
	public boolean canWorkWith(AbstractRequest request)
	{
		return !request.isItemRequest();
	}
	
	@Override
	public boolean isUsing(int objectId)
	{
		return (objectId > 0) && ((objectId == _enchantingItemObjectId) || (objectId == _enchantingStoneObjectId));
	}
}
