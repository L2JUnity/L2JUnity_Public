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
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.l2junity.commons.util.GenericUtil;

/**
 * @author _dev_
 */
@SuppressWarnings(
{
	"unchecked",
	"rawtypes"
})
public abstract class CollectionConfigConverter implements IConfigConverter
{
	@Override
	public Object convertFromString(Field field, Class<?> type, String value)
	{
		if (value.isEmpty())
		{
			return emptyCollection();
		}
		
		Collection<Object> result = null;
		final String[] splitted = value.split(",");
		
		final Class<?> elementType = GenericUtil.getFirstGenericTypeOfGenerizedField(field);
		if (type == Set.class)
		{
			// for enums, impose enum order
			if (elementType.isEnum())
			{
				final Class<? extends Enum> c = elementType.asSubclass(Enum.class);
				if ("*".equals(value))
				{
					return EnumSet.allOf(c);
				}
				
				final EnumSet set = EnumSet.noneOf(c);
				result = set;
			}
			// otherwise, impose natural order (if applicable)
			else if (Comparable.class.isAssignableFrom(elementType))
			{
				result = new TreeSet<>();
			}
		}
		
		if (result == null)
		{
			result = createCollection(splitted.length);
		}
		
		for (final String e : splitted)
		{
			result.add(getElementConverter().convertFromString(null, elementType, e));
		}
		return result;
	}
	
	@Override
	public String convertToString(Field field, Class<?> type, Object obj)
	{
		if (obj == null)
		{
			return "";
		}
		
		final Collection<?> col = (Collection<?>) obj;
		if (col.isEmpty())
		{
			return "";
		}
		
		final Class<?> elementType = GenericUtil.getFirstGenericTypeOfGenerizedField(field);
		if (elementType.isEnum() && (type == Set.class))
		{
			final Class<? extends Enum> c = elementType.asSubclass(Enum.class);
			if (col.equals(EnumSet.allOf(c)))
			{
				return "*";
			}
		}
		
		final Iterator<?> it = col.iterator();
		final StringBuilder sb = new StringBuilder().append(getElementConverter().convertToString(null, elementType, it.next()));
		while (it.hasNext())
		{
			sb.append(',').append(getElementConverter().convertToString(null, elementType, it.next()));
		}
		return sb.toString();
	}
	
	protected abstract Collection<Object> createCollection(int size);
	
	protected abstract <E> Collection<E> emptyCollection();
	
	protected IConfigConverter getElementConverter()
	{
		return MainConfigConverter.getInstance();
	}
}
