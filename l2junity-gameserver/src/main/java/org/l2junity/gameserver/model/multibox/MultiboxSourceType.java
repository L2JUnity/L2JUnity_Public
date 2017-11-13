/*
 * Copyright (C) 2004-2013 L2J Server
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
package org.l2junity.gameserver.model.multibox;

import org.l2junity.gameserver.network.client.L2GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public enum MultiboxSourceType
{
	IP
	{
		@Override
		public int generateHash(L2GameClient client)
		{
			return generateHash(client.getIP());
		}
	},
	HWID
	{
		@Override
		public int generateHash(L2GameClient client)
		{
			return generateHash(client.getHWID());
		}
	};
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiboxSourceType.class);
	
	public int generateHash(String value)
	{
		return value == null ? 0 : value.hashCode();
	}
	
	public abstract int generateHash(L2GameClient client);
	
	/**
	 * @param name
	 * @return
	 */
	public static MultiboxSourceType getByName(String name)
	{
		for (MultiboxSourceType type : values())
		{
			if (type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		LOGGER.warn("Requested for unexistent type: {}", name);
		return MultiboxSourceType.IP;
	}
}
