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
package org.l2junity.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public final class TargetHandler
{
	private final Map<String, ITargetTypeHandler> _targetTypeHandlers = new HashMap<>();
	private final Map<String, IAffectScopeHandler> _affectScopeHandlers = new HashMap<>();
	private final Map<String, IAffectObjectHandler> _affectObjectHandlers = new HashMap<>();
	
	public void registerTargetTypeHandler(String name, ITargetTypeHandler handler)
	{
		_targetTypeHandlers.put(name, handler);
	}
	
	public ITargetTypeHandler getTargetTypeHandler(String name)
	{
		return _targetTypeHandlers.get(name);
	}
	
	public void registerAffectScopeHandler(String name, IAffectScopeHandler handler)
	{
		_affectScopeHandlers.put(name, handler);
	}
	
	public IAffectScopeHandler getAffectScopeHandler(String name)
	{
		return _affectScopeHandlers.get(name);
	}
	
	public void registerAffectObjectHandler(String name, IAffectObjectHandler handler)
	{
		_affectObjectHandlers.put(name, handler);
	}
	
	public IAffectObjectHandler getAffectObjectHandler(String name)
	{
		return _affectObjectHandlers.get(name);
	}
	
	public int getTargetTypeHandlersSize()
	{
		return _targetTypeHandlers.size();
	}
	
	public int getAffectScopeHandlersSize()
	{
		return _affectScopeHandlers.size();
	}
	
	public int getAffectObjectHandlersSize()
	{
		return _affectObjectHandlers.size();
	}
	
	protected TargetHandler()
	{
	}

	public static TargetHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static final class SingletonHolder
	{
		protected static final TargetHandler INSTANCE = new TargetHandler();
	}
}
