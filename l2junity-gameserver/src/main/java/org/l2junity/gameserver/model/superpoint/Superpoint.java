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

import java.util.ArrayList;
import java.util.List;

import org.l2junity.gameserver.enums.SuperpointMoveType;
import org.l2junity.gameserver.model.StatsSet;

/**
 * @author Sdw
 */
public class Superpoint
{
	private final String _alias;
	private final SuperpointMoveType _moveType;
	private final boolean _isRunning;
	private final boolean _isCycling;
	private final List<SuperpointNode> _nodes = new ArrayList<>();
	
	public Superpoint(StatsSet set)
	{
		_alias = set.getString("alias");
		_moveType = set.getEnum("move_type", SuperpointMoveType.class);
		_isRunning = set.getBoolean("isRunning", false);
		_isCycling = set.getBoolean("cycling", false);
	}
	
	public void addNode(SuperpointNode node)
	{
		_nodes.add(node);
	}
	
	public String getAlias()
	{
		return _alias;
	}
	
	public SuperpointMoveType getMoveType()
	{
		return _moveType;
	}
	
	public int getNodeSize()
	{
		return _nodes.size();
	}
	
	public boolean isRunning()
	{
		return _isRunning;
	}
	
	public boolean isCycling()
	{
		return _isCycling;
	}
	
	public SuperpointNode getNode(int id)
	{
		return _nodes.stream().filter(node -> node.getNodeId() == id).findFirst().orElse(null);
	}
}
