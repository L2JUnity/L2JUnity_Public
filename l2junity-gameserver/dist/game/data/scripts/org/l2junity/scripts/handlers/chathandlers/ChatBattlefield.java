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
package org.l2junity.scripts.handlers.chathandlers;

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.handler.ChatHandler;
import org.l2junity.gameserver.handler.IChatHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Battlefield chat handler.
 * @author Nik
 */
public final class ChatBattlefield implements IChatHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBattlefield.class);
	
	private static final ChatType[] CHAT_TYPES =
	{
		ChatType.BATTLEFIELD,
	};
	
	@Override
	public void handleChat(ChatType type, PlayerInstance activeChar, String params, String text)
	{
		// This chat type is no longer used on retail due to the removal of territory wars.
		// This class acts only as a placeholder to prevent unnecessary errors or be customized.
		LOGGER.debug("Disabled chat type {} used by {}. Msg: {}. Params: {}.", type, activeChar, text, params);
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
	
	@GameScript
	public static void main()
	{
		ChatHandler.getInstance().registerHandler(new ChatBattlefield());
	}
}