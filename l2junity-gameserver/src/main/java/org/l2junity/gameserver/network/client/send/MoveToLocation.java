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

import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

public final class MoveToLocation implements IClientOutgoingPacket
{
	private final int _objectId, _x, _y, _z, _destinationX, _destinationY, _destinationZ;
	
	public MoveToLocation(Creature cha)
	{
		_objectId = cha.getObjectId();
		_x = (int) cha.getX();
		_y = (int) cha.getY();
		_z = (int) cha.getZ();
		_destinationX = (int) cha.getXdestination();
		_destinationY = (int) cha.getYdestination();
		_destinationZ = (int) cha.getZdestination();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MOVE_TO_LOCATION.writeId(packet);
		
		packet.writeD(_objectId);
		
		packet.writeD(_destinationX);
		packet.writeD(_destinationY);
		packet.writeD(_destinationZ);
		
		packet.writeD(_x);
		packet.writeD(_y);
		packet.writeD(_z);
		return true;
	}
}
