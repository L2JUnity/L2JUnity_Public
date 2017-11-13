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
package org.l2junity.gameserver.network.client;

import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventDispatcher;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2junity.gameserver.network.client.send.IClientOutgoingPacket;
import org.l2junity.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NB4L1
 */
public final class Disconnection
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Disconnection.class);
	
	public static L2GameClient getClient(L2GameClient client, PlayerInstance activeChar)
	{
		if (client != null)
		{
			return client;
		}
		
		if (activeChar != null)
		{
			return activeChar.getClient();
		}
		
		return null;
	}
	
	public static PlayerInstance getActiveChar(L2GameClient client, PlayerInstance activeChar)
	{
		if (activeChar != null)
		{
			return activeChar;
		}
		
		if (client != null)
		{
			return client.getActiveChar();
		}
		
		return null;
	}
	
	private final L2GameClient _client;
	private final PlayerInstance _activeChar;
	
	private Disconnection(L2GameClient client)
	{
		this(client, null);
	}
	
	public static Disconnection of(L2GameClient client)
	{
		return new Disconnection(client);
	}
	
	private Disconnection(PlayerInstance activeChar)
	{
		this(null, activeChar);
	}
	
	public static Disconnection of(PlayerInstance activeChar)
	{
		return new Disconnection(activeChar);
	}
	
	private Disconnection(L2GameClient client, PlayerInstance activeChar)
	{
		_client = getClient(client, activeChar);
		_activeChar = getActiveChar(client, activeChar);
		
		if (_client != null)
		{
			_client.setActiveChar(null);
		}
		
		if (_activeChar != null)
		{
			_activeChar.setClient(null);
		}
	}
	
	public static Disconnection of(L2GameClient client, PlayerInstance activeChar)
	{
		return new Disconnection(client, activeChar);
	}
	
	public Disconnection storeMe()
	{
		try
		{
			if (_activeChar != null)
			{
				_activeChar.storeMe();
			}
		}
		catch (final RuntimeException e)
		{
			LOGGER.warn("", e);
		}
		
		return this;
	}
	
	public Disconnection deleteMe()
	{
		try
		{
			if (_activeChar != null)
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(_activeChar, _client), _activeChar);
				_activeChar.deleteMe();
			}
		}
		catch (final RuntimeException e)
		{
			LOGGER.warn("", e);
		}
		
		return this;
	}
	
	public Disconnection close(boolean toLoginScreen)
	{
		if (_client != null)
		{
			_client.close(toLoginScreen);
		}
		
		return this;
	}
	
	public Disconnection close(IClientOutgoingPacket packet)
	{
		if (_client != null)
		{
			_client.close(packet);
		}
		
		return this;
	}
	
	public void defaultSequence(boolean toLoginScreen)
	{
		defaultSequence();
		close(toLoginScreen);
	}
	
	public void defaultSequence(IClientOutgoingPacket packet)
	{
		defaultSequence();
		close(packet);
	}
	
	private void defaultSequence()
	{
		storeMe();
		deleteMe();
	}
	
	public void onDisconnection()
	{
		if (_activeChar != null)
		{
			ThreadPool.schedule(() -> defaultSequence(), _activeChar.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME, TimeUnit.MILLISECONDS);
		}
	}
}