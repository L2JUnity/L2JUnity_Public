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

import java.awt.Polygon;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.geodata.GeoData;

/**
 * @author UnAfraid
 */
public class Territory extends Polygon
{
	private static final long serialVersionUID = 7132757127831184920L;
	private int _minZ;
	private int _maxZ;
	
	public Territory()
	{
		_minZ = 999999;
		_maxZ = -999999;
	}
	
	public void addPoint(int x, int y, int minZ, int maxZ)
	{
		super.addPoint(x, y);
		
		if (minZ < _minZ)
		{
			_minZ = minZ;
		}
		if (maxZ > _maxZ)
		{
			_maxZ = maxZ;
		}
	}
	
	public boolean isInside(double x, double y, double z)
	{
		return super.contains(x, y) && (z >= _minZ) && (z <= _maxZ);
	}
	
	public Location getRandomPoint()
	{
		int x, y;
		
		int minX = getBounds().x;
		int maxX = getBounds().x + getBounds().width;
		int minY = getBounds().y;
		int maxY = getBounds().y + getBounds().height;
		
		x = Rnd.get(minX, maxX);
		y = Rnd.get(minY, maxY);
		
		int antiBlocker = 0;
		while (!contains(x, y) && (antiBlocker++ < 1000))
		{
			x = Rnd.get(minX, maxX);
			y = Rnd.get(minY, maxY);
		}
		
		return new Location(x, y, GeoData.getInstance().getHeight(x, y, _minZ));
	}
}
