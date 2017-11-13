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

import java.util.StringTokenizer;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.handler.ChatHandler;
import org.l2junity.gameserver.handler.IChatHandler;
import org.l2junity.gameserver.handler.IVoicedCommandHandler;
import org.l2junity.gameserver.handler.VoicedCommandHandler;
import org.l2junity.gameserver.model.BlockList;
import org.l2junity.gameserver.model.PcCondOverride;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.CreatureSay;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General Chat Handler.
 * @author durgus
 */
public final class ChatGeneral implements IChatHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatGeneral.class);
	
	private static final ChatType[] CHAT_TYPES =
	{
		ChatType.GENERAL,
	};
	
	@Override
	public void handleChat(ChatType type, PlayerInstance activeChar, String params, String text)
	{
		boolean vcd_used = false;
		if (text.startsWith("."))
		{
			final StringTokenizer st = new StringTokenizer(text);
			final IVoicedCommandHandler vch;
			String command = "";
			
			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			}
			else
			{
				command = text.substring(1);
				if (GeneralConfig.DEBUG)
				{
					LOGGER.info("Command: " + command);
				}
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			}
			if (vch != null)
			{
				vch.useVoicedCommand(command, activeChar, params);
				vcd_used = true;
			}
			else
			{
				if (GeneralConfig.DEBUG)
				{
					LOGGER.warn("No handler registered for bypass '" + command + "'");
				}
				vcd_used = false;
			}
		}
		
		if (!vcd_used)
		{
			if (activeChar.isChatBanned() && GeneralConfig.BAN_CHAT_CHANNELS.contains(type))
			{
				activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER_CHATTING_BAN_TIME_REMAINING_S1_SECONDS);
				return;
			}
			
			if ((activeChar.getLevel() < GeneralConfig.MINIMUM_CHAT_LEVEL) && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PLAYERS_CAN_USE_GENERAL_CHAT_AFTER_LV_S1).addInt(GeneralConfig.MINIMUM_CHAT_LEVEL));
				return;
			}
			
			final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getAppearance().getVisibleName(), text);
			
			if (GeneralConfig.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("global"))
			{
				if (!activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("global chat"))
				{
					activeChar.sendMessage("Do not spam shout channel.");
					return;
				}
				
				for (PlayerInstance player : World.getInstance().getPlayers())
				{
					if (!BlockList.isBlocked(player, activeChar))
					{
						player.sendPacket(cs);
					}
				}
				return;
			}
			
			World.getInstance().forEachVisibleObjectInRadius(activeChar, PlayerInstance.class, 1250, player ->
			{
				if (!BlockList.isBlocked(player, activeChar))
				{
					player.sendPacket(cs);
				}
			});
			activeChar.sendPacket(cs);
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
	
	@GameScript
	public static void main()
	{
		ChatHandler.getInstance().registerHandler(new ChatGeneral());
	}
}