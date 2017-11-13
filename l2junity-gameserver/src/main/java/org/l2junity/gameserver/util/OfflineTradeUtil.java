/*
 * Copyright (C) 2004-2016 L2J Unity
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

import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.olympiad.OlympiadManager;
import org.l2junity.gameserver.model.zone.ZoneId;
import org.l2junity.gameserver.network.client.Disconnection;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public final class OfflineTradeUtil
{
	protected static final Logger LOG_ACCOUNTING = LoggerFactory.getLogger("accounting");
	
	private OfflineTradeUtil()
	{
		// utility class
	}
	
	/**
	 * Check whether player is able to enter offline mode.
	 * @param player the player to be check.
	 * @return {@code true} if the player is allowed to remain as off-line shop.
	 */
	private static boolean offlineMode(final PlayerInstance player)
	{
		if ((player == null) || player.isInOlympiadMode() || player.isBlockedFromExit() || player.isJailed() || (player.getVehicle() != null))
		{
			return false;
		}
		
		boolean canSetShop = false;
		switch (player.getPrivateStoreType())
		{
			case SELL:
			case PACKAGE_SELL:
			case BUY:
			{
				canSetShop = L2JModsConfig.OFFLINE_TRADE_ENABLE;
				break;
			}
			case MANUFACTURE:
			{
				canSetShop = L2JModsConfig.OFFLINE_TRADE_ENABLE;
				break;
			}
			default:
			{
				canSetShop = L2JModsConfig.OFFLINE_CRAFT_ENABLE && player.isCrafting();
				break;
			}
		}
		
		if (L2JModsConfig.OFFLINE_MODE_IN_PEACE_ZONE && !player.isInsideZone(ZoneId.PEACE))
		{
			canSetShop = false;
		}
		
		// Check whether client is null or player is already in offline mode.
		final L2GameClient client = player.getClient();
		if ((client == null) || client.isDetached())
		{
			return false;
		}
		
		return canSetShop;
	}
	
	/**
	 * Manages the disconnection process of offline traders.
	 * @param player
	 * @return {@code true} when player entered offline mode, otherwise {@code false}
	 */
	public static boolean enteredOfflineMode(final PlayerInstance player)
	{
		if (!OfflineTradeUtil.offlineMode(player))
		{
			return false;
		}
		
		final L2GameClient client = player.getClient();
		client.setDetached(true);
		
		player.leaveParty();
		OlympiadManager.getInstance().unRegisterNoble(player);
		
		// If the L2PcInstance has Pet, unsummon it
		Summon pet = player.getPet();
		if (pet != null)
		{
			pet.setRestoreSummon(true);
			
			pet.unSummon(player);
			pet = player.getPet();
			// Dead pet wasn't unsummoned, broadcast npcinfo changes (pet will be without owner name - means owner offline)
			if (pet != null)
			{
				pet.broadcastNpcInfo(0);
			}
		}
		
		player.getServitors().values().forEach(s ->
		{
			s.setRestoreSummon(true);
			s.unSummon(player);
		});
		
		if (L2JModsConfig.OFFLINE_SET_NAME_COLOR)
		{
			player.getAppearance().setNameColor(L2JModsConfig.OFFLINE_NAME_COLOR);
			player.broadcastUserInfo();
		}
		
		if (player.getOfflineStartTime() == 0)
		{
			player.setOfflineStartTime(System.currentTimeMillis());
		}
		
		Disconnection.of(player).storeMe().close(false);
		LOG_ACCOUNTING.info("Entering offline mode, {}", client);
		return true;
	}
}
