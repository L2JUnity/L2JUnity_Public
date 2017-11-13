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
package org.l2junity.gameserver.network.client.recv.crystalization;

import java.util.List;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2junity.gameserver.enums.PrivateStoreType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.CrystallizationRequest;
import org.l2junity.gameserver.model.holders.ItemChanceHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.items.type.CrystalType;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.recv.IClientIncomingPacket;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.crystalization.ExGetCrystalizingEstimation;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;
import org.l2junity.network.PacketReader;

/**
 * @author UnAfraid
 */
public class RequestCrystallizeEstimate implements IClientIncomingPacket
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
		final PlayerInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final CrystallizationRequest request = activeChar.getRequest(CrystallizationRequest.class);
		if (request != null)
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_CRYSTALLIZING);
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
		
		final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if ((item == null) || item.isShadowItem() || item.isTimeLimitedItem())
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (item.isHeroItem())
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (_count > item.getCount())
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!item.getItem().isCrystallizable() || (item.getItem().getCrystalCount() <= 0) || (item.getItem().getCrystalType() == CrystalType.NONE))
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		if (!activeChar.getInventory().canManipulateWithItemId(item.getId()))
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		// Check if the char can crystallize items and return if false;
		if (activeChar.getCrystallizeGrade().ordinal() < item.getItem().getItemGrade().ordinal())
		{
			client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Show crystallization rewards window.
		final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(item);
		if ((crystallizationRewards != null) && !crystallizationRewards.isEmpty())
		{
			activeChar.addRequest(new CrystallizationRequest(activeChar, _objectId, _count));
			client.sendPacket(new ExGetCrystalizingEstimation(crystallizationRewards));
		}
		else
		{
			client.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
		}
		
	}
}
