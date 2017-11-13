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

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.l2junity.commons.lang.FieldParser;

public class MainConfigConverter implements IConfigConverter
{
	@Override
	public Object convertFromString(Field field, Class<?> type, String value)
	{
		if (type.isArray())
		{
			return getArrayConverter().convertFromString(field, type, value);
		}
		
		if (type == List.class)
		{
			return getListConverter().convertFromString(field, type, value);
		}
		
		if (type == Set.class)
		{
			return getSetConverter().convertFromString(field, type, value);
		}
		
		if (type == Path.class)
		{
			return getPathConverter().convertFromString(field, type, value);
		}
		
		if (type == File.class)
		{
			return getFileConverter().convertFromString(field, type, value);
		}
		
		if (type == Pattern.class)
		{
			return getPatternConverter().convertFromString(field, type, value);
		}
		
		if (type == Duration.class)
		{
			return getDurationConverter().convertFromString(field, type, value);
		}
		
		return FieldParser.get(type, value);
	}
	
	@Override
	public String convertToString(Field field, Class<?> type, Object obj)
	{
		if (type.isArray())
		{
			return getArrayConverter().convertToString(field, type, obj);
		}
		
		if (type == List.class)
		{
			return getListConverter().convertToString(field, type, obj);
		}
		
		if (type == Set.class)
		{
			return getSetConverter().convertToString(field, type, obj);
		}
		
		if (type == Path.class)
		{
			return getPathConverter().convertToString(field, type, obj);
		}
		
		if (type == File.class)
		{
			return getFileConverter().convertToString(field, type, obj);
		}
		
		if (type == Pattern.class)
		{
			return getPatternConverter().convertToString(field, type, obj);
		}
		
		if (type == Duration.class)
		{
			return getDurationConverter().convertToString(field, type, obj);
		}
		
		if (obj == null)
		{
			return "";
		}
		
		return obj.toString();
	}
	
	protected IConfigConverter getArrayConverter()
	{
		return ArrayConfigConverter.getInstance();
	}
	
	protected IConfigConverter getListConverter()
	{
		return ListConfigConverter.getInstance();
	}
	
	protected IConfigConverter getSetConverter()
	{
		return SetConfigConverter.getInstance();
	}
	
	protected IConfigConverter getPathConverter()
	{
		return PathConfigConverter.getInstance();
	}
	
	protected IConfigConverter getFileConverter()
	{
		return FileConfigConverter.getInstance();
	}
	
	protected IConfigConverter getPatternConverter()
	{
		return PatternConfigConverter.getInstance();
	}
	
	protected IConfigConverter getDurationConverter()
	{
		return DurationConfigConverter.getInstance();
	}
	
	private static final class SingletonHolder
	{
		static final MainConfigConverter INSTANCE = new MainConfigConverter();
	}
	
	public static MainConfigConverter getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
