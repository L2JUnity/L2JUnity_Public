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
package org.l2junity.gameserver.network.client.send;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem implements IClientOutgoingPacket
{
	private int _itemId;
	private long _count;
	private AttributeType _atribute;
	private int _level;
	private final Set<Integer> _items = new HashSet<>();
	
	public ExChooseInventoryAttributeItem(PlayerInstance activeChar, ItemInstance stone)
	{
		final EnchantItemAttributeRequest request = activeChar.getRequest(EnchantItemAttributeRequest.class);
		if (request == null)
		{
			return;
		}
		_itemId = stone.getDisplayId();
		_count = stone.getCount();
		_atribute = request.getWeaponAttribute();
		_level = request.getMaxLevel();
		
		// Register only items that can be put an attribute stone/crystal
		for (ItemInstance item : activeChar.getInventory().getItems())
		{
			if (item.isElementable())
			{
				_items.add(item.getObjectId());
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM.writeId(packet);
		
		packet.writeD(_itemId);
		packet.writeQ(_count);
		packet.writeD(_atribute == AttributeType.FIRE ? 1 : 0); // Fire
		packet.writeD(_atribute == AttributeType.WATER ? 1 : 0); // Water
		packet.writeD(_atribute == AttributeType.WIND ? 1 : 0); // Wind
		packet.writeD(_atribute == AttributeType.EARTH ? 1 : 0); // Earth
		packet.writeD(_atribute == AttributeType.HOLY ? 1 : 0); // Holy
		packet.writeD(_atribute == AttributeType.DARK ? 1 : 0); // Unholy
		packet.writeD(_level); // Item max attribute level
		packet.writeD(_items.size());
		_items.forEach(packet::writeD);
		return true;
	}
}
