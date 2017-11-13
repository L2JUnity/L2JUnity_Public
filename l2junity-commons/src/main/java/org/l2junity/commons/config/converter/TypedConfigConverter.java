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

import java.lang.reflect.Field;

/**
 * @author NB4L1
 * @param <T> associated field type
 */
public abstract class TypedConfigConverter<T> implements IConfigConverter
{
	@Override
	public final Object convertFromString(Field field, Class<?> type, String value)
	{
		if (getRequiredType() != type)
		{
			throw new ClassCastException(getRequiredType() + " type required, but found: " + type + "!");
		}
		
		value = (String) MainConfigConverter.getInstance().convertFromString(field, String.class, value);
		
		return convertFromString(value);
	}
	
	@Override
	public final String convertToString(Field field, Class<?> type, Object obj)
	{
		if (getRequiredType() != type)
		{
			throw new ClassCastException(getRequiredType() + " type required, but found: " + type + "!");
		}
		
		if (obj == null)
		{
			return "";
		}
		
		if (!getRequiredType().isInstance(obj))
		{
			throw new ClassCastException(getRequiredType() + " value required, but found: '" + obj + "'!");
		}
		
		final String value = convertToString(getRequiredType().cast(obj));
		
		return MainConfigConverter.getInstance().convertToString(field, String.class, value);
	}
	
	protected abstract T convertFromString(String value);
	
	protected abstract String convertToString(T obj);
	
	protected abstract Class<T> getRequiredType();
}
