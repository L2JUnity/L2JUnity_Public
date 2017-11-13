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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.residences.ResidenceFunctionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The residence functions data
 * @author UnAfraid
 */
public final class ResidenceFunctionsData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceFunctionsData.class);
	private final Map<Integer, List<ResidenceFunctionTemplate>> _functions = new HashMap<>();
	
	protected ResidenceFunctionsData()
	{
	}
	
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		_functions.clear();
		parseDatapackFile("data/ResidenceFunctions.xml");
		LOGGER.info("Loaded: {} functions.", _functions.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", list -> forEach(list, "function", func ->
		{
			final NamedNodeMap attrs = func.getAttributes();
			final StatsSet set = new StatsSet(HashMap::new);
			for (int i = 0; i < attrs.getLength(); i++)
			{
				final Node node = attrs.item(i);
				set.set(node.getNodeName(), node.getNodeValue());
			}
			forEach(func, "function", levelNode ->
			{
				final NamedNodeMap levelAttrs = levelNode.getAttributes();
				final StatsSet levelSet = new StatsSet(HashMap::new);
				levelSet.merge(set);
				for (int i = 0; i < levelAttrs.getLength(); i++)
				{
					final Node node = levelAttrs.item(i);
					levelSet.set(node.getNodeName(), node.getNodeValue());
				}
				final ResidenceFunctionTemplate template = new ResidenceFunctionTemplate(levelSet);
				_functions.computeIfAbsent(template.getId(), key -> new ArrayList<>()).add(template);
			});
		}));
	}
	
	public int getFunctionCount()
	{
		return _functions.size();
	}
	
	/**
	 * @param id
	 * @param level
	 * @return function template by id and level, null if not available
	 */
	public ResidenceFunctionTemplate getFunction(int id, int level)
	{
		return _functions.getOrDefault(id, Collections.emptyList()).stream().filter(template -> template.getLevel() == level).findAny().orElse(null);
	}
	
	/**
	 * @param id
	 * @return function template by id, null if not available
	 */
	public List<ResidenceFunctionTemplate> getFunctions(int id)
	{
		return _functions.get(id);
	}
	
	@InstanceGetter
	public static final ResidenceFunctionsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ResidenceFunctionsData INSTANCE = new ResidenceFunctionsData();
	}
}
