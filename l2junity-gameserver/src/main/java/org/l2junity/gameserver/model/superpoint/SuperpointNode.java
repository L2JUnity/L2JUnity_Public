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
package org.l2junity.gameserver.model.superpoint;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;

/**
 * @author Sdw
 */
public class SuperpointNode extends Location
{
	private static final long serialVersionUID = 7383275335261090301L;
	private final int _id;
	private final int _fStringId;
	private final int _socialId;
	private final int _delay;
	
	public SuperpointNode(StatsSet set)
	{
		super(set.getInt("x"), set.getInt("y"), set.getInt("z"));
		_id = set.getInt("id");
		_fStringId = set.getInt("fstring_index", -1);
		_socialId = set.getInt("social_number", -1);
		_delay = set.getInt("delay", -1);
	}
	
	public int getFStringId()
	{
		return _fStringId;
	}
	
	public int getSocialId()
	{
		return _socialId;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public int getNodeId()
	{
		return _id;
	}
}
