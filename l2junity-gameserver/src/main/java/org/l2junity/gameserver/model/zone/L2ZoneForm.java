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
package org.l2junity.gameserver.model.zone;

import java.awt.geom.Line2D;

import org.l2junity.gameserver.idfactory.IdFactory;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

/**
 * Abstract base class for any zone form
 * @author durgus
 */
public abstract class L2ZoneForm
{
	protected static final int STEP = 10;
	
	public abstract boolean isInsideZone(double x, double y, double z);
	
	public abstract boolean intersectsRectangle(double x1, double x2, double y1, double y2);
	
	public abstract double getDistanceToZone(double x, double y);
	
	public abstract int getLowZ(); // Support for the ability to extract the z coordinates of zones.
	
	public abstract int getHighZ(); // New fishing patch makes use of that to get the Z for the hook
	
	// landing coordinates.
	
	protected boolean lineSegmentsIntersect(double ax1, double ay1, double ax2, double ay2, double bx1, double by1, double bx2, double by2)
	{
		return Line2D.linesIntersect(ax1, ay1, ax2, ay2, bx1, by1, bx2, by2);
	}
	
	public abstract void visualizeZone(double z);
	
	protected final void dropDebugItem(int itemId, int num, double x, double y, double z)
	{
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		item.setCount(num);
		item.spawnMe(x, y, z + 5);
		ZoneManager.getInstance().getDebugItems().add(item);
	}
	
	public abstract Location getRandomPoint();
}
