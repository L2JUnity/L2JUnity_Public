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

/**
 * A mutable <code>long</code> wrapper.
 * <p>
 * Note that as MutableLong does not extend Long, it is not treated by String.format as a Long parameter.
 * @see Long
 * @since 2.1
 */
public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number>
{
	/**
	 * Required for serialization support.
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 62986528375L;
	
	/** The mutable value. */
	private long _value;
	
	/**
	 * Constructs a new MutableLong with the default value of zero.
	 */
	public MutableLong()
	{
		super();
	}
	
	/**
	 * Constructs a new MutableLong with the specified value.
	 * @param value the initial value to store
	 */
	public MutableLong(final long value)
	{
		super();
		_value = value;
	}
	
	/**
	 * Constructs a new MutableLong with the specified value.
	 * @param value the initial value to store, not null
	 * @throws NullPointerException if the object is null
	 */
	public MutableLong(final Number value)
	{
		super();
		_value = value.longValue();
	}
	
	/**
	 * Constructs a new MutableLong parsing the given string.
	 * @param value the string to parse, not null
	 * @throws NumberFormatException if the string cannot be parsed into a long
	 * @since 2.5
	 */
	public MutableLong(final String value) throws NumberFormatException
	{
		super();
		_value = Long.parseLong(value);
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets the value as a Long instance.
	 * @return the value as a Long, never null
	 */
	@Override
	public Long getValue()
	{
		return Long.valueOf(_value);
	}
	
	/**
	 * Sets the value.
	 * @param value the value to set
	 */
	public void setValue(final long value)
	{
		_value = value;
	}
	
	/**
	 * Sets the value from any Number instance.
	 * @param value the value to set, not null
	 * @throws NullPointerException if the object is null
	 */
	@Override
	public void setValue(final Number value)
	{
		_value = value.longValue();
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Increments the value.
	 * @since Commons Lang 2.2
	 */
	public void increment()
	{
		_value++;
	}
	
	/**
	 * Increments this instance's value by 1; this method returns the value associated with the instance immediately prior to the increment operation. This method is not thread safe.
	 * @return the value associated with the instance before it was incremented
	 * @since 3.5
	 */
	public long getAndIncrement()
	{
		final long last = _value;
		_value++;
		return last;
	}
	
	/**
	 * Increments this instance's value by 1; this method returns the value associated with the instance immediately after the increment operation. This method is not thread safe.
	 * @return the value associated with the instance after it is incremented
	 * @since 3.5
	 */
	public long incrementAndGet()
	{
		_value++;
		return _value;
	}
	
	/**
	 * Decrements the value.
	 * @since Commons Lang 2.2
	 */
	public void decrement()
	{
		_value--;
	}
	
	/**
	 * Decrements this instance's value by 1; this method returns the value associated with the instance immediately prior to the decrement operation. This method is not thread safe.
	 * @return the value associated with the instance before it was decremented
	 * @since 3.5
	 */
	public long getAndDecrement()
	{
		final long last = _value;
		_value--;
		return last;
	}
	
	/**
	 * Decrements this instance's value by 1; this method returns the value associated with the instance immediately after the decrement operation. This method is not thread safe.
	 * @return the value associated with the instance after it is decremented
	 * @since 3.5
	 */
	public long decrementAndGet()
	{
		_value--;
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Adds a value to the value of this instance.
	 * @param operand the value to add, not null
	 * @since Commons Lang 2.2
	 */
	public void add(final long operand)
	{
		_value += operand;
	}
	
	/**
	 * Adds a value to the value of this instance.
	 * @param operand the value to add, not null
	 * @throws NullPointerException if the object is null
	 * @since Commons Lang 2.2
	 */
	public void add(final Number operand)
	{
		_value += operand.longValue();
	}
	
	/**
	 * Subtracts a value from the value of this instance.
	 * @param operand the value to subtract, not null
	 * @since Commons Lang 2.2
	 */
	public void subtract(final long operand)
	{
		_value -= operand;
	}
	
	/**
	 * Subtracts a value from the value of this instance.
	 * @param operand the value to subtract, not null
	 * @throws NullPointerException if the object is null
	 * @since Commons Lang 2.2
	 */
	public void subtract(final Number operand)
	{
		_value -= operand.longValue();
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately after the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance after adding the operand
	 * @since 3.5
	 */
	public long addAndGet(final long operand)
	{
		_value += operand;
		return _value;
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately after the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @throws NullPointerException if {@code operand} is null
	 * @return the value associated with this instance after adding the operand
	 * @since 3.5
	 */
	public long addAndGet(final Number operand)
	{
		_value += operand.longValue();
		return _value;
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately prior to the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance immediately before the operand was added
	 * @since 3.5
	 */
	public long getAndAdd(final long operand)
	{
		final long last = _value;
		_value += operand;
		return last;
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately prior to the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @throws NullPointerException if {@code operand} is null
	 * @return the value associated with this instance immediately before the operand was added
	 * @since 3.5
	 */
	public long getAndAdd(final Number operand)
	{
		final long last = _value;
		_value += operand.longValue();
		return last;
	}
	
	// -----------------------------------------------------------------------
	// shortValue and byteValue rely on Number implementation
	/**
	 * Returns the value of this MutableLong as an int.
	 * @return the numeric value represented by this object after conversion to type int.
	 */
	@Override
	public int intValue()
	{
		return (int) _value;
	}
	
	/**
	 * Returns the value of this MutableLong as a long.
	 * @return the numeric value represented by this object after conversion to type long.
	 */
	@Override
	public long longValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableLong as a float.
	 * @return the numeric value represented by this object after conversion to type float.
	 */
	@Override
	public float floatValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableLong as a double.
	 * @return the numeric value represented by this object after conversion to type double.
	 */
	@Override
	public double doubleValue()
	{
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets this mutable as an instance of Long.
	 * @return a Long instance containing the value from this mutable, never null
	 */
	public Long toLong()
	{
		return Long.valueOf(longValue());
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this object to the specified object. The result is <code>true</code> if and only if the argument is not <code>null</code> and is a <code>MutableLong</code> object that contains the same <code>long</code> value as this object.
	 * @param obj the object to compare with, null returns false
	 * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof MutableLong)
		{
			return _value == ((MutableLong) obj).longValue();
		}
		return false;
	}
	
	/**
	 * Returns a suitable hash code for this mutable.
	 * @return a suitable hash code
	 */
	@Override
	public int hashCode()
	{
		return (int) (_value ^ (_value >>> 32));
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this mutable to another in ascending order.
	 * @param other the other mutable to compare to, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	@Override
	public int compareTo(final MutableLong other)
	{
		return MutableComparator.compare(_value, other._value);
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Returns the String value of this mutable.
	 * @return the mutable value as a string
	 */
	@Override
	public String toString()
	{
		return String.valueOf(_value);
	}
}
