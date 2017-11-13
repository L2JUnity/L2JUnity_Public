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
package org.l2junity.gameserver.network.client.recv;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.variables.AccountVariables;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ExUISetting;
import org.l2junity.network.PacketReader;

/**
 * Request Save Key Mapping client packet.
 * @author mrTJO, Zoey76
 * @Reworked by Supreme - Grand Crusade / Classic 2.1
 */
public class RequestSaveKeyMapping implements IClientIncomingPacket
{
	private byte[] _data;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		final int length = packet.readD();
		if ((length > packet.getReadableBytes()) || (length < 0))
		{
			_data = null;
			return false;
		}
		_data = packet.readB(length);
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final PlayerInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		player.getAccountVariables().set(AccountVariables.UI_SETTINGS, _data);
		player.sendPacket(new ExUISetting(player));
	}
}
