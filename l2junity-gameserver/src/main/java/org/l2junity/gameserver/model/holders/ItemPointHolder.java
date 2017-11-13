/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.gameserver.model.holders;

import org.l2junity.gameserver.model.StatsSet;

/**
 * @author Sdw
 */
public class ItemPointHolder extends ItemHolder
{
	private static final long serialVersionUID = -4083112025688475858L;
	private final int _points;
	
	public ItemPointHolder(StatsSet params)
	{
		this(params.getInt("id"), params.getLong("count"), params.getInt("points"));
	}
	
	public ItemPointHolder(int id, long count, int points)
	{
		super(id, count);
		_points = points;
	}
	
	/**
	 * Gets the point.
	 * @return the number of point to get the item
	 */
	public int getPoints()
	{
		return _points;
	}
	
	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName() + "] ID: " + getId() + ", count: " + getCount() + ", points: " + _points;
	}
}
