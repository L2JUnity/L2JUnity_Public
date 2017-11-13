/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.gameserver.util;

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.CreatureSay;
import org.l2junity.gameserver.network.client.send.ExUserInfoAbnormalVisualEffect;

/**
 * @author lord_rex
 */
public final class BuilderUtil
{
	private BuilderUtil()
	{
		// utility class
	}
	
	/**
	 * Sends builder system message to the player.
	 * @param player
	 * @param message
	 */
	public static void sendSysMessage(final PlayerInstance player, final String message)
	{
		player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "SYS", message));
	}
	
	/**
	 * Changes player's hiding state.
	 * @param player
	 * @param hide
	 * @return {@code true} if hide state was changed, otherwise {@code false}
	 */
	public static boolean setHiding(final PlayerInstance player, final boolean hide)
	{
		if (player.isInvisible() && hide)
		{
			// already hiding
			return false;
		}
		
		if (!player.isInvisible() && !hide)
		{
			// already visible
			return false;
		}
		
		player.setSilenceMode(hide);
		player.setIsInvul(hide);
		player.setInvisible(hide);
		
		player.broadcastUserInfo();
		player.sendPacket(new ExUserInfoAbnormalVisualEffect(player));
		return true;
	}
}
