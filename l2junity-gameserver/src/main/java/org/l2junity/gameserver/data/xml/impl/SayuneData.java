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
package org.l2junity.gameserver.data.xml.impl;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.SayuneEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 */
public class SayuneData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SayuneData.class);
	
	private final Map<Integer, SayuneEntry> _maps = new HashMap<>();
	
	protected SayuneData()
	{
	}
	
	@Reload("sayune")
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		parseDatapackFile("data/SayuneData.xml");
		LOGGER.info("Loaded: {} maps.", _maps.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("map".equalsIgnoreCase(d.getNodeName()))
					{
						final int id = parseInteger(d.getAttributes(), "id");
						final SayuneEntry map = new SayuneEntry(id);
						parseEntries(map, d);
						_maps.put(map.getId(), map);
					}
				}
			}
		}
	}
	
	private void parseEntries(SayuneEntry lastEntry, Node n)
	{
		NamedNodeMap attrs;
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if ("selector".equals(d.getNodeName()) || "choice".equals(d.getNodeName()) || "loc".equals(d.getNodeName()))
			{
				attrs = d.getAttributes();
				final int id = parseInteger(attrs, "id");
				final int x = parseInteger(attrs, "x");
				final int y = parseInteger(attrs, "y");
				final int z = parseInteger(attrs, "z");
				
				parseEntries(lastEntry.addInnerEntry(new SayuneEntry("selector".equals(d.getNodeName()), id, x, y, z)), d);
			}
		}
	}
	
	public SayuneEntry getMap(int id)
	{
		return _maps.get(id);
	}
	
	public Collection<SayuneEntry> getMaps()
	{
		return _maps.values();
	}
	
	/**
	 * Gets the single instance of SayuneData.
	 * @return single instance of SayuneData
	 */
	@InstanceGetter
	public static final SayuneData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final SayuneData _instance = new SayuneData();
	}
}
