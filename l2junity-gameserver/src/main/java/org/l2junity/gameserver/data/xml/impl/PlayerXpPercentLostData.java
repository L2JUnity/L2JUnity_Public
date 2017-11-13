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
import java.util.Arrays;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class holds the Player Xp Percent Lost Data for each level for players.
 * @author Zealar
 */
public final class PlayerXpPercentLostData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerXpPercentLostData.class);
	
	private double[] _playerXpPercentLost = new double[ExperienceData.getInstance().getMaxLevel() + 1];
	
	protected PlayerXpPercentLostData()
	{
		Arrays.fill(_playerXpPercentLost, 1.);
	}
	
	@Reload("xploss")
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = ExperienceData.class))
	protected void load() throws Exception
	{
		_playerXpPercentLost = new double[ExperienceData.getInstance().getMaxLevel() + 1];
		parseDatapackFile("data/stats/chars/playerXpPercentLost.xml");
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
					if ("xpLost".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						
						final int level = parseInteger(attrs, "level");
						if (level < _playerXpPercentLost.length)
						{
							_playerXpPercentLost[level] = parseDouble(attrs, "val");
						}
					}
				}
			}
		}
	}
	
	public double getXpPercent(final int level)
	{
		if (level >= _playerXpPercentLost.length)
		{
			LOGGER.warn("Require to high level inside PlayerXpPercentLostData (" + level + ")");
			return _playerXpPercentLost[_playerXpPercentLost.length - 1];
		}
		return _playerXpPercentLost[level];
	}
	
	/**
	 * Gets the single instance of PlayerXpPercentLostData.
	 * @return single instance of PlayerXpPercentLostData.
	 */
	@InstanceGetter
	public static PlayerXpPercentLostData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerXpPercentLostData _instance = new PlayerXpPercentLostData();
	}
}
