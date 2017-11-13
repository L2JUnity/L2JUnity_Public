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

import org.l2junity.gameserver.enums.PcCafeConsumeType;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

/**
 * @author KenM
 * @author UnAfraid
 */
public class ExPCCafePointInfo implements IClientOutgoingPacket
{
	private final long _points;
	private final long _mAddPoint;
	private final int _mPeriodType;
	private final int _remainTime;
	private final PcCafeConsumeType _pointType;
	private final int _time;
	
	public ExPCCafePointInfo(long points, long pointsToAdd, PcCafeConsumeType type)
	{
		_points = points;
		_mAddPoint = pointsToAdd;
		_mPeriodType = 1;
		_remainTime = 0;
		_pointType = type;
		_time = 0;
		
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PCCAFE_POINT_INFO.writeId(packet);
		
		packet.writeD((int) _points); // Total Points
		packet.writeD((int) _mAddPoint); // Earned Points
		packet.writeC(_mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
		packet.writeD(_remainTime); // period hours left
		packet.writeC(_pointType.ordinal()); // points inc display color(0=yellow, 1=cyan-blue, 2=red, all other black)
		packet.writeD(_time); // value is in seconds * 3
		return true;
	}
}
