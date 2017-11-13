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
package org.l2junity.commons.lang;

import java.lang.reflect.Array;

/**
 * Its original name used to be L2Parser, but since L2JUnity concept doesn't like the "L2" tag, I've renamed it.<br>
 * This class's purpose is eventually to parse fields.
 * @author NB4L1
 */
public final class FieldParser
{
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	public static Object get(Class<?> type, String value)
	{
		if ((type == Boolean.class) || (type == Boolean.TYPE))
		{
			return FieldParser.getBoolean(value);
		}
		else if ((type == Long.class) || (type == Long.TYPE))
		{
			return FieldParser.getLong(value);
		}
		else if ((type == Integer.class) || (type == Integer.TYPE))
		{
			return FieldParser.getInteger(value);
		}
		else if ((type == Short.class) || (type == Short.TYPE))
		{
			return FieldParser.getShort(value);
		}
		else if ((type == Byte.class) || (type == Byte.TYPE))
		{
			return FieldParser.getByte(value);
		}
		else if ((type == Double.class) || (type == Double.TYPE))
		{
			return FieldParser.getDouble(value);
		}
		else if ((type == Float.class) || (type == Float.TYPE))
		{
			return FieldParser.getFloat(value);
		}
		else if (type == String.class)
		{
			return FieldParser.getString(value);
		}
		else if (type.isEnum())
		{
			return FieldParser.getEnum((Class<? extends Enum>) type, value);
		}
		else
		{
			throw new FieldParserException("Not covered type: " + type + "!");
		}
	}
	
	public static boolean getBoolean(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Boolean.class);
		}
		
		try
		{
			return Boolean.parseBoolean(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Boolean.class, value, e);
		}
	}
	
	public static byte getByte(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Byte.class);
		}
		
		try
		{
			return Byte.decode(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Byte.class, value, e);
		}
	}
	
	public static short getShort(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Short.class);
		}
		
		try
		{
			return Short.decode(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Short.class, value, e);
		}
	}
	
	public static int getInteger(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Integer.class);
		}
		
		try
		{
			return Integer.decode(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Integer.class, value, e);
		}
	}
	
	public static long getLong(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Long.class);
		}
		
		try
		{
			return Long.decode(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Long.class, value, e);
		}
	}
	
	public static float getFloat(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Float.class);
		}
		
		try
		{
			return Float.parseFloat(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Float.class, value, e);
		}
	}
	
	public static double getDouble(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(Double.class);
		}
		
		try
		{
			return Double.parseDouble(value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(Double.class, value, e);
		}
	}
	
	public static String getString(String value)
	{
		if (value == null)
		{
			throw new FieldParserException(String.class);
		}
		
		return String.valueOf(value);
	}
	
	public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String value)
	{
		if (value == null)
		{
			throw new FieldParserException(enumClass);
		}
		
		try
		{
			return Enum.valueOf(enumClass, value);
		}
		catch (final RuntimeException e)
		{
			throw new FieldParserException(enumClass, value, e);
		}
	}
	
	public static Object getArray(Class<?> componentClass, String value, String regex)
	{
		final String[] values = value.split(regex);
		
		final Object array = Array.newInstance(componentClass, values.length);
		
		for (int i = 0; i < values.length; i++)
		{
			Array.set(array, i, get(componentClass, values[i]));
		}
		
		return array;
	}
	
	public static final class FieldParserException extends RuntimeException
	{
		private static final long serialVersionUID = 1839324679891385619L;
		
		public FieldParserException()
		{
			super();
		}
		
		public FieldParserException(String message)
		{
			super(message);
		}
		
		public FieldParserException(Class<?> requiredType)
		{
			super(requiredType + " value required, but not specified!");
		}
		
		public FieldParserException(Class<?> requiredType, String value, RuntimeException cause)
		{
			super(requiredType + " value required, but found: '" + value + "'!", cause);
		}
	}
}