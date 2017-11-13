/*
 * Copyright (C) 2004-2013 L2J Unity
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
package org.l2junity.scripts.handlers.telnethandlers.server;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.network.telnet.ITelnetCommand;
import org.l2junity.gameserver.network.telnet.TelnetServer;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * @author UnAfraid
 */
public class Performance implements ITelnetCommand
{
	private Performance()
	{
	}
	
	@Override
	public String getCommand()
	{
		return "performance";
	}
	
	@Override
	public String getUsage()
	{
		return "Performance";
	}
	
	@Override
	public String handle(String ipAddress, String[] args)
	{
		ThreadPool.purge();
		final StringBuilder sb = new StringBuilder();
		for (String line : ThreadPool.getStats())
		{
			sb.append(line + System.lineSeparator());
		}
		return sb.toString();
	}
	
	@GameScript
	public static void main()
	{
		TelnetServer.getInstance().addHandler(new Performance());
	}
}
