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
package org.l2junity.gameserver.network.client.send;

import static org.l2junity.gameserver.data.xml.impl.MultisellData.PAGE_SIZE;

import org.l2junity.gameserver.datatables.ItemTable;
import org.l2junity.gameserver.model.ItemInfo;
import org.l2junity.gameserver.model.holders.ItemChanceHolder;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.holders.MultisellEntryHolder;
import org.l2junity.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

public final class MultiSellList extends AbstractItemPacket
{
	private int _size, _index;
	private final PreparedMultisellListHolder _list;
	private final boolean _finished;
	
	public MultiSellList(PreparedMultisellListHolder list, int index)
	{
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MULTI_SELL_LIST.writeId(packet);
		
		packet.writeC(0x00);
		packet.writeD(_list.getId()); // list id
		packet.writeC(0x00); // GOD Unknown
		packet.writeD(1 + (_index / PAGE_SIZE)); // page started from 1
		packet.writeD(_finished ? 0x01 : 0x00); // finished
		packet.writeD(PAGE_SIZE); // size of pages
		packet.writeD(_size); // list length
		packet.writeC(0x00);
		packet.writeC(_list.isChanceMultisell() ? 0x01 : 0x00); // new multisell window
		packet.writeD(0x20); // Always 32 oO
		
		while (_size-- > 0)
		{
			final ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
			final MultisellEntryHolder entry = _list.getEntries().get(_index++);
			
			packet.writeD(_index); // Entry ID. Start from 1.
			packet.writeC(entry.isStackable() ? 1 : 0);
			
			// Those values will be passed down to MultiSellChoose packet.
			packet.writeH(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0); // enchant level
			writeItemAugment(packet, itemEnchantment);
			writeItemElemental(packet, itemEnchantment);
			writeItemEnsoulOptions(packet, itemEnchantment);
			
			packet.writeH(entry.getProducts().size());
			packet.writeH(entry.getIngredients().size());
			
			for (ItemChanceHolder product : entry.getProducts())
			{
				final L2Item template = ItemTable.getInstance().getTemplate(product.getId());
				final ItemInfo displayItemEnchantment = (_list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass())) ? itemEnchantment : null;
				
				packet.writeD(product.getId());
				if (template != null)
				{
					packet.writeQ(template.getBodyPart());
					packet.writeH(template.getType2());
				}
				else
				{
					packet.writeQ(0);
					packet.writeH(65535);
				}
				packet.writeQ(_list.getProductCount(product));
				packet.writeH(displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				packet.writeD((int) Math.ceil(product.getChance())); // chance
				writeItemAugment(packet, displayItemEnchantment);
				writeItemElemental(packet, displayItemEnchantment);
				writeItemEnsoulOptions(packet, displayItemEnchantment);
			}
			
			for (ItemHolder ingredient : entry.getIngredients())
			{
				final L2Item template = ItemTable.getInstance().getTemplate(ingredient.getId());
				final ItemInfo displayItemEnchantment = ((itemEnchantment != null) && (itemEnchantment.getItem().getId() == ingredient.getId())) ? itemEnchantment : null;
				
				packet.writeD(ingredient.getId());
				packet.writeH(template != null ? template.getType2() : 65535);
				packet.writeQ(_list.getIngredientCount(ingredient));
				packet.writeH(displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				writeItemAugment(packet, displayItemEnchantment);
				writeItemElemental(packet, displayItemEnchantment);
				writeItemEnsoulOptions(packet, displayItemEnchantment);
			}
		}
		return true;
	}
}
