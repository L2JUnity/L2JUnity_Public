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
package org.l2junity.scripts.handlers.voicedcommandhandlers;

import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.handler.IVoicedCommandHandler;
import org.l2junity.gameserver.handler.VoicedCommandHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * This class trades Gold Bars for Adena and vice versa.
 * @author Ahmed
 */
public class Banking implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"bank",
		"withdraw",
		"deposit"
	};
	
	@Override
	public boolean useVoicedCommand(String command, PlayerInstance activeChar, String params)
	{
		if (command.equals("bank"))
		{
			activeChar.sendMessage(".deposit (" + L2JModsConfig.BANKING_SYSTEM_ADENA + " Adena = " + L2JModsConfig.BANKING_SYSTEM_GOLDBARS + " Goldbar) / .withdraw (" + L2JModsConfig.BANKING_SYSTEM_GOLDBARS + " Goldbar = " + L2JModsConfig.BANKING_SYSTEM_ADENA + " Adena)");
		}
		else if (command.equals("deposit"))
		{
			if (activeChar.getInventory().getInventoryItemCount(57, 0) >= L2JModsConfig.BANKING_SYSTEM_ADENA)
			{
				if (!activeChar.reduceAdena("Goldbar", L2JModsConfig.BANKING_SYSTEM_ADENA, activeChar, false))
				{
					return false;
				}
				activeChar.getInventory().addItem("Goldbar", 3470, L2JModsConfig.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage("Thank you, you now have " + L2JModsConfig.BANKING_SYSTEM_GOLDBARS + " Goldbar(s), and " + L2JModsConfig.BANKING_SYSTEM_ADENA + " less adena.");
			}
			else
			{
				activeChar.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + L2JModsConfig.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		else if (command.equals("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(3470, 0) >= L2JModsConfig.BANKING_SYSTEM_GOLDBARS)
			{
				if (!activeChar.destroyItemByItemId("Adena", 3470, L2JModsConfig.BANKING_SYSTEM_GOLDBARS, activeChar, false))
				{
					return false;
				}
				activeChar.getInventory().addAdena("Adena", L2JModsConfig.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage("Thank you, you now have " + L2JModsConfig.BANKING_SYSTEM_ADENA + " Adena, and " + L2JModsConfig.BANKING_SYSTEM_GOLDBARS + " less Goldbar(s).");
			}
			else
			{
				activeChar.sendMessage("You do not have any Goldbars to turn into " + L2JModsConfig.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
	
	@GameScript
	public static void main()
	{
		VoicedCommandHandler.getInstance().registerHandler(new Banking());
	}
}