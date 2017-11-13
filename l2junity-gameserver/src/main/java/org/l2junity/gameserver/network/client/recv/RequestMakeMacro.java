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
package org.l2junity.gameserver.network.client.recv;

import java.util.ArrayList;
import java.util.List;

import org.l2junity.gameserver.enums.MacroType;
import org.l2junity.gameserver.model.Macro;
import org.l2junity.gameserver.model.MacroCmd;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.network.PacketReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestMakeMacro implements IClientIncomingPacket
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMakeMacro.class);
	
	private Macro _macro;
	private int _commandsLenght = 0;
	
	private static final int MAX_MACRO_LENGTH = 12;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		final int id = packet.readD();
		final String name = packet.readS();
		final String desc = packet.readS();
		final String acronym = packet.readS();
		final int icon = packet.readD();
		int count = packet.readC();
		if (count > MAX_MACRO_LENGTH)
		{
			count = MAX_MACRO_LENGTH;
		}
		
		final List<MacroCmd> commands = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			final int entry = packet.readC();
			final int type = packet.readC(); // 1 = skill, 3 = action, 4 = shortcut
			if ((type < 1) || (type > 6))
			{
				LOGGER.warn("Client: {} Sent unhandled macro type: {} within packet: {}", client, type, getClass().getSimpleName());
				return false;
			}
			final int skillId = packet.readD(); // skill or page number for shortcuts
			final int shortcutId = packet.readC();
			String command = packet.readS();
			_commandsLenght += command.length();
			commands.add(new MacroCmd(entry, MacroType.values()[type], skillId, shortcutId, command));
		}
		_macro = new Macro(id, icon, name, desc, acronym, commands);
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final PlayerInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		if (_commandsLenght > 255)
		{
			// Invalid macro. Refer to the Help file for instructions.
			player.sendPacket(SystemMessageId.INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS);
			return;
		}
		if (player.getMacros().getAllMacroses().size() > 48)
		{
			// You may create up to 48 macros.
			player.sendPacket(SystemMessageId.YOU_MAY_CREATE_UP_TO_48_MACROS);
			return;
		}
		if (_macro.getName().isEmpty())
		{
			// Enter the name of the macro.
			player.sendPacket(SystemMessageId.ENTER_THE_NAME_OF_THE_MACRO);
			return;
		}
		if (_macro.getDescr().length() > 32)
		{
			// Macro descriptions may contain up to 32 characters.
			player.sendPacket(SystemMessageId.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
			return;
		}
		player.registerMacro(_macro);
	}
}
