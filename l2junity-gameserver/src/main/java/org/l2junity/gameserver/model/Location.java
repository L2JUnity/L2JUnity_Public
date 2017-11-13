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

import java.io.Serializable;

import org.l2junity.gameserver.model.interfaces.ILocational;
import org.l2junity.gameserver.model.interfaces.IPositionable;

/**
 * Location data transfer object.<br>
 * Contains coordinates data, heading and instance Id.
 * @author Zoey76
 */
public class Location implements IPositionable, Serializable
{
	private static final long serialVersionUID = 9053071440080626121L;
	
	private double _x;
	private double _y;
	private double _z;
	private int _heading;
	
	public Location(double x, double y, double z)
	{
		this(x, y, z, 0);
	}
	
	public Location(double x, double y, double z, int heading)
	{
		_x = x;
		_y = y;
		_z = z;
		_heading = heading;
	}
	
	public Location(ILocational loc)
	{
		this(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
	}
	
	public Location(StatsSet set)
	{
		_x = set.getInt("x");
		_y = set.getInt("y");
		_z = set.getInt("z");
		_heading = set.getInt("heading", 0);
	}
	
	/**
	 * Get the x coordinate.
	 * @return the x coordinate
	 */
	@Override
	public double getX()
	{
		return _x;
	}
	
	/**
	 * Get the y coordinate.
	 * @return the y coordinate
	 */
	@Override
	public double getY()
	{
		return _y;
	}
	
	/**
	 * Get the z coordinate.
	 * @return the z coordinate
	 */
	@Override
	public double getZ()
	{
		return _z;
	}
	
	/**
	 * Set the x, y, z coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	@Override
	public void setXYZ(double x, double y, double z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	/**
	 * Set the x, y, z coordinates.
	 * @param loc The location.
	 */
	@Override
	public void setXYZ(ILocational loc)
	{
		setXYZ(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Get the heading.
	 * @return the heading
	 */
	@Override
	public int getHeading()
	{
		return _heading;
	}
	
	/**
	 * Set the heading.
	 * @param heading the heading
	 */
	@Override
	public void setHeading(int heading)
	{
		_heading = heading;
	}
	
	@Override
	public void setLocation(Location loc)
	{
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
		_heading = loc.getHeading();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ((obj != null) && (obj instanceof ILocational))
		{
			final ILocational loc = (ILocational) obj;
			return (((int) getX()) == ((int) loc.getX())) && (((int) getY()) == ((int) loc.getY())) && (((int) getZ()) == ((int) loc.getZ())) && (getHeading() == loc.getHeading());
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName() + "] X: " + getX() + " Y: " + getY() + " Z: " + getZ() + " Heading: " + _heading;
	}
}
