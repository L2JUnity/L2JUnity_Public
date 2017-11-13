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
package org.l2junity.gameserver.enums;

/**
 * @author UnAfraid
 */
public enum NobleStatus
{
	NONE(0),
	NOBLESS(1),
	EXALTED_1(2),
	EXALTED_2(2),
	EXALTED_3(2);
	
	private int _clientId;
	
	private NobleStatus(int clientId)
	{
		_clientId = clientId;
	}
	
	public int getClientId()
	{
		return _clientId;
	}
	
	public static NobleStatus valueOf(int status)
	{
		if ((status < 0) || (status >= values().length))
		{
			return NONE;
		}
		return values()[status];
	}
}
