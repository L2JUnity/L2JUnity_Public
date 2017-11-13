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

/**
 * @author _dev_
 */
public class FileConfigConverter implements IConfigConverter
{
	@Override
	public Object convertFromString(Field field, Class<?> type, String value)
	{
		return new File(value);
	}
	
	@Override
	public String convertToString(Field field, Class<?> type, Object obj)
	{
		return obj.toString();
	}
	
	/**
	 * Returns a singleton instance of this type.
	 * @return an instance of this class
	 */
	public static final FileConfigConverter getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		static final FileConfigConverter INSTANCE = new FileConfigConverter();
	}
}
