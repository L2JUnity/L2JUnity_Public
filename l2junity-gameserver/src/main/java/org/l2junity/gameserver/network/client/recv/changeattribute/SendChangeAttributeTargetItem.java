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
package org.l2junity.gameserver.network.client.recv.changeattribute;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.ChangeAttributeRequest;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.recv.IClientIncomingPacket;
import org.l2junity.gameserver.network.client.send.changeattribute.ExChangeAttributeFail;
import org.l2junity.gameserver.network.client.send.changeattribute.ExChangeAttributeInfo;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.network.PacketReader;

/**
 * @author Sdw
 */
public class SendChangeAttributeTargetItem implements IClientIncomingPacket
{
	private boolean _isValid;
	private int _objectId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_isValid = packet.readD() == 1;
		_objectId = packet.readD();
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
		
		if (!_isValid)
		{
			activeChar.sendPacket(ExChangeAttributeFail.STATIC_PACKET);
			activeChar.removeRequest(ChangeAttributeRequest.class);
			return;
		}
		
		final ChangeAttributeRequest request = activeChar.getRequest(ChangeAttributeRequest.class);
		if ((request == null) || request.isProcessing())
		{
			activeChar.sendPacket(ExChangeAttributeFail.STATIC_PACKET);
			activeChar.removeRequest(ChangeAttributeRequest.class);
			return;
		}
		
		if (activeChar.isProcessingTransaction() || activeChar.isInStoreMode())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_AN_ATTRIBUTE_WHILE_USING_A_PRIVATE_STORE_OR_WORKSHOP);
			activeChar.removeRequest(request.getClass());
			return;
		}
		
		request.setChangeAttributeItem(_objectId);
		
		final ItemInstance item = request.getChangeAttributeItem();
		final ItemInstance crystal = request.getChangeAttributeCrystal();
		if ((item == null) || (crystal == null) || !item.isElementable() || !item.isWeapon())
		{
			activeChar.removeRequest(request.getClass());
			activeChar.sendPacket(ExChangeAttributeFail.STATIC_PACKET);
			return;
		}
		
		activeChar.sendPacket(new ExChangeAttributeInfo(true, item.getAttackAttributeType()));
		
	}
}