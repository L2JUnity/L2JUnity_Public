/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.usercommandhandlers;

import org.l2junity.gameserver.handler.IUserCommandHandler;
import org.l2junity.gameserver.handler.UserCommandHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Mount user command.
 * @author Tempy
 */
public class Mount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		61
	};
	
	@Override
	public synchronized boolean useUserCommand(int id, PlayerInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		return activeChar.mountPlayer(activeChar.getPet());
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@GameScript
	public static void main()
	{
		UserCommandHandler.getInstance().registerHandler(new Mount());
	}
}