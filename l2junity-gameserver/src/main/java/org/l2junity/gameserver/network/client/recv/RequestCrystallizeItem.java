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

import java.util.List;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2junity.gameserver.enums.PrivateStoreType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.CrystallizationRequest;
import org.l2junity.gameserver.model.holders.ItemChanceHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.CrystalType;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;
import org.l2junity.network.PacketReader;

/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestCrystallizeItem implements IClientIncomingPacket
{
	private int _objectId;
	private long _count;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_objectId = packet.readD();
		_count = packet.readQ();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		PlayerInstance activeChar = client.getActiveChar();
		
		if (activeChar == null)
		{
			LOGGER.debug("RequestCrystalizeItem: activeChar was null");
			return;
		}
		
		final CrystallizationRequest request = activeChar.getRequest(CrystallizationRequest.class);
		if ((request == null) || request.isProcessing())
		{
			client.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
			return;
		}
		
		request.setProcessing(true);
		
		try
		{
			if ((_objectId != request.getObjectId()) || (_count != request.getCount()))
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (_count <= 0)
			{
				Util.handleIllegalPlayerAction(activeChar, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + activeChar.getName(), GeneralConfig.DEFAULT_PUNISH);
				return;
			}
			
			if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
			{
				client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
				return;
			}
			
			final ItemInstance itemToRemove = request.getItem();
			if (itemToRemove == null)
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (itemToRemove.isHeroItem())
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (_count > itemToRemove.getCount())
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (itemToRemove.isShadowItem() || itemToRemove.isTimeLimitedItem())
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!itemToRemove.getItem().isCrystallizable() || (itemToRemove.getItem().getCrystalCount() <= 0) || (itemToRemove.getItem().getCrystalType() == CrystalType.NONE))
			{
				client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
				return;
			}
			
			if (!activeChar.getInventory().canManipulateWithItemId(itemToRemove.getId()))
			{
				client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
				return;
			}
			
			// Check if the char can crystallize items and return if false;
			if (activeChar.getCrystallizeGrade().ordinal() < itemToRemove.getItem().getItemGrade().ordinal())
			{
				client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(itemToRemove);
			if ((crystallizationRewards == null) || crystallizationRewards.isEmpty())
			{
				activeChar.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
				return;
			}
			
			// unequip if needed
			SystemMessage sm;
			if (itemToRemove.isEquipped())
			{
				ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot());
				InventoryUpdate iu = new InventoryUpdate();
				for (ItemInstance item : unequiped)
				{
					iu.addModifiedItem(item);
				}
				activeChar.sendInventoryUpdate(iu);
				
				if (itemToRemove.getEnchantLevel() > 0)
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
					sm.addInt(itemToRemove.getEnchantLevel());
					sm.addItemName(itemToRemove);
				}
				else
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
					sm.addItemName(itemToRemove);
				}
				client.sendPacket(sm);
			}
			
			// remove from inventory
			final ItemInstance removedItem = activeChar.getInventory().destroyItem("Crystalize", _objectId, _count, activeChar, null);
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addRemovedItem(removedItem);
			activeChar.sendInventoryUpdate(iu);
			
			for (ItemChanceHolder holder : crystallizationRewards)
			{
				final double rand = Rnd.nextDouble() * 100;
				if (rand < holder.getChance())
				{
					// add crystals
					final ItemInstance createdItem = activeChar.getInventory().addItem("Crystalize", holder.getId(), holder.getCount(), activeChar, activeChar);
					
					sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
					sm.addItemName(createdItem);
					sm.addLong(holder.getCount());
					client.sendPacket(sm);
				}
			}
			
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_CRYSTALLIZED);
			sm.addItemName(removedItem);
			client.sendPacket(sm);
			
			activeChar.broadcastUserInfo();
		}
		finally
		{
			activeChar.removeRequest(CrystallizationRequest.class);
		}
	}
}
