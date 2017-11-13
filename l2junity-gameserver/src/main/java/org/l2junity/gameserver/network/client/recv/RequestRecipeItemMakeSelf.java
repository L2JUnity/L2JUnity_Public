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

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.impl.RecipeData;
import org.l2junity.gameserver.enums.PrivateStoreType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.holders.RecipeHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.RecipeItemMakeInfo;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.Util;
import org.l2junity.network.PacketReader;

/**
 * @author Nik
 */
public final class RequestRecipeItemMakeSelf implements IClientIncomingPacket
{
	private int _id;
	private ItemHolder[] _offeredItems;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_id = packet.readD();
		
		final int offeringsCount = packet.readD();
		if (offeringsCount > 0)
		{
			_offeredItems = new ItemHolder[offeringsCount];
			for (int i = 0; i < offeringsCount; i++)
			{
				final int objectId = packet.readD();
				final long count = packet.readQ();
				_offeredItems[i] = new ItemHolder(objectId, count);
			}
		}
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
		
		if (!PlayerConfig.IS_CRAFTING_ENABLED)
		{
			activeChar.sendMessage("Item creation is currently disabled.");
			return;
		}
		
		if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf"))
		{
			return;
		}
		
		if (activeChar.isCastingNow())
		{
			activeChar.sendPacket(SystemMessageId.YOUR_RECIPE_BOOK_MAY_NOT_BE_ACCESSED_WHILE_USING_A_SKILL);
			return;
		}
		
		if (activeChar.getPrivateStoreType() == PrivateStoreType.MANUFACTURE)
		{
			activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
			return;
		}
		
		if (activeChar.isProcessingTransaction())
		{
			activeChar.sendPacket(SystemMessageId.ITEM_CREATION_IS_NOT_POSSIBLE_WHILE_ENGAGED_IN_A_TRADE);
			return;
		}
		
		// TODO: Check if its a retail-like check.
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		// On retail if player is requesting trade, it is instantly canceled.
		activeChar.cancelActiveTrade();
		
		final RecipeHolder recipe = RecipeData.getInstance().getRecipe(_id);
		if (recipe == null)
		{
			activeChar.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
			return;
		}
		
		if (!activeChar.hasRecipeList(recipe.getId()))
		{
			Util.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false recipe id.", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		// Check if stats or ingredients are met.
		if (!recipe.checkNecessaryStats(activeChar, activeChar, true) || !recipe.checkNecessaryIngredients(activeChar, true))
		{
			return;
		}
		
		// Check if all offerings are legit.
		if ((_offeredItems != null) && (recipe.getMaxOffering() > 0) && (recipe.getMaxOfferingBonus() > 0))
		{
			for (ItemHolder offer : _offeredItems)
			{
				final ItemInstance item = activeChar.getInventory().getItemByObjectId(offer.getId());
				if ((item == null) || (item.getCount() < offer.getCount()) || !item.isDestroyable())
				{
					return;
				}
			}
		}
		
		if (activeChar.isCrafting())
		{
			activeChar.sendPacket(SystemMessageId.CURRENTLY_CRAFTING_AN_ITEM_PLEASE_WAIT);
			return;
		}
		
		activeChar.setIsCrafting(true);
		
		// Take offerings to increase chance
		double offeringBonus = 0;
		if ((_offeredItems != null) && (recipe.getMaxOffering() > 0) && (recipe.getMaxOfferingBonus() > 0))
		{
			long offeredAdenaWorth = 0;
			for (ItemHolder offer : _offeredItems)
			{
				final ItemInstance item = activeChar.getInventory().getItemByObjectId(offer.getId());
				if (activeChar.destroyItem("CraftOffering", item, offer.getCount(), null, true))
				{
					offeredAdenaWorth += (item.getItem().getReferencePrice() * offer.getCount());
				}
			}
			
			offeringBonus = Math.min((offeredAdenaWorth / recipe.getMaxOffering()) * recipe.getMaxOfferingBonus(), recipe.getMaxOfferingBonus());
		}
		
		final boolean success = activeChar.tryLuck() || ((recipe.getSuccessRate() + offeringBonus) > Rnd.get(100));
		final boolean craftingCritical = success && (activeChar.getStat().getValue(DoubleStat.CRAFTING_CRITICAL) > Rnd.get(100));
		
		if (success) // Successful craft.
		{
			if (craftingCritical)
			{
				activeChar.sendPacket(SystemMessageId.CRAFTING_CRITICAL);
			}
		}
		else // Failed craft.
		{
			activeChar.sendPacket(SystemMessageId.YOU_FAILED_AT_MIXING_THE_ITEM);
		}
		
		// Perform the crafting: take the items and give reward if success.
		recipe.doCraft(activeChar, activeChar, success, craftingCritical, true);
		
		// Send craft window. Must be sent after crafting so it properly counts the items.
		activeChar.sendPacket(new RecipeItemMakeInfo(recipe.getId(), activeChar, success, recipe.getMaxOffering()));
		
		activeChar.setIsCrafting(false);
	}
}
