/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.gameserver.network.client.send.changeattribute;

import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.gameserver.network.client.send.AbstractItemPacket;
import org.l2junity.network.PacketWriter;

/**
 * @author Sdw
 */
public class ExChangeAttributeInfo extends AbstractItemPacket
{
	private final boolean _isValid;
	private final int _attributeMask;
	
	private static final int[] ELEMENT_FLAG_ARRAY =
	{
		0x01,
		0x02,
		0x04,
		0x08,
		0x10,
		0x20
	};
	
	private static final int TOTAL_ELEMENT_FLAG = 0x3F;
	
	public ExChangeAttributeInfo(boolean isValid, AttributeType attributeType)
	{
		_isValid = isValid;
		_attributeMask = TOTAL_ELEMENT_FLAG - ELEMENT_FLAG_ARRAY[attributeType.getClientId()];
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CHANGE_ATTRIBUTE_INFO.writeId(packet);
		packet.writeD(_isValid ? 0x01 : 0x00);
		packet.writeD(_attributeMask);
		return true;
	}
}
