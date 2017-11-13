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
 * A mutable <code>byte</code> wrapper.
 * <p>
 * Note that as MutableByte does not extend Byte, it is not treated by String.format as a Byte parameter.
 * @see Byte
 * @since 2.1
 */
public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number>
{
	/**
	 * Required for serialization support.
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = -1585823265L;
	
	/** The mutable value. */
	private byte _value;
	
	/**
	 * Constructs a new MutableByte with the default value of zero.
	 */
	public MutableByte()
	{
		super();
	}
	
	/**
	 * Constructs a new MutableByte with the specified value.
	 * @param value the initial value to store
	 */
	public MutableByte(final byte value)
	{
		super();
		_value = value;
	}
	
	/**
	 * Constructs a new MutableByte with the specified value.
	 * @param value the initial value to store, not null
	 * @throws NullPointerException if the object is null
	 */
	public MutableByte(final Number value)
	{
		super();
		_value = value.byteValue();
	}
	
	/**
	 * Constructs a new MutableByte parsing the given string.
	 * @param value the string to parse, not null
	 * @throws NumberFormatException if the string cannot be parsed into a byte
	 * @since 2.5
	 */
	public MutableByte(final String value) throws NumberFormatException
	{
		super();
		_value = Byte.parseByte(value);
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets the value as a Byte instance.
	 * @return the value as a Byte, never null
	 */
	@Override
	public Byte getValue()
	{
		return Byte.valueOf(_value);
	}
	
	/**
	 * Sets the value.
	 * @param value the value to set
	 */
	public void setValue(final byte value)
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
		_value = value.byteValue();
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
	public byte getAndIncrement()
	{
		final byte last = _value;
		_value++;
		return last;
	}
	
	/**
	 * Increments this instance's value by 1; this method returns the value associated with the instance immediately after the increment operation. This method is not thread safe.
	 * @return the value associated with the instance after it is incremented
	 * @since 3.5
	 */
	public byte incrementAndGet()
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
	public byte getAndDecrement()
	{
		final byte last = _value;
		_value--;
		return last;
	}
	
	/**
	 * Decrements this instance's value by 1; this method returns the value associated with the instance immediately after the decrement operation. This method is not thread safe.
	 * @return the value associated with the instance after it is decremented
	 * @since 3.5
	 */
	public byte decrementAndGet()
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
	public void add(final byte operand)
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
		_value += operand.byteValue();
	}
	
	/**
	 * Subtracts a value from the value of this instance.
	 * @param operand the value to subtract, not null
	 * @since Commons Lang 2.2
	 */
	public void subtract(final byte operand)
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
		_value -= operand.byteValue();
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately after the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance after adding the operand
	 * @since 3.5
	 */
	public byte addAndGet(final byte operand)
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
	public byte addAndGet(final Number operand)
	{
		_value += operand.byteValue();
		return _value;
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately prior to the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance immediately before the operand was added
	 * @since 3.5
	 */
	public byte getAndAdd(final byte operand)
	{
		final byte last = _value;
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
	public byte getAndAdd(final Number operand)
	{
		final byte last = _value;
		_value += operand.byteValue();
		return last;
	}
	
	// -----------------------------------------------------------------------
	// shortValue relies on Number implementation
	/**
	 * Returns the value of this MutableByte as a byte.
	 * @return the numeric value represented by this object after conversion to type byte.
	 */
	@Override
	public byte byteValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableByte as an int.
	 * @return the numeric value represented by this object after conversion to type int.
	 */
	@Override
	public int intValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableByte as a long.
	 * @return the numeric value represented by this object after conversion to type long.
	 */
	@Override
	public long longValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableByte as a float.
	 * @return the numeric value represented by this object after conversion to type float.
	 */
	@Override
	public float floatValue()
	{
		return _value;
	}
	
	/**
	 * Returns the value of this MutableByte as a double.
	 * @return the numeric value represented by this object after conversion to type double.
	 */
	@Override
	public double doubleValue()
	{
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets this mutable as an instance of Byte.
	 * @return a Byte instance containing the value from this mutable
	 */
	public Byte toByte()
	{
		return Byte.valueOf(byteValue());
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this object to the specified object. The result is <code>true</code> if and only if the argument is not <code>null</code> and is a <code>MutableByte</code> object that contains the same <code>byte</code> value as this object.
	 * @param obj the object to compare with, null returns false
	 * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof MutableByte)
		{
			return _value == ((MutableByte) obj).byteValue();
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
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this mutable to another in ascending order.
	 * @param other the other mutable to compare to, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	@Override
	public int compareTo(final MutableByte other)
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
