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

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.handler.BypassHandler;
import org.l2junity.gameserver.handler.IBypassHandler;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.WareHouseDepositList;
import org.l2junity.gameserver.network.client.send.WareHouseWithdrawalList;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

public class PrivateWarehouse implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"withdrawp",
		"depositp"
	};
	
	@Override
	public boolean useBypass(String command, PlayerInstance activeChar, Creature target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		if (activeChar.hasItemRequest())
		{
			return false;
		}
		
		try
		{
			if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawP
			{
				showWithdrawWindow(activeChar);
				return true;
			}
			else if (command.toLowerCase().startsWith(COMMANDS[1])) // DepositP
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				activeChar.setActiveWarehouse(activeChar.getWarehouse());
				activeChar.setInventoryBlockingStatus(true);
				activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.PRIVATE));
				return true;
			}
			
			return false;
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	private static final void showWithdrawWindow(PlayerInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
			return;
		}
		
		player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE));
		
		if (GeneralConfig.DEBUG)
		{
			LOGGER.debug("Source: L2WarehouseInstance.java; Player: " + player.getName() + "; Command: showRetrieveWindow; Message: Showing stored items.");
		}
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	@GameScript
	public static void main()
	{
		BypassHandler.getInstance().registerHandler(new PrivateWarehouse());
	}
}