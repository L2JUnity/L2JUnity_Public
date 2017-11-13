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
package org.l2junity.commons.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lord_rex
 */
public final class GenericUtil
{
	private GenericUtil()
	{
		// utility class
	}
	
	public static Type[] getGenericTypes(final Field field)
	{
		final Type genType = field.getGenericType();
		if (!ParameterizedType.class.isInstance(genType))
		{
			return null;
		}
		
		final ParameterizedType pType = (ParameterizedType) genType;
		return pType.getActualTypeArguments();
	}
	
	public static Class<?> getFirstGenericTypeOfGenerizedField(final Field field)
	{
		final Type[] allGenTypes = getGenericTypes(field);
		if (allGenTypes == null)
		{
			return Object.class; // missing wildcard declaration
		}
		
		return (Class<?>) allGenTypes[0];
	}
}
