/*
 * Copyright (C) 2004-2017 L2J Unity
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

import java.io.Serializable;

import org.l2junity.commons.util.TimeUtil;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.interfaces.IIdentifiable;

/**
 * @author Sdw
 */
public class FishingRod implements IIdentifiable, Serializable
{
	private static final long serialVersionUID = 7997508947507139746L;
	private final int _id;
	private final long _reduceFishingTime;
	
	public FishingRod(StatsSet set)
	{
		_id = set.getInt("id");
		_reduceFishingTime = TimeUtil.parseDuration(set.getString("reduceFishingTime")).toMillis();
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	public long getReduceFishingTime()
	{
		return _reduceFishingTime;
	}
}
