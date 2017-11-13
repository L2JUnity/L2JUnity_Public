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
package org.l2junity.gameserver.model.variables;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.holders.TrainingHolder;
import org.l2junity.gameserver.model.interfaces.IDeletable;
import org.l2junity.gameserver.model.interfaces.IRestorable;
import org.l2junity.gameserver.model.interfaces.IStorable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public abstract class AbstractVariables extends StatsSet implements IRestorable, IStorable, IDeletable
{
	static final Logger LOGGER = LoggerFactory.getLogger(AbstractVariables.class);
	private final AtomicBoolean _hasChanges = new AtomicBoolean(false);
	
	public AbstractVariables()
	{
		super(new ConcurrentHashMap<>());
	}
	
	/**
	 * Overriding following methods to prevent from doing useless database operations if there is no changes since player's login.
	 */
	
	@Override
	public final StatsSet set(String name, boolean value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, double value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, Enum<?> value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, int value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, long value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, String value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	@Override
	public final StatsSet set(String name, Object value)
	{
		_hasChanges.compareAndSet(false, true);
		return super.set(name, value);
	}
	
	/**
	 * Put's entry to the variables and marks as changed if required (<i>Useful when restoring to do not save them again</i>).
	 * @param name
	 * @param value
	 * @param markAsChanged
	 * @return
	 */
	public final StatsSet set(String name, String value, boolean markAsChanged)
	{
		if (markAsChanged)
		{
			_hasChanges.compareAndSet(false, true);
		}
		return super.set(name, value);
	}
	
	/**
	 * Return true if there exists a record for the variable name.
	 * @param name
	 * @return
	 */
	public boolean hasVariable(String name)
	{
		return getSet().keySet().contains(name);
	}
	
	/**
	 * @return {@code true} if changes are made since last load/save.
	 */
	public final boolean hasChanges()
	{
		return _hasChanges.get();
	}
	
	/**
	 * Atomically sets the value to the given updated value if the current value {@code ==} the expected value.
	 * @param expect
	 * @param update
	 * @return {@code true} if successful. {@code false} return indicates that the actual value was not equal to the expected value.
	 */
	public final boolean compareAndSetChanges(boolean expect, boolean update)
	{
		return _hasChanges.compareAndSet(expect, update);
	}
	
	/**
	 * Removes variable
	 * @param name
	 */
	@Override
	public final void remove(String name)
	{
		_hasChanges.compareAndSet(false, true);
		getSet().remove(name);
	}
	
	protected static class ValidObjectInputStream extends ObjectInputStream
	{
		private static final List<Class<?>> VALID_CLASSES = new ArrayList<>();
		static
		{
			// Default classes
			VALID_CLASSES.add(String.class);
			VALID_CLASSES.add(Boolean.class);
			
			// Unity's default classes
			VALID_CLASSES.add(TrainingHolder.class);
			VALID_CLASSES.add(ItemHolder.class);
			VALID_CLASSES.add(SkillHolder.class);
			VALID_CLASSES.add(Location.class);
			
			// Custom classes
			//@formatter:off
			Arrays.stream(GeneralConfig.ACCOUNT_VARIABLES_CLASS_WHITELIST)
				.map(className ->
				{
					try
					{
						return Class.forName(className);
					}
					catch (Exception e)
					{
						LOGGER.warn("Missing class: {}", className);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.forEach(VALID_CLASSES::add);
			//@formatter:on
		}
		
		public ValidObjectInputStream(InputStream inputStream) throws IOException
		{
			super(inputStream);
		}
		
		@Override
		protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException
		{
			final Class<?> clazz = super.resolveClass(osc);
			if (!clazz.isPrimitive() && !clazz.isArray() && !Number.class.isAssignableFrom(clazz) && !Collection.class.isAssignableFrom(clazz) && !Map.class.isAssignableFrom(clazz) && !Enum.class.isAssignableFrom(clazz) && !VALID_CLASSES.contains(clazz))
			{
				throw new InvalidClassException("Unauthorized deserialization attempt", osc.getName());
			}
			return clazz;
		}
	}
}
