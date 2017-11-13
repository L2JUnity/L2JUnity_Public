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
package org.l2junity.commons.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author NosKun
 */
public class LoadHolder
{
	private final Method _instanceGetterMethod;
	private final Method _loadMethod;
	
	public LoadHolder(Method instanceGetterMethod, Method loadMethod)
	{
		_instanceGetterMethod = Objects.requireNonNull(instanceGetterMethod);
		_loadMethod = Objects.requireNonNull(loadMethod);
	}
	
	public Method getInstanceGetterMethod()
	{
		return _instanceGetterMethod;
	}
	
	public Method getLoadMethod()
	{
		return _loadMethod;
	}
	
	public void call() throws InvocationTargetException, IllegalAccessException
	{
		final boolean loadMethodAccessible = _loadMethod.isAccessible();
		_loadMethod.setAccessible(true);
		_loadMethod.invoke(_instanceGetterMethod.invoke(null));
		_loadMethod.setAccessible(loadMethodAccessible);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}
		
		final LoadHolder that = (LoadHolder) o;
		if (!_instanceGetterMethod.equals(that._instanceGetterMethod))
		{
			return false;
		}
		return _loadMethod.equals(that._loadMethod);
		
	}
	
	@Override
	public int hashCode()
	{
		int result = _instanceGetterMethod.hashCode();
		result = (31 * result) + _loadMethod.hashCode();
		return result;
	}
	
	@Override
	public String toString()
	{
		return _instanceGetterMethod.getDeclaringClass().getName() + "." + _instanceGetterMethod.getName() + "()." + _loadMethod.getName() + "()";
	}
}
