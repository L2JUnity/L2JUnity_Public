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
package org.l2junity.gameserver.model.stats;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.l2junity.commons.util.IXmlReader;
import org.l2junity.commons.util.XmlReaderException;
import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.model.actor.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * @author DS, Sdw, UnAfraid
 */
public enum BaseStats
{
	STR(DoubleStat.STAT_STR),
	INT(DoubleStat.STAT_INT),
	DEX(DoubleStat.STAT_DEX),
	WIT(DoubleStat.STAT_WIT),
	CON(DoubleStat.STAT_CON),
	MEN(DoubleStat.STAT_MEN),
	CHA(DoubleStat.STAT_CHA),
	LUC(DoubleStat.STAT_LUC);
	
	private final double[] _bonus = new double[PlayerConfig.MAX_BASE_STAT + 1];
	private final DoubleStat _stat;
	
	BaseStats(DoubleStat stat)
	{
		_stat = stat;
	}
	
	public DoubleStat getStat()
	{
		return _stat;
	}
	
	public int calcValue(Creature creature)
	{
		if ((creature != null) && (_stat != null))
		{
			return (int) creature.getStat().getValue(_stat);
		}
		return 0;
	}
	
	public double calcBonus(Creature creature)
	{
		if (creature != null)
		{
			final int value = calcValue(creature);
			if (value < 1)
			{
				return 1;
			}
			return _bonus[value];
		}
		
		return 1;
	}
	
	void setValue(int index, double value)
	{
		_bonus[index] = value;
	}
	
	public double getValue(int index)
	{
		return _bonus[index];
	}
	
	public static BaseStats valueOf(DoubleStat stat)
	{
		for (BaseStats baseStat : values())
		{
			if (baseStat.getStat() == stat)
			{
				return baseStat;
			}
		}
		throw new NoSuchElementException("Unknown base stat '" + stat + "' for enum BaseStats");
	}
	
	static
	{
		new IGameXmlReader()
		{
			final Logger LOGGER = LoggerFactory.getLogger(BaseStats.class);
			
			protected void load()
			{
				try
				{
					parseDatapackFile("data/stats/statBonus.xml");
				}
				catch (XmlReaderException | IOException e)
				{
					LOGGER.info("Error loading stat bonus.", e);
				}
			}
			
			@Override
			public void parseDocument(Document doc, Path path)
			{
				forEach(doc, "list", listNode -> forEach(listNode, IXmlReader::isNode, statNode ->
				{
					final BaseStats baseStat;
					try
					{
						baseStat = valueOf(statNode.getNodeName());
					}
					catch (Exception e)
					{
						LOGGER.error("Invalid base stats type: {}, skipping", statNode.getNodeValue());
						return;
					}
					
					forEach(statNode, "stat", statValue ->
					{
						final NamedNodeMap attrs = statValue.getAttributes();
						final int val = parseInteger(attrs, "value");
						final double bonus = parseDouble(attrs, "bonus");
						baseStat.setValue(val, bonus);
					});
				}));
			}
		}.load();
	}
}