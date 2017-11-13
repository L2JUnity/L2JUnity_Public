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
 * A mutable <code>double</code> wrapper.
 * <p>
 * Note that as MutableDouble does not extend Double, it is not treated by String.format as a Double parameter.
 * @see Double
 * @since 2.1
 */
public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number>
{
	/**
	 * Required for serialization support.
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1587163916L;
	
	/** The mutable value. */
	private double _value;
	
	/**
	 * Constructs a new MutableDouble with the default value of zero.
	 */
	public MutableDouble()
	{
		super();
	}
	
	/**
	 * Constructs a new MutableDouble with the specified value.
	 * @param value the initial value to store
	 */
	public MutableDouble(final double value)
	{
		super();
		_value = value;
	}
	
	/**
	 * Constructs a new MutableDouble with the specified value.
	 * @param value the initial value to store, not null
	 * @throws NullPointerException if the object is null
	 */
	public MutableDouble(final Number value)
	{
		super();
		_value = value.doubleValue();
	}
	
	/**
	 * Constructs a new MutableDouble parsing the given string.
	 * @param value the string to parse, not null
	 * @throws NumberFormatException if the string cannot be parsed into a double
	 * @since 2.5
	 */
	public MutableDouble(final String value) throws NumberFormatException
	{
		super();
		_value = Double.parseDouble(value);
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets the value as a Double instance.
	 * @return the value as a Double, never null
	 */
	@Override
	public Double getValue()
	{
		return Double.valueOf(_value);
	}
	
	/**
	 * Sets the value.
	 * @param value the value to set
	 */
	public void setValue(final double value)
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
		_value = value.doubleValue();
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Checks whether the double value is the special NaN value.
	 * @return true if NaN
	 */
	public boolean isNaN()
	{
		return Double.isNaN(_value);
	}
	
	/**
	 * Checks whether the double value is infinite.
	 * @return true if infinite
	 */
	public boolean isInfinite()
	{
		return Double.isInfinite(_value);
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
	public double getAndIncrement()
	{
		final double last = _value;
		_value++;
		return last;
	}
	
	/**
	 * Increments this instance's value by 1; this method returns the value associated with the instance immediately after the increment operation. This method is not thread safe.
	 * @return the value associated with the instance after it is incremented
	 * @since 3.5
	 */
	public double incrementAndGet()
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
	public double getAndDecrement()
	{
		final double last = _value;
		_value--;
		return last;
	}
	
	/**
	 * Decrements this instance's value by 1; this method returns the value associated with the instance immediately after the decrement operation. This method is not thread safe.
	 * @return the value associated with the instance after it is decremented
	 * @since 3.5
	 */
	public double decrementAndGet()
	{
		_value--;
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Adds a value to the value of this instance.
	 * @param operand the value to add
	 * @since Commons Lang 2.2
	 */
	public void add(final double operand)
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
		_value += operand.doubleValue();
	}
	
	/**
	 * Subtracts a value from the value of this instance.
	 * @param operand the value to subtract, not null
	 * @since Commons Lang 2.2
	 */
	public void subtract(final double operand)
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
		_value -= operand.doubleValue();
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately after the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance after adding the operand
	 * @since 3.5
	 */
	public double addAndGet(final double operand)
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
	public double addAndGet(final Number operand)
	{
		_value += operand.doubleValue();
		return _value;
	}
	
	/**
	 * Increments this instance's value by {@code operand}; this method returns the value associated with the instance immediately prior to the addition operation. This method is not thread safe.
	 * @param operand the quantity to add, not null
	 * @return the value associated with this instance immediately before the operand was added
	 * @since 3.5
	 */
	public double getAndAdd(final double operand)
	{
		final double last = _value;
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
	public double getAndAdd(final Number operand)
	{
		final double last = _value;
		_value += operand.doubleValue();
		return last;
	}
	
	// -----------------------------------------------------------------------
	// shortValue and byteValue rely on Number implementation
	/**
	 * Returns the value of this MutableDouble as an int.
	 * @return the numeric value represented by this object after conversion to type int.
	 */
	@Override
	public int intValue()
	{
		return (int) _value;
	}
	
	/**
	 * Returns the value of this MutableDouble as a long.
	 * @return the numeric value represented by this object after conversion to type long.
	 */
	@Override
	public long longValue()
	{
		return (long) _value;
	}
	
	/**
	 * Returns the value of this MutableDouble as a float.
	 * @return the numeric value represented by this object after conversion to type float.
	 */
	@Override
	public float floatValue()
	{
		return (float) _value;
	}
	
	/**
	 * Returns the value of this MutableDouble as a double.
	 * @return the numeric value represented by this object after conversion to type double.
	 */
	@Override
	public double doubleValue()
	{
		return _value;
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Gets this mutable as an instance of Double.
	 * @return a Double instance containing the value from this mutable, never null
	 */
	public Double toDouble()
	{
		return Double.valueOf(doubleValue());
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this object against the specified object. The result is <code>true</code> if and only if the argument is not <code>null</code> and is a <code>Double</code> object that represents a double that has the identical bit pattern to the bit pattern of the double represented by this object.
	 * For this purpose, two <code>double</code> values are considered to be the same if and only if the method {@link Double#doubleToLongBits(double)}returns the same long value when applied to each.
	 * <p>
	 * Note that in most cases, for two instances of class <code>Double</code>,<code>d1</code> and <code>d2</code>, the value of <code>d1.equals(d2)</code> is <code>true</code> if and only if <blockquote>
	 * 
	 * <pre>
	 * d1.doubleValue() == d2.doubleValue()
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * also has the value <code>true</code>. However, there are two exceptions:
	 * <ul>
	 * <li>If <code>d1</code> and <code>d2</code> both represent <code>Double.NaN</code>, then the <code>equals</code> method returns <code>true</code>, even though <code>Double.NaN==Double.NaN</code> has the value <code>false</code>.
	 * <li>If <code>d1</code> represents <code>+0.0</code> while <code>d2</code> represents <code>-0.0</code>, or vice versa, the <code>equal</code> test has the value <code>false</code>, even though <code>+0.0==-0.0</code> has the value <code>true</code>. This allows hashtables to operate properly.
	 * </ul>
	 * @param obj the object to compare with, null returns false
	 * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		return (obj instanceof MutableDouble) && (Double.doubleToLongBits(((MutableDouble) obj)._value) == Double.doubleToLongBits(_value));
	}
	
	/**
	 * Returns a suitable hash code for this mutable.
	 * @return a suitable hash code
	 */
	@Override
	public int hashCode()
	{
		final long bits = Double.doubleToLongBits(_value);
		return (int) (bits ^ (bits >>> 32));
	}
	
	// -----------------------------------------------------------------------
	/**
	 * Compares this mutable to another in ascending order.
	 * @param other the other mutable to compare to, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	@Override
	public int compareTo(final MutableDouble other)
	{
		return Double.compare(_value, other._value);
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
