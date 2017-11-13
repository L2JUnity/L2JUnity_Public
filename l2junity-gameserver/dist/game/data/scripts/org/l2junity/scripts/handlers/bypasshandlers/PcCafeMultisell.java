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
package org.l2junity.scripts.handlers.bypasshandlers;

import org.l2junity.gameserver.data.xml.impl.MultisellData;
import org.l2junity.gameserver.handler.BypassHandler;
import org.l2junity.gameserver.handler.IBypassHandler;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

public class PcCafeMultisell implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"pcc_multisell"
	};
	
	@Override
	public boolean useBypass(String command, PlayerInstance activeChar, Creature target)
	{
		try
		{
			final int listId = Integer.parseInt(command.substring(13).trim());
			MultisellData.getInstance().separateAndSend(listId, activeChar, null, false);
			return true;
		}
		catch (Exception e)
		{
			LOGGER.warn("An exception occured when requesting a pcc_multisell", e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	@GameScript
	public static void main()
	{
		BypassHandler.getInstance().registerHandler(new PcCafeMultisell());
	}
}