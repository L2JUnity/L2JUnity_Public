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
package org.l2junity.commons.lang.mutable;

import java.io.Serializable;

/**
 * A mutable <code>Object</code> wrapper.
 * @param <T> the type to set and get
 * @since 2.1
 */
public class MutableObject<T> implements Mutable<T>, Serializable
{
	/**
	 * Required for serialization support.
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 86241875189L;
	
	/** The mutable value. */
	private T _value;
	
	/**
	 * Constructs a new MutableObject with the default value of <code>null</code>.
	 */
	public MutableObject()
	{
		super();
	}
	
	/**
	 * Constructs a new MutableObject with the specified value.
	 * @param value the initial value to store
	 */
	public MutableObject(final T value)
	{
		super();
		_value = value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets the value.
	 * @return the value, may be null
	 */
	@Override
	public T getValue()
	{
		return _value;
	}
	
	/**
	 * Sets the value.
	 * @param value the value to set
	 */
	@Override
	public void setValue(final T value)
	{
		_value = value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Compares this object against the specified object. The result is <code>true</code> if and only if the argument is not <code>null</code> and is a <code>MutableObject</code> object that contains the same <code>T</code> value as this object.
	 * </p>
	 * @param obj the object to compare with, <code>null</code> returns <code>false</code>
	 * @return <code>true</code> if the objects are the same; <code>true</code> if the objects have equivalent <code>value</code> fields; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (this == obj)
		{
			return true;
		}
		if (getClass() == obj.getClass())
		{
			final MutableObject<?> that = (MutableObject<?>) obj;
			return _value.equals(that._value);
		}
		return false;
	}
	
	/**
	 * Returns the value's hash code or <code>0</code> if the value is <code>null</code>.
	 * @return the value's hash code or <code>0</code> if the value is <code>null</code>.
	 */
	@Override
	public int hashCode()
	{
		return _value == null ? 0 : _value.hashCode();
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Returns the String value of this mutable.
	 * @return the mutable value as a string
	 */
	@Override
	public String toString()
	{
		return _value == null ? "null" : _value.toString();
	}
}
