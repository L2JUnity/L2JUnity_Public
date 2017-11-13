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

import java.util.concurrent.ScheduledFuture;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.enums.SuperpointMoveType;
import org.l2junity.gameserver.instancemanager.SuperpointManager;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;

/**
 * Holds info about current walk progress
 * @author GKR, UnAfraid
 */
public class SuperpointInfo
{
	private final Superpoint _route;
	
	private ScheduledFuture<?> _walkCheckTask;
	private boolean _blocked = false;
	private boolean _suspended = false;
	private boolean _stoppedByAttack = false;
	private int _currentNode = 0;
	private boolean _forward = true; // Determines first --> last or first <-- last direction
	private long _lastActionTime; // Debug field
	
	public SuperpointInfo(Superpoint route)
	{
		_route = route;
	}
	
	/**
	 * @return name of route of this WalkInfo.
	 */
	public Superpoint getRoute()
	{
		return _route;
	}
	
	/**
	 * @return current node of this WalkInfo.
	 */
	public SuperpointNode getCurrentNode()
	{
		return _route.getNode(_currentNode);
	}
	
	/**
	 * Calculate next node for this WalkInfo and send debug message from given npc
	 * @param npc NPC to debug message to be sent from
	 * @return
	 */
	public boolean calculateNextNode(Npc npc)
	{
		if (_route.getMoveType() == SuperpointMoveType.MoveSuperPoint_Random)
		{
			int newNode = _currentNode;
			
			while (newNode == _currentNode)
			{
				newNode = Rnd.get(_route.getNodeSize());
			}
			
			_currentNode = newNode;
			
			npc.sendDebugMessage("Route: " + _route.getAlias() + ", next random node is " + _currentNode, DebugType.WALKER);
		}
		else
		{
			_currentNode = _forward ? _currentNode + 1 : _currentNode - 1;
			
			if (_currentNode == _route.getNodeSize())
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnNpcMoveRouteFinished(npc), npc);
				npc.sendDebugMessage("Route: " + getRoute().getAlias() + ", last node arrived", DebugType.WALKER);
				if (_route.getMoveType() == SuperpointMoveType.MoveSuperPoint_StopRail)
				{
					SuperpointManager.getInstance().cancelMoving(npc);
					return false;
				}
				if (_route.isCycling())
				{
					_currentNode = 0;
				}
				else
				{
					_forward = false;
					_currentNode -= 1;
				}
			}
			
			else if (_currentNode == -1)
			{
				_currentNode = 1;
				_forward = true;
			}
		}
		return true;
	}
	
	/**
	 * @return {@code true} if walking task is blocked, {@code false} otherwise,
	 */
	public boolean isBlocked()
	{
		return _blocked;
	}
	
	/**
	 * @param val
	 */
	public void setBlocked(boolean val)
	{
		_blocked = val;
	}
	
	/**
	 * @return {@code true} if walking task is suspended, {@code false} otherwise,
	 */
	public boolean isSuspended()
	{
		return _suspended;
	}
	
	/**
	 * @param val
	 */
	public void setSuspended(boolean val)
	{
		_suspended = val;
	}
	
	/**
	 * @return {@code true} if walking task shall be stopped by attack, {@code false} otherwise,
	 */
	public boolean isStoppedByAttack()
	{
		return _stoppedByAttack;
	}
	
	/**
	 * @param val
	 */
	public void setStoppedByAttack(boolean val)
	{
		_stoppedByAttack = val;
	}
	
	/**
	 * @return the id of the current node in this walking task.
	 */
	public int getCurrentNodeId()
	{
		return _currentNode;
	}
	
	/**
	 * @return {@code long} last action time used only for debugging.
	 */
	public long getLastAction()
	{
		return _lastActionTime;
	}
	
	/**
	 * @param val
	 */
	public void setLastAction(long val)
	{
		_lastActionTime = val;
	}
	
	/**
	 * @return walking check task.
	 */
	public ScheduledFuture<?> getWalkCheckTask()
	{
		return _walkCheckTask;
	}
	
	/**
	 * @param val walking check task.
	 */
	public void setWalkCheckTask(ScheduledFuture<?> val)
	{
		_walkCheckTask = val;
	}
	
	public boolean isRunning()
	{
		return _route.isRunning();
	}
}