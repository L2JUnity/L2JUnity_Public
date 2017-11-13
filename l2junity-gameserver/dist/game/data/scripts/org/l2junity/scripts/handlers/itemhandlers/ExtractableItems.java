/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.itemhandlers;

import java.util.List;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.config.RatesConfig;
import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.handler.IItemHandler;
import org.l2junity.gameserver.handler.ItemHandler;
import org.l2junity.gameserver.model.ExtractableProduct;
import org.l2junity.gameserver.model.actor.Playable;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.EtcItem;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.send.InventoryUpdate;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Extractable Items handler.
 * @author HorridoJoho
 */
public class ExtractableItems implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final PlayerInstance activeChar = playable.getActingPlayer();
		final EtcItem etcitem = (EtcItem) item.getItem();
		final List<ExtractableProduct> exitem = etcitem.getExtractableItems();
		if (exitem == null)
		{
			LOGGER.info("No extractable data defined for " + etcitem);
			return false;
		}
		
		// destroy item
		if (!activeChar.destroyItem("Extract", item.getObjectId(), 1, activeChar, true))
		{
			return false;
		}
		
		boolean created = false;
		final InventoryUpdate iu = new InventoryUpdate();
		for (ExtractableProduct expi : exitem)
		{
			if (Rnd.get(100000) <= expi.getChance())
			{
				final int min = (int) (expi.getMin() * RatesConfig.RATE_EXTRACTABLE);
				final int max = (int) (expi.getMax() * RatesConfig.RATE_EXTRACTABLE);
				
				final int createItemAmount = (max == min) ? min : (Rnd.get((max - min) + 1) + min);
				if (createItemAmount == 0)
				{
					continue;
				}
				
				final L2Item itemTemplate = ItemTable.getInstance().getTemplate(expi.getId());
				if (itemTemplate == null)
				{
					continue;
				}
				
				if (itemTemplate.isStackable() || (createItemAmount == 1))
				{
					final ItemInstance createdItem = activeChar.addItem("Extract", expi.getId(), createItemAmount, activeChar, true, false);
					if (expi.getEnchant() > 0)
					{
						createdItem.setEnchantLevel(expi.getEnchant());
					}
					iu.addItem(createdItem);
				}
				else
				{
					for (int i = 0; i < createItemAmount; i++)
					{
						ItemInstance createdItem = activeChar.addItem("Extract", expi.getId(), 1, activeChar, true, false);
						if (expi.getEnchant() > 0)
						{
							createdItem.setEnchantLevel(expi.getEnchant());
						}
						iu.addItem(createdItem);
					}
				}
				created = true;
			}
		}
		
		if (!created)
		{
			activeChar.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
		}
		else
		{
			activeChar.sendInventoryUpdate(iu);
		}
		return true;
	}
	
	@GameScript
	public static void main()
	{
		ItemHandler.getInstance().registerHandler(new ExtractableItems());
	}
}