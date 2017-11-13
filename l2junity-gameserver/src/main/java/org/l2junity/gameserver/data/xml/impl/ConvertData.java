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
package org.l2junity.gameserver.data.xml.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * @author Sdw
 */
public class ConvertData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertData.class);
	
	private final Map<Integer, Integer> _convertData = new HashMap<>();
	
	protected ConvertData()
	{
	}
	
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		_convertData.clear();
		parseDatapackFile("data/ConvertData.xml");
		LOGGER.info("Loaded {} weapon conversion data.", _convertData.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "convert", convertNode ->
		{
			_convertData.put(parseInteger(convertNode.getAttributes(), "input_item"), parseInteger(convertNode.getAttributes(), "output_item"));
		}));
	}
	
	public int getConversionId(int weaponId)
	{
		return _convertData.getOrDefault(weaponId, 0);
	}
	
	public int getLoadedElementsCount()
	{
		return _convertData.size();
	}
	
	@InstanceGetter
	public static final ConvertData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ConvertData _instance = new ConvertData();
	}
}