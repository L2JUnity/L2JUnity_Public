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

import java.util.Collection;

import org.l2junity.gameserver.data.sql.impl.ClanTable;
import org.l2junity.gameserver.enums.TaxType;
import org.l2junity.gameserver.instancemanager.CastleManager;
import org.l2junity.gameserver.model.entity.Castle;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

/**
 * @author KenM
 */
public class ExShowCastleInfo implements IClientOutgoingPacket
{
	public static final ExShowCastleInfo STATIC_PACKET = new ExShowCastleInfo();
	
	private ExShowCastleInfo()
	{
		
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_CASTLE_INFO.writeId(packet);
		
		final Collection<Castle> castles = CastleManager.getInstance().getCastles();
		packet.writeD(castles.size());
		for (Castle castle : castles)
		{
			packet.writeD(castle.getResidenceId());
			if (castle.getOwnerId() > 0)
			{
				if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null)
				{
					packet.writeS(ClanTable.getInstance().getClan(castle.getOwnerId()).getName());
				}
				else
				{
					LOGGER.warn("Castle owner with no name! Castle: {} has an OwnerId = {} who does not have a name!", castle.getName(), castle.getOwnerId());
					packet.writeS("");
				}
			}
			else
			{
				packet.writeS("");
			}
			packet.writeD(castle.getTaxPercent(TaxType.BUY));
			packet.writeD((int) (castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
			packet.writeC(castle.getSiege().isInProgress() ? 0x01 : 0x00);
			packet.writeC(castle.getSide().ordinal());
		}
		return true;
	}
}
