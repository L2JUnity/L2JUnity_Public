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
package org.l2junity.commons.config.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ArrayConfigConverter implements IConfigConverter
{
	@Override
	public Object convertFromString(Field field, Class<?> type, String value)
	{
		final Class<?> componentType = type.getComponentType();
		
		if (value.isEmpty())
		{
			return Array.newInstance(componentType, 0);
		}
		
		final String[] splitted = Pattern.compile(getElementDelimiter(), Pattern.LITERAL).split(value);
		final Object array = Array.newInstance(componentType, splitted.length);
		
		for (int i = 0; i < splitted.length; i++)
		{
			Array.set(array, i, getElementConverter().convertFromString(field, componentType, splitted[i]));
		}
		
		if (Comparable.class.isAssignableFrom(componentType))
		{
			Arrays.sort((Comparable[]) array);
		}
		
		return array;
	}
	
	@Override
	public String convertToString(Field field, Class<?> type, Object obj)
	{
		final Class<?> componentType = type.getComponentType();
		
		if (obj == null)
		{
			return "";
		}
		
		final int length = Array.getLength(obj);
		if (length < 1)
		{
			return "";
		}
		
		if (Comparable.class.isAssignableFrom(componentType))
		{
			Arrays.sort((Comparable[]) obj);
		}
		
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < length; i++)
		{
			if (i > 0)
			{
				sb.append(getElementDelimiter());
			}
			
			sb.append(getElementConverter().convertToString(field, componentType, Array.get(obj, i)));
		}
		
		return sb.toString();
	}
	
	protected String getElementDelimiter()
	{
		return ",";
	}
	
	protected IConfigConverter getElementConverter()
	{
		return MainConfigConverter.getInstance();
	}
	
	private static final class SingletonHolder
	{
		static final ArrayConfigConverter INSTANCE = new ArrayConfigConverter();
	}
	
	public static ArrayConfigConverter getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
