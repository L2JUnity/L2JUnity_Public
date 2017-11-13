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
package org.l2junity.gameserver.model.items.enchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.l2junity.gameserver.data.xml.impl.EnchantItemGroupsData;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.items.L2Item;

/**
 * @author UnAfraid
 */
public final class EnchantScrollGroup
{
	private final int _id;
	private final int _extendId;
	private List<EnchantRateItem> _rateGroups;
	
	public EnchantScrollGroup(StatsSet set)
	{
		_id = set.getInt("id");
		_extendId = set.getInt("extend", 0);
	}
	
	/**
	 * Adds new rate group.
	 * @param group
	 */
	public void addRateGroup(EnchantRateItem group)
	{
		if (_rateGroups == null)
		{
			_rateGroups = new ArrayList<>();
		}
		_rateGroups.add(group);
	}
	
	/**
	 * @return {@code List} of all enchant rate items, Empty list if none.
	 */
	public List<EnchantRateItem> getRateGroups()
	{
		return Stream.concat(_extendId != 0 ? EnchantItemGroupsData.getInstance().getScrollGroup(_extendId).getRateGroups().stream() : Collections.<EnchantRateItem> emptyList().stream(), _rateGroups != null ? _rateGroups.stream() : Collections.<EnchantRateItem> emptyList().stream()).collect(Collectors.toList());
	}
	
	/**
	 * @param item
	 * @return {@link EnchantRateItem}, {@code NULL} in case non of rate items can be used with.
	 */
	public EnchantRateItem getRateGroup(L2Item item)
	{
		for (EnchantRateItem group : getRateGroups())
		{
			if (group.validate(item))
			{
				return group;
			}
		}
		return null;
	}
	
	/**
	 * @return id of current enchant scroll group.
	 */
	public int getId()
	{
		return _id;
	}
}
