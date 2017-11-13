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
package org.l2junity.gameserver.network.client.recv.ensoul;

import org.l2junity.gameserver.data.xml.impl.EnsoulData;
import org.l2junity.gameserver.enums.PrivateStoreType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.ensoul.EnsoulOption;
import org.l2junity.gameserver.model.ensoul.EnsoulStone;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.recv.IClientIncomingPacket;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.ensoul.ExEnsoulResult;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2junity.network.PacketReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class RequestItemEnsoul implements IClientIncomingPacket
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestItemEnsoul.class);
	private int _itemObjectId;
	private EnsoulItemOption[] _options;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_itemObjectId = packet.readD();
		int options = packet.readC();
		if ((options > 0) && (options <= 3))
		{
			_options = new EnsoulItemOption[options];
			for (int i = 0; i < options; i++)
			{
				final int slotType = packet.readC();
				final int position = packet.readC();
				final int soulCrystalObjectId = packet.readD();
				final int soulCrystalOption = packet.readD();
				if ((position > 0) && (position < 3) && ((slotType == 1) || (slotType == 2)))
				{
					_options[i] = new EnsoulItemOption(slotType, position, soulCrystalObjectId, soulCrystalOption);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final PlayerInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
			return;
		}
		else if (player.hasAbnormalType(AbnormalType.FREEZING))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_FROZEN);
		}
		else if (player.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_DEAD);
			return;
		}
		else if ((player.getActiveTradeList() != null) || player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_TRADING);
			return;
		}
		else if (player.hasAbnormalType(AbnormalType.PARALYZE))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_PETRIFIED);
			return;
		}
		else if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_FISHING);
			return;
		}
		else if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_SEATED);
			return;
		}
		else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_IN_BATTLE);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			LOGGER.warn("Player: {} attempting to ensoul item without having it!", player);
			return;
		}
		else if (!item.isEquipable())
		{
			LOGGER.warn("Player: {} attempting to ensoul non equippable item: {}!", player, item);
			return;
		}
		else if (!item.isWeapon())
		{
			LOGGER.warn("Player: {} attempting to ensoul item that's not a weapon: {}!", player, item);
			return;
		}
		else if (item.isCommonItem())
		{
			LOGGER.warn("Player: {} attempting to ensoul common item: {}!", player, item);
			return;
		}
		else if (item.isShadowItem())
		{
			LOGGER.warn("Player: {} attempting to ensoul shadow item: {}!", player, item);
			return;
		}
		else if (item.isHeroItem())
		{
			LOGGER.warn("Player: {} attempting to ensoul hero item: {}!", player, item);
			return;
		}
		
		if ((_options == null) || (_options.length == 0))
		{
			LOGGER.warn("Player: {} attempting to ensoul item without any special ability declared!", player);
			return;
		}
		
		int success = 0;
		final InventoryUpdate iu = new InventoryUpdate();
		for (EnsoulItemOption itemOption : _options)
		{
			final int position = itemOption.getPosition() - 1;
			final ItemInstance soulCrystal = player.getInventory().getItemByObjectId(itemOption.getSoulCrystalObjectId());
			if (soulCrystal == null)
			{
				player.sendPacket(SystemMessageId.INVALID_SOUL_CRYSTAL);
				continue;
			}
			
			final EnsoulStone stone = EnsoulData.getInstance().getStone(soulCrystal.getId());
			if (stone == null)
			{
				continue;
			}
			
			if (stone.getSlotType() != itemOption.getSlotType())
			{
				LOGGER.warn("Player: {} attempting to ensoul item with a wrong slot type!", player);
				continue;
			}
			
			if ((stone.getSlotType() == 2) && (position != 0))
			{
				LOGGER.warn("Player: {} attempting to ensoul item with a wrong position!", player);
				continue;
			}
			
			if (!stone.getOptions().contains(itemOption.getSoulCrystalOption()))
			{
				LOGGER.warn("Player: {} attempting to ensoul item option that stone doesn't contains!", player);
				continue;
			}
			
			final EnsoulOption option = EnsoulData.getInstance().getOption(itemOption.getSoulCrystalOption());
			if (option == null)
			{
				LOGGER.warn("Player: {} attempting to ensoul item option that doesn't exists!", player);
				continue;
			}
			
			boolean isResoul = itemOption.getSlotType() == 2 ? (item.getAdditionalSpecialAbility(position) != null) : (item.getSpecialAbility(position) != null);
			final ItemHolder fee = isResoul ? EnsoulData.getInstance().getResoulFee(item.getItem().getCrystalType(), position) : EnsoulData.getInstance().getEnsoulFee(item.getItem().getCrystalType(), position);
			
			if (fee == null)
			{
				LOGGER.warn("Player: {} attempting to ensoul item option that doesn't exists!", player);
				continue;
			}
			
			final ItemInstance gemStones = player.getInventory().getItemByItemId(fee.getId());
			if ((gemStones == null) || (gemStones.getCount() < fee.getCount()))
			{
				continue;
			}
			
			if (player.destroyItem("EnsoulOption", soulCrystal, 1, player, true) && player.destroyItem("EnsoulOption", gemStones, fee.getCount(), player, true))
			{
				item.addSpecialAbility(option, position, stone.getSlotType(), true);
				success = 1;
			}
			
			if (soulCrystal.getCount() == 0)
			{
				iu.addRemovedItem(soulCrystal);
			}
			else
			{
				iu.addModifiedItem(soulCrystal);
			}
			if (gemStones.getCount() == 0)
			{
				iu.addRemovedItem(gemStones);
			}
			else
			{
				iu.addModifiedItem(gemStones);
			}
			iu.addModifiedItem(item);
		}
		player.sendInventoryUpdate(iu);
		if (item.isEquipped())
		{
			item.applySpecialAbilities();
		}
		player.sendPacket(new ExEnsoulResult(success, item));
	}
	
	static class EnsoulItemOption
	{
		private final int _slotType;
		private final int _position;
		private final int _soulCrystalObjectId;
		private final int _soulCrystalOption;
		
		EnsoulItemOption(int slotType, int position, int soulCrystalObjectId, int soulCrystalOption)
		{
			_slotType = slotType;
			_position = position;
			_soulCrystalObjectId = soulCrystalObjectId;
			_soulCrystalOption = soulCrystalOption;
		}
		
		public int getSlotType()
		{
			return _slotType;
		}
		
		public int getPosition()
		{
			return _position;
		}
		
		public int getSoulCrystalObjectId()
		{
			return _soulCrystalObjectId;
		}
		
		public int getSoulCrystalOption()
		{
			return _soulCrystalOption;
		}
	}
}
