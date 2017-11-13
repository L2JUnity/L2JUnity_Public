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
 * Provides mutable access to a value.
 * <p>
 * <code>Mutable</code> is used as a generic interface to the implementations in this package.
 * <p>
 * A typical use case would be to enable a primitive or string to be passed to a method and allow that method to effectively change the value of the primitive/string. Another use case is to store a frequently changing primitive in a collection (for example a total in a map) without needing to create
 * new Integer/Long wrapper objects.
 * @param <T> the type to set and get
 * @since 2.1
 */
public interface Mutable<T>
{
	/**
	 * Gets the value of this mutable.
	 * @return the stored value
	 */
	T getValue();
	
	/**
	 * Sets the value of this mutable.
	 * @param value the value to store
	 * @throws NullPointerException if the object is null and null is invalid
	 * @throws ClassCastException if the type is invalid
	 */
	void setValue(T value);
}
