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
package org.l2junity.commons.lang.mutable;

public final class MutableComparator
{
	private MutableComparator()
	{
		// utility class
	}
	
	/**
	 * <p>
	 * Compares two {@code int} values numerically. This is the same functionality as provided in Java 7.
	 * </p>
	 * @param x the first {@code int} to compare
	 * @param y the second {@code int} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y}; and a value greater than {@code 0} if {@code x > y}
	 * @since 3.4
	 */
	public static int compare(final int x, final int y)
	{
		if (x == y)
		{
			return 0;
		}
		return x < y ? -1 : 1;
	}
	
	/**
	 * <p>
	 * Compares to {@code long} values numerically. This is the same functionality as provided in Java 7.
	 * </p>
	 * @param x the first {@code long} to compare
	 * @param y the second {@code long} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y}; and a value greater than {@code 0} if {@code x > y}
	 * @since 3.4
	 */
	public static int compare(final long x, final long y)
	{
		if (x == y)
		{
			return 0;
		}
		return x < y ? -1 : 1;
	}
	
	/**
	 * <p>
	 * Compares to {@code short} values numerically. This is the same functionality as provided in Java 7.
	 * </p>
	 * @param x the first {@code short} to compare
	 * @param y the second {@code short} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y}; and a value greater than {@code 0} if {@code x > y}
	 * @since 3.4
	 */
	public static int compare(final short x, final short y)
	{
		if (x == y)
		{
			return 0;
		}
		return x < y ? -1 : 1;
	}
	
	/**
	 * <p>
	 * Compares two {@code byte} values numerically. This is the same functionality as provided in Java 7.
	 * </p>
	 * @param x the first {@code byte} to compare
	 * @param y the second {@code byte} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y}; and a value greater than {@code 0} if {@code x > y}
	 * @since 3.4
	 */
	public static int compare(final byte x, final byte y)
	{
		return x - y;
	}
	
	/**
	 * <p>
	 * Compares two {@code boolean} values. This is the same functionality as provided in Java 7.
	 * </p>
	 * @param x the first {@code boolean} to compare
	 * @param y the second {@code boolean} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code !x && y}; and a value greater than {@code 0} if {@code x && !y}
	 * @since 3.4
	 */
	public static int compare(final boolean x, final boolean y)
	{
		if (x == y)
		{
			return 0;
		}
		return x ? 1 : -1;
	}
}
