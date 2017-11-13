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
package org.l2junity.scripts.handlers.playeractions;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.datatables.BotReportTable;
import org.l2junity.gameserver.handler.IPlayerActionHandler;
import org.l2junity.gameserver.handler.PlayerActionHandler;
import org.l2junity.gameserver.model.ActionDataHolder;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Bot Report button player action handler.
 * @author Nik
 */
public final class BotReport implements IPlayerActionHandler
{
	@Override
	public void useAction(PlayerInstance activeChar, ActionDataHolder data, boolean ctrlPressed, boolean shiftPressed)
	{
		if (GeneralConfig.BOTREPORT_ENABLE)
		{
			BotReportTable.getInstance().reportBot(activeChar);
		}
		else
		{
			activeChar.sendMessage("This feature is disabled.");
		}
	}
	
	@GameScript
	public static void main()
	{
		PlayerActionHandler.getInstance().registerHandler(new BotReport());
	}
}
