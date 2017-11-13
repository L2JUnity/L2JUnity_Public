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
package org.l2junity.gameserver.model.multibox;

import java.util.LinkedHashMap;
import java.util.Map;

import org.l2junity.gameserver.model.StatsSet;

/**
 * @author UnAfraid
 */
public class MultiboxSettings
{
	private final String _name;
	private int _maxClients;
	private MultiboxSourceType _protectionType;
	private String _restrictionHtmlFile;
	private boolean _unregisterOnDisconnected;
	private boolean _clearOnDisconnected;
	private final Map<Integer, Integer> _whitelisted = new LinkedHashMap<>();
	
	public MultiboxSettings(String name)
	{
		_name = name;
	}
	
	/**
	 * Sets all parameters to the variables, used to reload configuration without creation of new object.
	 * @param set
	 */
	public void set(StatsSet set)
	{
		_maxClients = set.getInt("maxClients", 0);
		_protectionType = set.getEnum("type", MultiboxSourceType.class, MultiboxSourceType.IP);
		_restrictionHtmlFile = set.getString("restrictionHtmlFile", "data/html/mods/MultiboxRestriction.htm");
		_unregisterOnDisconnected = set.getBoolean("unregisterOnDisconnected", false);
		_clearOnDisconnected = set.getBoolean("clearOnDisconnected", false);
		_whitelisted.clear();
	}
	
	/**
	 * @return name of class that is using this configuration.
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @return returns {@code true} if max clients are more 0, {@code false} otherwise.
	 */
	public boolean isEnabled()
	{
		return _maxClients > 0;
	}
	
	/**
	 * @return the maximum clients allowed.
	 */
	public int getMaxClients()
	{
		return _maxClients;
	}
	
	/**
	 * @return source type IPv4/HWID
	 */
	public MultiboxSourceType getProtectionType()
	{
		return _protectionType;
	}
	
	/**
	 * @return absolute html file path when player reach maximum amount of boxes.
	 */
	public String getRestrictionHtmlFile()
	{
		return _restrictionHtmlFile;
	}
	
	/**
	 * Registers hash with separate maximum clients amount.
	 * @param hash
	 * @param maxClients
	 */
	public void addToWhitelist(int hash, int maxClients)
	{
		_whitelisted.put(hash, maxClients);
	}
	
	/**
	 * @param hash
	 * @return the maximum boxes amount for the current hash 0 if not found.
	 */
	public int getWhitelistBonus(int hash)
	{
		return (_whitelisted.containsKey(hash) ? _whitelisted.get(hash) : 0);
	}
	
	/**
	 * @return {@code true} if client must be unregistered when it get's disconnected from the game, {@code false} otherwise.
	 */
	public boolean unregisterOnDisconnected()
	{
		return _unregisterOnDisconnected;
	}
	
	/**
	 * @return {@code true} if all un-active cleans has to be cleared when a player disconnects, {@code false} otherwise.
	 */
	public boolean clearOnDisconnected()
	{
		return _clearOnDisconnected;
	}
}
