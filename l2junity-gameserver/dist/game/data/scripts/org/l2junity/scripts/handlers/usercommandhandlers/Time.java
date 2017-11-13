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

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.handler.IUserCommandHandler;
import org.l2junity.gameserver.handler.UserCommandHandler;
import org.l2junity.gameserver.instancemanager.GameTimeManager;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Time user command.
 */
public class Time implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		77
	};
	
	private static final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");
	
	@Override
	public boolean useUserCommand(int id, PlayerInstance activeChar)
	{
		if (COMMAND_IDS[0] != id)
		{
			return false;
		}

		final SystemMessage sm = GameTimeManager.getInstance().isNight() ? SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S22) : SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S2);
		final LocalTime gameTime = GameTimeManager.getInstance().getGameTime();
		sm.addString((gameTime.getHour() < 10 ? "0" : "") + gameTime.getHour());
		sm.addString((gameTime.getMinute() < 10 ? "0" : "") + gameTime.getMinute());
		activeChar.sendPacket(sm);
		if (L2JModsConfig.L2JMOD_DISPLAY_SERVER_TIME)
		{
			activeChar.sendMessage("Server time is " + fmt.format(new Date(System.currentTimeMillis())));
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@GameScript
	public static void main()
	{
		UserCommandHandler.getInstance().registerHandler(new Time());
	}
}