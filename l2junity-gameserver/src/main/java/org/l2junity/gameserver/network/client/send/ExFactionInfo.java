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
package org.l2junity.gameserver.network.client.send;

import org.l2junity.gameserver.data.xml.impl.FactionData;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.FactionHolder;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

/**
 * @author Sdw
 */
public class ExFactionInfo implements IClientOutgoingPacket
{
	private final PlayerInstance _player;
	private final boolean _open;
	
	public ExFactionInfo(PlayerInstance player, boolean open)
	{
		_player = player;
		_open = open;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_FACTION_INFO.writeId(packet);
		
		packet.writeD(_player.getObjectId());
		packet.writeC(_open ? 0x01 : 0x00);
		packet.writeD(_player.getFactionsPoints().size());
		_player.getFactionsPoints().entrySet().forEach(entry ->
		{
			final FactionHolder holder = FactionData.getInstance().getFaction(entry.getKey());
			if (holder != null)
			{
				packet.writeC(holder.getId());
				packet.writeH(holder.getLevel(entry.getValue()));
				packet.writeE(holder.getProgressToNextLevel(entry.getValue()));
			}
		});
		return true;
	}
}
