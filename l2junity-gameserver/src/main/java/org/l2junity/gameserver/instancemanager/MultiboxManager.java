/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.gameserver.instancemanager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.GameServer;
import org.l2junity.gameserver.cache.HtmCache;
import org.l2junity.gameserver.data.xml.impl.MultiboxData;
import org.l2junity.gameserver.loader.ClientAccessLoadGroup;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.Containers;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.multibox.MultiboxHolder;
import org.l2junity.gameserver.model.multibox.MultiboxSettings;
import org.l2junity.gameserver.model.multibox.MultiboxSourceType;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class MultiboxManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiboxManager.class);
	
	private final Map<Object, MultiboxHolder> _holders = new ConcurrentHashMap<>();
	
	protected MultiboxManager()
	{
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGOUT, event -> onPlayerLogout((OnPlayerLogout) event), this));
	}
	
	@Load(group = ClientAccessLoadGroup.class)
	private void load()
	{
		registerManager(GameServer.getInstance());
	}
	
	/**
	 * @param obj
	 */
	public void registerManager(Object obj)
	{
		final MultiboxSettings settings = MultiboxData.getInstance().getSettings(obj.getClass().getSimpleName());
		if (settings != null)
		{
			_holders.put(obj, new MultiboxHolder(settings));
		}
		else
		{
			LOGGER.warn("Attempt to register manager ({}) with non-existent settings:", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		}
	}
	
	/**
	 * @param obj
	 */
	public void unregisterManager(Object obj)
	{
		_holders.remove(obj);
	}
	
	/**
	 * @param obj
	 */
	public void clearManager(Object obj)
	{
		if (_holders.containsKey(obj))
		{
			_holders.get(obj).clear();
		}
		else
		{
			LOGGER.warn("Attempt to clear non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		}
	}
	
	/**
	 * @param obj
	 * @param client
	 * @return {@code true} if player does not excess maximum amount of dual boxes, {@code false} otherwise.
	 */
	public boolean canRegisterClient(Object obj, L2GameClient client)
	{
		if (_holders.containsKey(obj))
		{
			return _holders.get(obj).canAddClient(client);
		}
		LOGGER.warn("Attempt to check for registered client on non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		return false;
	}
	
	/**
	 * @param obj
	 * @param client
	 * @return
	 */
	public boolean registerClient(Object obj, L2GameClient client)
	{
		if (_holders.containsKey(obj))
		{
			return _holders.get(obj).addClient(client);
		}
		LOGGER.warn("Attempt to register client on non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		return false;
	}
	
	/**
	 * @param obj
	 * @param client
	 */
	public void unregisterClient(Object obj, L2GameClient client)
	{
		if (_holders.containsKey(obj))
		{
			_holders.get(obj).removeClient(client);
		}
		else
		{
			LOGGER.warn("Attempt to unregister client on non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		}
	}
	
	/**
	 * @param obj
	 * @param client
	 * @return
	 */
	public int getCurrentConnections(Object obj, L2GameClient client)
	{
		if (_holders.containsKey(obj))
		{
			return _holders.get(obj).getCurrentConnections(client);
		}
		LOGGER.warn("Attempt to get current connections of client ({}) on non-existent manager ({}):", client, obj.getClass().getSimpleName(), new UnsupportedOperationException());
		return 0;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public int getMaximumConnectionsLimit(Object obj)
	{
		if (_holders.containsKey(obj))
		{
			return _holders.get(obj).getSettings().getMaxClients();
		}
		LOGGER.warn("Attempt to get max connections limit on non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
		return 0;
	}
	
	private void onPlayerLogout(OnPlayerLogout event)
	{
		final PlayerInstance player = event.getActiveChar();
		final L2GameClient client = event.getClient();
		if ((player == null) || (client == null))
		{
			LOGGER.warn("Player or client is null, {}, {}", player, client);
			return;
		}
		
		for (MultiboxHolder holder : _holders.values())
		{
			if (holder.getSettings().unregisterOnDisconnected())
			{
				holder.removeClient(client);
			}
			holder.clearDisconnected();
		}
	}
	
	/**
	 * @param obj
	 * @param client
	 * @return Set of multiboxes of current client.
	 */
	public Set<L2GameClient> getMultiboxesOf(Object obj, L2GameClient client)
	{
		if (!_holders.containsKey(obj))
		{
			return Collections.emptySet();
		}
		
		final MultiboxHolder holder = _holders.get(obj);
		final MultiboxSourceType type = holder.getSettings().getProtectionType();
		
		int hash = type.generateHash(client);
		if ((hash == 0) || !holder.getAllClients().containsKey(hash))
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(holder.getAllClients().get(hash));
	}
	
	/**
	 * @param obj
	 * @param client
	 */
	public void sendDefaultRestrictionMessage(Object obj, L2GameClient client)
	{
		final MultiboxHolder holder = _holders.get(obj);
		if (holder == null)
		{
			LOGGER.warn("Attempt to send default restriction message on non-existent manager ({}):", obj.getClass().getSimpleName(), new UnsupportedOperationException());
			return;
		}
		
		final String html = HtmCache.getInstance().getHtm(null, holder.getSettings().getRestrictionHtmlFile());
		if (html == null)
		{
			LOGGER.warn("Restriction html file is missing: {} !", holder.getSettings().getRestrictionHtmlFile());
			return;
		}
		
		final StringBuilder sb = new StringBuilder();
		final Set<L2GameClient> clients = getMultiboxesOf(obj, client);
		if (!clients.isEmpty())
		{
			sb.append("<table width=\"100%\">");
			for (L2GameClient multiboxClient : getMultiboxesOf(obj, client))
			{
				if (multiboxClient.getActiveChar() != null)
				{
					sb.append("<tr><td>Character: <font color=\"LEVEL\">" + multiboxClient.getActiveChar().getName() + "</font></td></tr>");
				}
				else
				{
					sb.append("<tr><td>Account: <font color=\"LEVEL\">" + multiboxClient.getAccountName() + "</font></td></tr>");
				}
			}
			sb.append("</table>");
		}
		final NpcHtmlMessage msg = new NpcHtmlMessage(html);
		msg.replace("%max%", Integer.toString(getMaximumConnectionsLimit(obj)));
		msg.replace("%type%", holder.getSettings().getProtectionType().name());
		msg.replace("%clients%", sb.toString());
		client.sendPacket(msg);
	}
	
	@InstanceGetter
	public static MultiboxManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MultiboxManager INSTANCE = new MultiboxManager();
	}
}
