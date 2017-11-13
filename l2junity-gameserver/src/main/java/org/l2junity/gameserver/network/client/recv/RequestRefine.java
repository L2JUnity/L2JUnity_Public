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

import org.l2junity.gameserver.data.xml.impl.VariationData;
import org.l2junity.gameserver.model.VariationInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.options.Variation;
import org.l2junity.gameserver.model.options.VariationFee;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ExVariationResult;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.network.PacketReader;

/**
 * Format:(ch) dddd
 * @author -Wooden-
 */
public final class RequestRefine extends AbstractRefinePacket
{
	private int _targetItemObjId;
	private int _mineralItemObjId;
	private int _feeItemObjId;
	private long _feeCount;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_targetItemObjId = packet.readD();
		_mineralItemObjId = packet.readD();
		_feeItemObjId = packet.readD();
		_feeCount = packet.readQ();
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
		
		final ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
		{
			return;
		}
		
		final ItemInstance mineralItem = activeChar.getInventory().getItemByObjectId(_mineralItemObjId);
		if (mineralItem == null)
		{
			return;
		}
		
		final ItemInstance feeItem = activeChar.getInventory().getItemByObjectId(_feeItemObjId);
		if (feeItem == null)
		{
			return;
		}
		
		final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), mineralItem.getId());
		if (!isValid(activeChar, targetItem, mineralItem, feeItem, fee))
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, false));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		if (_feeCount != fee.getItemCount())
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, false));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final Variation variation = VariationData.getInstance().getVariation(mineralItem.getId());
		if (variation == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, false));
			return;
		}
		
		final VariationInstance augment = VariationData.getInstance().generateRandomVariation(variation, targetItem);
		if (augment == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, false));
			return;
		}
		
		// unequip item
		final InventoryUpdate iu = new InventoryUpdate();
		if (targetItem.isEquipped())
		{
			ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(targetItem.getLocationSlot());
			for (ItemInstance itm : unequiped)
			{
				iu.addModifiedItem(itm);
			}
			activeChar.broadcastUserInfo();
		}
		
		// consume the life stone
		if (!activeChar.destroyItem("RequestRefine", mineralItem, 1, null, false))
		{
			return;
		}
		
		// consume the gemstones
		if (!activeChar.destroyItem("RequestRefine", feeItem, _feeCount, null, false))
		{
			return;
		}
		
		targetItem.setAugmentation(augment, true);
		activeChar.sendPacket(new ExVariationResult(augment.getOption1Id(), augment.getOption2Id(), true));
		
		iu.addModifiedItem(targetItem);
		activeChar.sendInventoryUpdate(iu);
	}
}
