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

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.impl.VariationData;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ExVariationCancelResult;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;
import org.l2junity.network.PacketReader;

/**
 * Format(ch) d
 * @author -Wooden-
 */
public final class RequestRefineCancel implements IClientIncomingPacket
{
	private int _targetItemObjId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_targetItemObjId = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		PlayerInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
		{
			client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
			return;
		}
		
		if (targetItem.getOwnerId() != activeChar.getObjectId())
		{
			Util.handleIllegalPlayerAction(client.getActiveChar(), "Warning!! Character " + client.getActiveChar().getName() + " of account " + client.getActiveChar().getAccountName() + " tryied to augment item that doesn't own.", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		// cannot remove augmentation from a not augmented item
		if (!targetItem.isAugmented())
		{
			client.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
			return;
		}
		
		// get the price
		final long price = VariationData.getInstance().getCancelFee(targetItem.getId(), targetItem.getAugmentation().getMineralId());
		if (price < 0)
		{
			client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
			return;
		}
		
		// try to reduce the players adena
		if (!activeChar.reduceAdena("RequestRefineCancel", price, targetItem, true))
		{
			client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
			client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		
		// unequip item
		if (targetItem.isEquipped())
		{
			activeChar.unequipWeapon();
		}
		
		// remove the augmentation
		targetItem.removeAugmentation();
		
		// send ExVariationCancelResult
		client.sendPacket(ExVariationCancelResult.STATIC_PACKET_SUCCESS);
		
		// send inventory update
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		activeChar.sendInventoryUpdate(iu);
	}
}
