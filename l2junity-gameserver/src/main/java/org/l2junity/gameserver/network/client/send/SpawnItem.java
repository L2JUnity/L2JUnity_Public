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

import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

public final class SpawnItem implements IClientOutgoingPacket
{
	private final ItemInstance _item;
	
	public SpawnItem(ItemInstance item)
	{
		_item = item;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SPAWN_ITEM.writeId(packet);
		
		packet.writeD(_item.getObjectId());
		packet.writeD(_item.getDisplayId());
		packet.writeD((int) _item.getX());
		packet.writeD((int) _item.getY());
		packet.writeD((int) _item.getZ());
		// only show item count if it is a stackable item
		packet.writeD(_item.isStackable() ? 0x01 : 0x00);
		packet.writeQ(_item.getCount());
		packet.writeD(0x00); // c2
		packet.writeC(_item.getEnchantLevel());
		packet.writeC(_item.getAugmentation() != null ? 1 : 0);
		packet.writeC(_item.getSpecialAbilities().size());
		return true;
	}
}
