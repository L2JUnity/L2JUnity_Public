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
package org.l2junity.gameserver.instancemanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2junity.gameserver.config.L2JModsConfig;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.L2GameClient;

public final class AntiFeedManager
{
	private final Map<Integer, Long> _lastDeathTimes = new ConcurrentHashMap<>();
	
	protected AntiFeedManager()
	{
	}
	
	/**
	 * Set time of the last player's death to current
	 * @param objectId Player's objectId
	 */
	public final void updateTimeOfDeath(int objectId)
	{
		_lastDeathTimes.put(objectId, System.currentTimeMillis());
	}
	
	/**
	 * Check if current kill should be counted as non-feeded.
	 * @param attacker Attacker character
	 * @param target Target character
	 * @return True if kill is non-feeded.
	 */
	public final boolean check(Creature attacker, Creature target)
	{
		if (!L2JModsConfig.L2JMOD_ANTIFEED_ENABLE)
		{
			return true;
		}
		else if ((target == null) || (attacker == null))
		{
			return false;
		}
		
		final PlayerInstance targetPlayer = target.getActingPlayer();
		final PlayerInstance attackerPlayer = attacker.getActingPlayer();
		if ((targetPlayer == null) || (attackerPlayer == null))
		{
			return false;
		}
		
		final L2GameClient targetClient = targetPlayer.getClient();
		final L2GameClient attackerClient = attackerPlayer.getClient();
		if ((targetClient == null) || (attackerClient == null))
		{
			return false;
		}
		
		// Unable to check ip address
		if (targetPlayer.isOfflineShop() || attackerPlayer.isOfflineShop())
		{
			return false;
		}
		
		// Same HWID
		if (L2JModsConfig.L2JMOD_ANTIFEED_USE_HWID && attackerPlayer.hasSameHWID(targetPlayer))
		{
			return false;
		}
		
		// Same IP Address
		if (L2JModsConfig.L2JMOD_ANTIFEED_USE_IP && attackerPlayer.hasSameIP(targetPlayer))
		{
			return false;
		}
		
		if (L2JModsConfig.L2JMOD_ANTIFEED_INTERVAL > 0)
		{
			final long deathTimeDifference = System.currentTimeMillis() - _lastDeathTimes.getOrDefault(targetPlayer.getObjectId(), System.currentTimeMillis());
			if (deathTimeDifference > L2JModsConfig.L2JMOD_ANTIFEED_INTERVAL)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static AntiFeedManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AntiFeedManager INSTANCE = new AntiFeedManager();
	}
}