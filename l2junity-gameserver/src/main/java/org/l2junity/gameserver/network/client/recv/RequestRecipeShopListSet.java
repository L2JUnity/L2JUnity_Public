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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.impl.RecipeData;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.enums.PrivateStoreType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.RecipeHolder;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.itemcontainer.ItemContainer;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.RecipeShopMsg;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2junity.gameserver.util.Broadcast;
import org.l2junity.gameserver.util.Util;
import org.l2junity.network.PacketReader;

/**
 * RequestRecipeShopListSet client packet class.
 */
public final class RequestRecipeShopListSet implements IClientIncomingPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private Map<Integer, Long> _manufactureRecipes = null;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		int count = packet.readD();
		if ((count <= 0) || (count > PlayerConfig.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != packet.getReadableBytes()))
		{
			return false;
		}
		
		_manufactureRecipes = new HashMap<>(count);
		for (int i = 0; i < count; i++)
		{
			int id = packet.readD();
			long cost = packet.readQ();
			if (cost < 0)
			{
				_manufactureRecipes = null;
				return false;
			}
			_manufactureRecipes.put(id, cost);
		}
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
		
		if (_manufactureRecipes == null)
		{
			player.sendPacket(SystemMessageId.ITEMS_ARE_NOT_AVAILABLE_FOR_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.setPrivateStoreType(PrivateStoreType.NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return;
		}
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.CURRENTLY_CRAFTING_AN_ITEM_PLEASE_WAIT);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			client.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		for (Entry<Integer, Long> item : _manufactureRecipes.entrySet())
		{
			final int recipeId = item.getKey();
			final long recipeCost = item.getValue();
			final RecipeHolder recipe = RecipeData.getInstance().getRecipe(recipeId);
			if (recipe == null)
			{
				player.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
				return;
			}
			if (ItemTable.getInstance().getTemplate(recipe.getItemId()).isQuestItem())
			{
				player.sendPacket(SystemMessageId.QUEST_RECIPES_CAN_NOT_BE_REGISTERED);
				return;
			}
			if (!player.hasRecipeList(recipe.getId()))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Player " + player.getName() + " of account " + player.getAccountName() + " tried to set recipe which he dont have.", GeneralConfig.DEFAULT_PUNISH);
				return;
			}
			
			if (!ItemContainer.validateCount(Inventory.ADENA_ID, recipeCost))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price of " + recipeCost + " adena in Private Manufacture.", GeneralConfig.DEFAULT_PUNISH);
				return;
			}
		}
		
		player.setManufactureItems(_manufactureRecipes);
		
		player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
		player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
	}
}
