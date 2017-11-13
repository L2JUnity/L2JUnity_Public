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
package org.l2junity.gameserver.network.client.recv.training;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.TrainingHolder;
import org.l2junity.gameserver.model.variables.AccountVariables;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.recv.IClientIncomingPacket;
import org.l2junity.gameserver.network.client.send.training.ExTrainingZone_Leaving;
import org.l2junity.network.PacketReader;

/**
 * @author Sdw
 */
public class NotifyTrainingRoomEnd implements IClientIncomingPacket
{
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		// Nothing to read
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final PlayerInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final TrainingHolder holder = activeChar.getAccountVariables().getObject(AccountVariables.TRAINING_CAMP, TrainingHolder.class);
		if (holder == null)
		{
			return;
		}
		
		if (holder.isTraining())
		{
			activeChar.teleToLocation(activeChar.getLastLocation());
			activeChar.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
			holder.setEndTime(System.currentTimeMillis());
			activeChar.getAccountVariables().set(AccountVariables.TRAINING_CAMP, holder);
		}
	}
}
