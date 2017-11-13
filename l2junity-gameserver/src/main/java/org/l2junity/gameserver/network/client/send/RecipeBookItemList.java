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

import java.util.Collection;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.RecipeHolder;
import org.l2junity.gameserver.network.client.OutgoingPackets;
import org.l2junity.network.PacketWriter;

public class RecipeBookItemList implements IClientOutgoingPacket
{
	private final Collection<RecipeHolder> _recipes;
	private final boolean _isDwarvenCraft;
	private final int _maxMp;
	
	public RecipeBookItemList(PlayerInstance player, boolean isDwarvenCraft)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_maxMp = player.getMaxMp();
		_recipes = (isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RECIPE_BOOK_ITEM_LIST.writeId(packet);
		
		packet.writeD(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
		packet.writeD(_maxMp);
		
		if (_recipes == null)
		{
			packet.writeD(0);
		}
		else
		{
			packet.writeD(_recipes.size()); // number of items in recipe book
			int i = 1;
			for (RecipeHolder recipe : _recipes)
			{
				packet.writeD(recipe.getId());
				packet.writeD(i++);
			}
		}
		return true;
	}
}
