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
package org.l2junity.gameserver.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.datatables.SpawnTable;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldRegion
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WorldRegion.class);
	
	private volatile Map<Integer, WorldObject> _visibleObjects;
	private final short _regionX;
	private final short _regionY;
	private final short _regionZ;
	private boolean _active = false;
	private ScheduledFuture<?> _neighborsTask = null;
	
	public WorldRegion(short regionX, short regionY, short regionZ)
	{
		_regionX = regionX;
		_regionY = regionY;
		_regionZ = regionZ;
		_active = GeneralConfig.GRIDS_ALWAYS_ON;
	}
	
	public class NeighborsTask implements Runnable
	{
		private final boolean _isActivating;
		
		public NeighborsTask(boolean isActivating)
		{
			_isActivating = isActivating;
		}
		
		@Override
		public void run()
		{
			forEachSurroundingRegion(w ->
			{
				if (_isActivating || w.areNeighborsEmpty())
				{
					w.setActive(_isActivating);
				}
				return true;
			});
		}
	}
	
	private void switchAI(boolean isOn)
	{
		if (_visibleObjects == null)
		{
			return;
		}
		
		int c = 0;
		if (!isOn)
		{
			for (WorldObject o : _visibleObjects.values())
			{
				if (o instanceof Attackable)
				{
					c++;
					Attackable mob = (Attackable) o;
					
					mob.setTarget(null);
					mob.stopMove(null);
					mob.stopAllEffects();
					
					mob.clearAggroList();
					mob.getAttackByList().clear();
					
					if (mob.hasAI())
					{
						mob.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						mob.getAI().stopAITask();
					}
				}
				else if (o instanceof Vehicle)
				{
					c++;
				}
			}
			LOGGER.debug("{} mobs were turned off", c);
		}
		else
		{
			for (WorldObject o : _visibleObjects.values())
			{
				if (o instanceof Attackable)
				{
					c++;
					((Attackable) o).getStatus().startHpMpRegeneration();
				}
				else if (o instanceof Npc)
				{
					((Npc) o).startRandomAnimationTask();
				}
			}
			LOGGER.debug("{} mobs were turned on", c);
		}
	}
	
	public boolean isActive()
	{
		return _active;
	}
	
	public boolean areNeighborsEmpty()
	{
		return forEachSurroundingRegion(w -> !(w.isActive() && w.getVisibleObjects().values().stream().anyMatch(WorldObject::isPlayable)));
	}
	
	public void setActive(boolean active)
	{
		if (_active == active)
		{
			return;
		}
		
		_active = active;
		
		switchAI(active);
		
		LOGGER.debug("{} Grid {}", active ? "Starting" : "Stopping", this);
	}
	
	private void startActivation()
	{
		setActive(true);
		
		synchronized (this)
		{
			if (_neighborsTask != null)
			{
				_neighborsTask.cancel(true);
				_neighborsTask = null;
			}
			_neighborsTask = ThreadPool.schedule(new NeighborsTask(true), 1000 * GeneralConfig.GRID_NEIGHBOR_TURNON_TIME, TimeUnit.MILLISECONDS);
		}
	}
	
	private void startDeactivation()
	{
		synchronized (this)
		{
			if (_neighborsTask != null)
			{
				_neighborsTask.cancel(true);
				_neighborsTask = null;
			}
			_neighborsTask = ThreadPool.schedule(new NeighborsTask(false), 1000 * GeneralConfig.GRID_NEIGHBOR_TURNOFF_TIME, TimeUnit.MILLISECONDS);
		}
	}
	
	public void addVisibleObject(WorldObject object)
	{
		if (_visibleObjects == null)
		{
			synchronized (this)
			{
				if (_visibleObjects == null)
				{
					_visibleObjects = new ConcurrentHashMap<>();
				}
			}
		}

		_visibleObjects.put(object.getObjectId(), object);
		
		if (object.isPlayable())
		{
			if (!isActive() && (!GeneralConfig.GRIDS_ALWAYS_ON))
			{
				startActivation();
			}
		}
	}
	
	public void removeVisibleObject(WorldObject object)
	{
		if (_visibleObjects == null)
		{
			return;
		}
		_visibleObjects.remove(object.getObjectId());
		
		if (object.isPlayable())
		{
			if (areNeighborsEmpty() && !GeneralConfig.GRIDS_ALWAYS_ON)
			{
				startDeactivation();
			}
		}
	}
	
	public Map<Integer, WorldObject> getVisibleObjects()
	{
		return _visibleObjects != null ? _visibleObjects : Collections.emptyMap();
	}
	
	/**
	 * Deleted all spawns in the world.
	 */
	public void deleteVisibleNpcSpawns()
	{
		if (_visibleObjects == null)
		{
			return;
		}
		
		LOGGER.debug("Deleting all visible NPC's in Region: {}", this);
		for (WorldObject obj : _visibleObjects.values())
		{
			if (obj instanceof Npc)
			{
				Npc target = (Npc) obj;
				target.deleteMe();
				L2Spawn spawn = target.getSpawn();
				if (spawn != null)
				{
					spawn.stopRespawn();
					SpawnTable.getInstance().deleteSpawn(spawn, false);
				}
				LOGGER.trace("Removed NPC {}", target);
			}
		}
		LOGGER.info("All visible NPC's deleted in Region: {}", this);
	}
	
	public boolean forEachSurroundingRegion(Predicate<WorldRegion> p)
	{
		for (int x = _regionX - 1; x <= (_regionX + 1); x++)
		{
			for (int y = _regionY - 1; y <= (_regionY + 1); y++)
			{
				for (int z = _regionZ - 1; z <= (_regionZ + 1); z++)
				{
					if (World.validRegion(x, y, z))
					{
						final WorldRegion worldRegion = World.getInstance().getWorldRegions()[x][y][z];
						if (!p.test(worldRegion))
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public short getRegionX()
	{
		return _regionX;
	}
	
	public short getRegionY()
	{
		return _regionY;
	}
	
	public short getRegionZ()
	{
		return _regionZ;
	}
	
	public boolean isSurroundingRegion(WorldRegion region)
	{
		return (region != null) && (getRegionX() >= (region.getRegionX() - 1)) && (getRegionX() <= (region.getRegionX() + 1)) && (getRegionY() >= (region.getRegionY() - 1)) && (getRegionY() <= (region.getRegionY() + 1)) && (getRegionZ() >= (region.getRegionZ() - 1)) && (getRegionZ() <= (region.getRegionZ() + 1));
	}
	
	@Override
	public String toString()
	{
		return "(" + _regionX + ", " + _regionY + ", " + _regionZ + ")";
	}
}
