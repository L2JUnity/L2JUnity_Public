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
package org.l2junity.gameserver.model.events.returns;

import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.interfaces.ILocational;

/**
 * @author Nik
 */
public class LocationReturn extends TerminateReturn
{
	private final boolean _overrideLocation;
	private double _x;
	private double _y;
	private double _z;
	private int _heading;
	private Instance _instance;
	
	public LocationReturn(boolean terminate, boolean overrideLocation)
	{
		super(terminate, false, false);
		_overrideLocation = overrideLocation;
	}
	
	public LocationReturn(boolean terminate, boolean overrideLocation, ILocational targetLocation, Instance instance)
	{
		super(terminate, false, false);
		_overrideLocation = overrideLocation;
		
		if (targetLocation != null)
		{
			setX(targetLocation.getX());
			setY(targetLocation.getY());
			setZ(targetLocation.getZ());
			setHeading(targetLocation.getHeading());
			setInstance(instance);
		}
	}
	
	public void setX(double x)
	{
		_x = x;
	}
	
	public void setY(double y)
	{
		_y = y;
	}
	
	public void setZ(double z)
	{
		_z = z;
	}
	
	public void setHeading(int heading)
	{
		_heading = heading;
	}
	
	public void setInstance(Instance instance)
	{
		_instance = instance;
	}
	
	public boolean overrideLocation()
	{
		return _overrideLocation;
	}
	
	public double getX()
	{
		return _x;
	}
	
	public double getY()
	{
		return _y;
	}
	
	public double getZ()
	{
		return _z;
	}
	
	public int getHeading()
	{
		return _heading;
	}
	
	public Instance getInstance()
	{
		return _instance;
	}
}
