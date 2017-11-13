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
import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.fishing.FishingBait;
import org.l2junity.gameserver.model.fishing.FishingRod;
import org.l2junity.gameserver.model.holders.CatchHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * This class holds the Fishing information.
 * @author bit
 */
public final class FishingData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FishingData.class);
	private final Map<Integer, FishingRod> _rodData = new HashMap<>();
	private final Map<Integer, FishingBait> _baitData = new HashMap<>();
	private int _minPlayerLevel;
	private int _baitDistanceMin;
	private int _baitDistanceMax;
	private int _catchRate;
	private ExpressionBuilder _expFormula;
	private double _spRate;
	
	/**
	 * Instantiates a new fishing data.
	 */
	protected FishingData()
	{
	}
	
	@Reload("fishing")
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		_rodData.clear();
		_baitData.clear();
		parseDatapackFile("data/fishing.xml");
		LOGGER.info("Loaded Fishing Data.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "playerLevel", playerLevelNode ->
			{
				_minPlayerLevel = parseInteger(playerLevelNode.getAttributes(), "min");
			});
			
			forEach(listNode, "baitDistance", baitDistanceNode ->
			{
				_baitDistanceMin = parseInteger(baitDistanceNode.getAttributes(), "min");
				_baitDistanceMax = parseInteger(baitDistanceNode.getAttributes(), "max");
			});
			
			forEach(listNode, "rate", rateNode ->
			{
				final String expression = parseString(rateNode.getAttributes(), "exp");
				if (expression.startsWith("{") && expression.endsWith("}"))
				{
					_expFormula = new ExpressionBuilder(expression);
				}
				else
				{
					throw new IllegalArgumentException("Fishing exp node should be a formula !");
				}
				_spRate = parseDouble(rateNode.getAttributes(), "spRate");
			});
			
			forEach(listNode, "catchRate", catchRateNode ->
			{
				_catchRate = parseInteger(catchRateNode.getAttributes(), "chance");
			});
			
			forEach(listNode, "rods", rodsNode -> forEach(rodsNode, "rod", rodNode ->
			{
				_rodData.put(parseInteger(rodNode.getAttributes(), "id"), new FishingRod(new StatsSet(parseAttributes(rodNode))));
			}));
			
			forEach(listNode, "baits", baitsNode -> forEach(baitsNode, "bait", baitNode ->
			{
				final FishingBait bait = new FishingBait(new StatsSet(parseAttributes(baitNode)));
				forEach(baitNode, "catch", catchNode ->
				{
					bait.addCatch(new CatchHolder(new StatsSet(parseAttributes(catchNode))));
				});
				_baitData.put(bait.getItemId(), bait);
			}));
		});
	}
	
	public int getBaitDataCount()
	{
		return _baitData.size();
	}
	
	/**
	 * Gets the fishing rod.
	 * @param baitItemId the item id
	 * @return A list of reward item ids
	 */
	public FishingBait getBaitData(int baitItemId)
	{
		return _baitData.get(baitItemId);
	}
	
	public FishingRod getRodData(int rodItemId)
	{
		return _rodData.get(rodItemId);
	}
	
	public int getMinPlayerLevel()
	{
		return _minPlayerLevel;
	}
	
	public int getBaitDistanceMin()
	{
		return _baitDistanceMin;
	}
	
	public int getBaitDistanceMax()
	{
		return _baitDistanceMax;
	}
	
	public int getCatchRate()
	{
		return _catchRate;
	}
	
	public long getExpReward(int level, double multiplier)
	{
		return (long) _expFormula.variable("player.level").variable("catch.multiplier").build().setVariable("player.level", level).setVariable("catch.multiplier", multiplier).evaluate();
	}
	
	public double getSpRate()
	{
		return _spRate;
	}
	
	/**
	 * Gets the single instance of FishingData.
	 * @return single instance of FishingData
	 */
	@InstanceGetter
	public static FishingData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FishingData _instance = new FishingData();
	}
}
