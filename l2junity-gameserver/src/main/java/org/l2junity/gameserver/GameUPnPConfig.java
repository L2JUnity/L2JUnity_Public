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
package org.l2junity.gameserver;

import org.l2junity.commons.network.IUPnPConfig;
import org.l2junity.gameserver.config.ServerConfig;
import org.l2junity.gameserver.config.TelnetConfig;

/**
 * @author lord_rex
 */
public final class GameUPnPConfig implements IUPnPConfig
{
	@Override
	public boolean isAllowed()
	{
		return ServerConfig.ENABLE_UPNP;
	}
	
	@Override
	public String getApplicationName()
	{
		return "Game Server";
	}
	
	@Override
	public int getApplicationPort()
	{
		return ServerConfig.PORT_GAME;
	}
	
	@Override
	public boolean isTelnetAllowed()
	{
		return TelnetConfig.TELNET_UPNP_ENABLED;
	}
	
	@Override
	public String getTelnetName()
	{
		return "Game Telnet Server";
	}
	
	@Override
	public int getTelnetPort()
	{
		return TelnetConfig.TELNET_PORT;
	}
}
