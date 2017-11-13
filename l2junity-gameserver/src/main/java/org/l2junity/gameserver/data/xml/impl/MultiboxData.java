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
package org.l2junity.gameserver.data.xml.impl;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.multibox.MultiboxSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * @author UnAfraid
 */
public class MultiboxData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiboxData.class);
	
	private final Map<String, MultiboxSettings> _data = new LinkedHashMap<>();
	
	protected MultiboxData()
	{
	}
	
	@Reload("multibox")
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		parseDatapackFile("config/MultiboxProtection.xml");
		LOGGER.info("Loaded {} settings.", _data.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "protection", protectionNode ->
		{
			final StatsSet set = new StatsSet(parseAttributes(protectionNode));
			final MultiboxSettings settings = _data.computeIfAbsent(set.getString("name"), MultiboxSettings::new);
			
			// Update the settings in case of reload
			settings.set(set);
			
			// Add some exceptions
			forEach(protectionNode, "whitelist", whitelistNode ->
			{
				final int source = settings.getProtectionType().generateHash(parseString(whitelistNode.getAttributes(), "source"));
				final int maxClients = parseInteger(whitelistNode.getAttributes(), "maxClients");
				settings.addToWhitelist(source, maxClients);
			});
		}));
	}
	
	public MultiboxSettings getSettings(String name)
	{
		return _data.get(name);
	}
	
	/**
	 * Gets the single instance of MultiboxData.
	 * @return single instance of MultiboxData
	 */
	@InstanceGetter
	public static MultiboxData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MultiboxData INSTANCE = new MultiboxData();
	}
}
