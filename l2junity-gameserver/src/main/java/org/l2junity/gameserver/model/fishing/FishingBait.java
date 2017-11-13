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
package org.l2junity.gameserver.model.fishing;

import java.util.ArrayList;
import java.util.List;

import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.holders.CatchHolder;

/**
 * @author bit
 */
public class FishingBait
{
	private final int _id;
	private final long _fishingTime;
	private final long _fishingTimeWait;
	private final List<CatchHolder> _catches = new ArrayList<>();
	
	public FishingBait(StatsSet set)
	{
		_id = set.getInt("id");
		_fishingTime = set.getDuration("fishingTime").toMillis();
		_fishingTimeWait = set.getDuration("fishingTimeWait").toMillis();
	}
	
	public int getItemId()
	{
		return _id;
	}
	
	public long getFishingTime()
	{
		return _fishingTime;
	}
	
	public long getFishingTimeWait()
	{
		return _fishingTimeWait;
	}
	
	public List<CatchHolder> getCatches()
	{
		return _catches;
	}
	
	public void addCatch(CatchHolder holder)
	{
		_catches.add(holder);
	}
}
