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

import org.l2junity.gameserver.enums.ItemSkillType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.request.EnchantItemRequest;
import org.l2junity.gameserver.model.holders.ItemSkillHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.SkillConditionScope;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ExPutEnchantScrollItemResult;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.network.PacketReader;

/**
 * @author Sdw
 */
public class RequestExAddEnchantScrollItem implements IClientIncomingPacket
{
	private int _scrollObjectId;
	private int _enchantObjectId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_scrollObjectId = packet.readD();
		_enchantObjectId = packet.readD();
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
		
		final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
		if ((request == null) || request.isProcessing())
		{
			return;
		}
		
		request.setEnchantingItem(_enchantObjectId);
		request.setEnchantingScroll(_scrollObjectId);
		
		final ItemInstance item = request.getEnchantingItem();
		final ItemInstance scroll = request.getEnchantingScroll();
		if ((item == null) || (scroll == null))
		{
			// message may be custom
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
			request.setEnchantingItem(PlayerInstance.ID_NONE);
			request.setEnchantingScroll(PlayerInstance.ID_NONE);
			return;
		}
		
		final List<ItemSkillHolder> skills = item.getItem().getSkills(ItemSkillType.NORMAL);
		
		if (!skills.stream().allMatch(skill -> skill.getSkill().checkConditions(SkillConditionScope.GENERAL, activeChar, item)))
		{
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
			request.setEnchantingScroll(PlayerInstance.ID_NONE);
			return;
		}
		
		request.setTimestamp(System.currentTimeMillis());
		activeChar.sendPacket(new ExPutEnchantScrollItemResult(_scrollObjectId));
	}
}
