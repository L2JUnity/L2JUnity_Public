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
package org.l2junity.gameserver.model.cache;

/**
 * @author Sdw
 */
public class RelationCache
{
	private int _relation;
	private boolean _isAutoAttackable;
	
	public RelationCache(int relation, boolean isAutoAttackable)
	{
		_relation = relation;
		_isAutoAttackable = isAutoAttackable;
	}
	
	public int getRelation()
	{
		return _relation;
	}
	
	public boolean isAutoAttackable()
	{
		return _isAutoAttackable;
	}
	
	public void setRelation(int relation)
	{
		_relation = relation;
	}
	
	public void setAutoAttackable(boolean isAutoAttackable)
	{
		_isAutoAttackable = isAutoAttackable;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (_isAutoAttackable ? 1231 : 1237);
		result = (prime * result) + _relation;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof RelationCache))
		{
			return false;
		}
		else if (obj == this)
		{
			return true;
		}
		RelationCache other = (RelationCache) obj;
		return (_isAutoAttackable == other.isAutoAttackable()) && (_relation == other.getRelation());
	}
	
}
