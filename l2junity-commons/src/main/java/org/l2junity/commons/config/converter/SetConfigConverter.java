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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates a {@link Set} (a collection that implies stellar {@link Collection#contains(Object)} performance and disallows multiple additions of the same object).
 * @author _dev_
 */
public class SetConfigConverter extends CollectionConfigConverter
{
	@Override
	protected Set<Object> createCollection(int size)
	{
		return new HashSet<>(size);
	}
	
	@Override
	protected <E> Set<E> emptyCollection()
	{
		return Collections.emptySet();
	}
	
	/**
	 * Returns a singleton instance of this type.
	 * @return an instance of this class
	 */
	public static final SetConfigConverter getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		static final SetConfigConverter INSTANCE = new SetConfigConverter();
	}
}
