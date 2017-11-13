/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.commons.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author NB4L1 (l2jfree methods)
 * @author UnAfraid (contains mechanism)
 * @author lord_rex (re-architecture)
 * @author Apache Commons-Lang3 (only the static empty arrays)
 */
public final class ArrayUtil
{
	/**
	 * An empty immutable {@code Object} array.
	 */
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
	/**
	 * An empty immutable {@code Class} array.
	 */
	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
	
	/**
	 * An empty immutable {@code String} array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	/**
	 * An empty immutable {@code long} array.
	 */
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	
	/**
	 * An empty immutable {@code Long} array.
	 */
	public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
	
	/**
	 * An empty immutable {@code int} array.
	 */
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	
	/**
	 * An empty immutable {@code Integer} array.
	 */
	public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
	
	/**
	 * An empty immutable {@code short} array.
	 */
	public static final short[] EMPTY_SHORT_ARRAY = new short[0];
	
	/**
	 * An empty immutable {@code Short} array.
	 */
	public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
	
	/**
	 * An empty immutable {@code byte} array.
	 */
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	/**
	 * An empty immutable {@code Byte} array.
	 */
	public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
	
	/**
	 * An empty immutable {@code double} array.
	 */
	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
	
	/**
	 * An empty immutable {@code Double} array.
	 */
	public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
	
	/**
	 * An empty immutable {@code float} array.
	 */
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	
	/**
	 * An empty immutable {@code Float} array.
	 */
	public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
	
	/**
	 * An empty immutable {@code boolean} array.
	 */
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
	
	/**
	 * An empty immutable {@code Boolean} array.
	 */
	public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
	
	/**
	 * An empty immutable {@code char} array.
	 */
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];
	
	/**
	 * An empty immutable {@code Character} array.
	 */
	public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
	
	private ArrayUtil()
	{
		// utility class
	}
	
	/**
	 * Checks whether array is empty.
	 * @param <T>
	 * @param array
	 * @return {@code true} in case array is empty, otherwise {@code false}
	 */
	public static <T> boolean isEmpty(T[] array)
	{
		return (array == null) || (array.length == 0);
	}
	
	/**
	 * Checks whether array is not empty.
	 * @param <T>
	 * @param array
	 * @return {@code true} in case array is not empty, otherwise {@code false}
	 */
	public static <T> boolean isNotEmpty(T[] array)
	{
		return (array != null) && (array.length > 0);
	}
	
	/**
	 * @param <T>
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		for (T element : array)
		{
			if (element.equals(obj))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the integer to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise
	 */
	public static boolean contains(int[] array, int obj)
	{
		for (int element : array)
		{
			if (element == obj)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @param ignoreCase
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static boolean contains(String[] array, String obj, boolean ignoreCase)
	{
		for (String element : array)
		{
			if (element.equals(obj) || (ignoreCase && element.equalsIgnoreCase(obj)))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tests all elements from {@code objects} for reference equality.
	 * @param objects
	 * @return {@code false} if {@code objects} contain distinct elements, {@code true} otherwise
	 */
	public static boolean isSameObject(Object... objects)
	{
		for (int i = 1; i < objects.length; ++i)
		{
			if (objects[i] != objects[i - 1])
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static int countNull(Object[] array)
	{
		if (array == null)
		{
			return 0;
		}
		
		int nullCount = 0;
		
		for (final Object obj : array)
		{
			if (obj == null)
			{
				++nullCount;
			}
		}
		
		return nullCount;
	}
	
	public static int countNotNull(Object[] array)
	{
		return array == null ? 0 : array.length - countNull(array);
	}
	
	/**
	 * @param <T>
	 * @param array to remove null elements from
	 * @return an array without null elements - can be the same, if the original contains no null elements
	 * @throws NullPointerException if array is null
	 */
	public static <T> T[] compact(T[] array)
	{
		final int newSize = countNotNull(array);
		
		if (array.length == newSize)
		{
			return array;
		}
		
		@SuppressWarnings("unchecked")
		final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), newSize);
		
		int index = -1;
		
		for (final T t : array)
		{
			if (t != null)
			{
				result[++index] = t;
			}
		}
		
		return result;
	}
	
	/*
	 * Commented out, because L2JUnity policy restricts CopyOnWriteArrayList.
	 * @param <T>
	 * @param array to create a list from
	 * @return a List&lt;T&gt;, which will NOT throw ConcurrentModificationException, if an element gets removed inside a foreach loop, and supports addition
	 */
	//@formatter:off
	/*public static <T> List<T> asForeachSafeList(T... array)
	{
		return asForeachSafeList(true, array);
	}*/
	
	/*
	 * Commented out, because L2JUnity policy restricts CopyOnWriteArrayList.
	 * @param <T>
	 * @param allowAddition determines that list MUST support add operation or not
	 * @param array to create a list from
	 * @return a List&lt;T&gt;, which will NOT throw ConcurrentModificationException, if an element
	 *         gets removed inside a foreach loop, and supports addition if required
	 */
	/*public static <T> List<T> asForeachSafeList(boolean allowAddition, T... array)
	{
		final int newSize = countNotNull(array);
		
		if (newSize == 0 && !allowAddition)
			return Collections.emptyList();
		
		if (newSize <= 8)
			return new CopyOnWriteArrayList<>(compact(array));
		
		final List<T> result = new ArrayList<>(newSize);
		
		for (final T t : array)
			if (t != null)
				result.add(t);
			
		return result;
	}*/
	//@formatter:on
	
	public static <T> Iterable<T> iterable(Object[] array)
	{
		return new NullFreeArrayIterable<>(array);
	}
	
	public static <T> Iterable<T> iterable(Object[] array, boolean allowNull)
	{
		if (allowNull)
		{
			return new ArrayIterable<>(array);
		}
		return new NullFreeArrayIterable<>(array);
	}
	
	private static class ArrayIterable<T> implements Iterable<T>
	{
		protected final Object[] _array;
		
		protected ArrayIterable(Object[] array)
		{
			_array = array;
		}
		
		@Override
		public Iterator<T> iterator()
		{
			return new ArrayIterator<>(_array);
		}
	}
	
	private static final class NullFreeArrayIterable<T> extends ArrayIterable<T>
	{
		protected NullFreeArrayIterable(Object[] array)
		{
			super(array);
		}
		
		@Override
		public Iterator<T> iterator()
		{
			return new NullFreeArrayIterator<>(_array);
		}
	}
	
	public static <T> Iterator<T> iterator(Object[] array)
	{
		return new NullFreeArrayIterator<>(array);
	}
	
	public static <T> Iterator<T> iterator(Object[] array, boolean allowNull)
	{
		if (allowNull)
		{
			return new ArrayIterator<>(array);
		}
		return new NullFreeArrayIterator<>(array);
	}
	
	private static class ArrayIterator<T> implements Iterator<T>
	{
		private final Object[] _array;
		
		private int _index;
		
		protected ArrayIterator(Object[] array)
		{
			_array = array;
		}
		
		/**
		 * @param obj
		 * @return boolean
		 */
		boolean allowElement(Object obj)
		{
			return true;
		}
		
		@Override
		public final boolean hasNext()
		{
			for (;;)
			{
				if (_array.length <= _index)
				{
					return false;
				}
				
				if (allowElement(_array[_index]))
				{
					return true;
				}
				
				_index++;
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public final T next()
		{
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			
			return (T) _array[_index++];
		}
		
		@Override
		public final void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class NullFreeArrayIterator<T> extends ArrayIterator<T>
	{
		protected NullFreeArrayIterator(Object[] array)
		{
			super(array);
		}
		
		@Override
		boolean allowElement(Object obj)
		{
			return obj != null;
		}
	}
}
