/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.data.xml.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.enums.FenceState;
import org.l2junity.gameserver.geodata.pathfinding.AbstractNodeLoc;
import org.l2junity.gameserver.instancemanager.MapRegionManager;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.MapRegion;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.instance.FenceInstance;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author HoridoJoho / FBIagent
 */
public final class FenceData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FenceData.class);
	
	private static final int MAX_Z_DIFF = 100;
	
	private final Map<Integer, List<FenceInstance>> _regions = new ConcurrentHashMap<>();
	private final Map<Integer, FenceInstance> _fences = new ConcurrentHashMap<>();
	
	protected FenceData()
	{
	}
	
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = MapRegionManager.class))
	private void load() throws Exception
	{
		if (!_fences.isEmpty())
		{
			// Remove old fences when reloading
			_fences.values().forEach(this::removeFence);
		}
		
		parseDatapackFile("data/FenceData.xml");
		LOGGER.info("Loaded {} Fences.", _fences.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "fence", this::spawnFence));
	}
	
	public int getLoadedElementsCount()
	{
		return _fences.size();
	}
	
	private void spawnFence(Node fenceNode)
	{
		final StatsSet set = new StatsSet(parseAttributes(fenceNode));
		spawnFence(set.getInt("x"), set.getInt("y"), set.getInt("z"), set.getInt("width"), set.getInt("length"), set.getInt("height"), 0, set.getEnum("state", FenceState.class, FenceState.CLOSED));
	}
	
	public void spawnFence(int x, int y, int z, int width, int length, int height, int instanceId, FenceState state)
	{
		final MapRegion region = MapRegionManager.getInstance().getMapRegion(x, y);
		final FenceInstance fence = new FenceInstance(x, y, region != null ? region.getName() : null, width, length, height, state);
		if (instanceId > 0)
		{
			fence.setInstanceById(instanceId);
		}
		fence.spawnMe(x, y, z);
		addFence(fence);
	}
	
	private void addFence(FenceInstance fence)
	{
		_fences.put(fence.getObjectId(), fence);
		_regions.computeIfAbsent(MapRegionManager.getInstance().getMapRegionLocId(fence), key -> new ArrayList<>()).add(fence);
	}
	
	public void removeFence(FenceInstance fence)
	{
		_fences.remove(fence.getObjectId());
		
		final List<FenceInstance> fencesInRegion = _regions.get(MapRegionManager.getInstance().getMapRegionLocId(fence));
		if (fencesInRegion != null)
		{
			fencesInRegion.remove(fence);
		}
	}
	
	public Map<Integer, FenceInstance> getFences()
	{
		return _fences;
	}
	
	public FenceInstance getFence(int objectId)
	{
		return _fences.get(objectId);
	}

	public boolean checkIfFenceBetween(AbstractNodeLoc start, AbstractNodeLoc end, Instance instance)
	{
		return checkIfFenceBetween(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ(), instance);
	}
	
	public boolean checkIfFenceBetween(double x, double y, double z, double tx, double ty, double tz, Instance instance)
	{
		final Predicate<FenceInstance> filter = fence ->
		{
			// Check if fence is geodata enabled.
			if (!fence.getState().isGeodataEnabled())
			{
				return false;
			}
			
			// Check if fence is within the instance we search for.
			final int instanceId = (instance == null) ? 0 : instance.getId();
			if (fence.getInstanceId() != instanceId)
			{
				return false;
			}
			
			final double xMin = fence.getXMin();
			final double xMax = fence.getXMax();
			final double yMin = fence.getYMin();
			final double yMax = fence.getYMax();
			if ((x < xMin) && (tx < xMin))
			{
				return false;
			}
			if ((x > xMax) && (tx > xMax))
			{
				return false;
			}
			if ((y < yMin) && (ty < yMin))
			{
				return false;
			}
			if ((y > yMax) && (ty > yMax))
			{
				return false;
			}
			if ((x > xMin) && (tx > xMin) && (x < xMax) && (tx < xMax))
			{
				if ((y > yMin) && (ty > yMin) && (y < yMax) && (ty < yMax))
				{
					return false;
				}
			}
			
			if (crossLinePart(xMin, yMin, xMax, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMin, xMax, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMax, xMin, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMin, yMax, xMin, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax))
			{
				if ((z > (fence.getZ() - MAX_Z_DIFF)) && (z < (fence.getZ() + MAX_Z_DIFF)))
				{
					return true;
				}
			}
			
			return false;
		};
		
		return _regions.getOrDefault(MapRegionManager.getInstance().getMapRegionLocId((int) x, (int) y), Collections.emptyList()).stream().anyMatch(filter);
	}
	
	private boolean crossLinePart(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double xMin, double yMin, double xMax, double yMax)
	{
		final double[] result = intersection(x1, y1, x2, y2, x3, y3, x4, y4);
		if (result == null)
		{
			return false;
		}
		
		final double xCross = result[0];
		final double yCross = result[1];
		if ((xCross <= xMax) && (xCross >= xMin))
		{
			return true;
		}
		if ((yCross <= yMax) && (yCross >= yMin))
		{
			return true;
		}
		
		return false;
	}
	
	private double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
	{
		final double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
		if (d == 0)
		{
			return null;
		}
		
		final double xi = (((x3 - x4) * ((x1 * y2) - (y1 * x2))) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))) / d;
		final double yi = (((y3 - y4) * ((x1 * y2) - (y1 * x2))) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))) / d;
		
		return new double[]
		{
			xi,
			yi
		};
	}
	
	@InstanceGetter
	public static FenceData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FenceData INSTANCE = new FenceData();
	}
}