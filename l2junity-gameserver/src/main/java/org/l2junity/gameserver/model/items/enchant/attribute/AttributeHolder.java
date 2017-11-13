/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.model.items.enchant.attribute;

import org.l2junity.gameserver.enums.AttributeType;

/**
 * @author UnAfraid
 */
public class AttributeHolder
{
	private final AttributeType _type;
	private int _value;
	
	public AttributeHolder(AttributeType type, int value)
	{
		_type = type;
		_value = value;
	}
	
	public AttributeType getType()
	{
		return _type;
	}
	
	public int getValue()
	{
		return _value;
	}
	
	public void setValue(int value)
	{
		_value = value;
	}
	
	public void incValue(int with)
	{
		_value += with;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((_type == null) ? 0 : _type.hashCode());
		result = (prime * result) + _value;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AttributeHolder))
		{
			return false;
		}
		else if (obj == this)
		{
			return true;
		}
		final AttributeHolder objInstance = (AttributeHolder) obj;
		return (_type == objInstance.getType()) && (_value == objInstance.getValue());
	}
	
	@Override
	public String toString()
	{
		return _type.name() + " +" + _value;
	}
}
