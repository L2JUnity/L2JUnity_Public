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
package org.l2junity.gameserver.geodata.pathfinding.empty;

import java.util.Collections;
import java.util.List;

import org.l2junity.gameserver.geodata.pathfinding.AbstractNodeLoc;
import org.l2junity.gameserver.geodata.pathfinding.PathFinding;
import org.l2junity.gameserver.model.instancezone.Instance;

/**
 * @author lord_rex
 */
public final class EmptyPathFinding extends PathFinding
{
	public EmptyPathFinding()
	{
	}
	
	@Override
	public void load()
	{
	}
	
	@Override
	public boolean pathNodesExist(short regionoffset)
	{
		return false;
	}
	
	@Override
	public List<AbstractNodeLoc> findPath(double x, double y, double z, double tx, double ty, double tz, Instance instance, boolean playable)
	{
		return Collections.emptyList();
	}
}