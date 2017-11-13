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

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.enums.Position;
import org.l2junity.gameserver.instancemanager.GameTimeManager;
import org.l2junity.gameserver.loader.LoadGroup;
import org.l2junity.gameserver.model.actor.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class load, holds and calculates the hit condition bonuses.
 * @author Nik
 */
public final class HitConditionBonusData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HitConditionBonusData.class);
	
	private int frontBonus = 0;
	private int sideBonus = 0;
	private int backBonus = 0;
	private int highBonus = 0;
	private int lowBonus = 0;
	private int darkBonus = 0;
	private int rainBonus = 0;
	
	/**
	 * Instantiates a new hit condition bonus.
	 */
	protected HitConditionBonusData()
	{
	}
	
	@Load(group = LoadGroup.class)
	private void load() throws Exception
	{
		parseDatapackFile("data/stats/hitConditionBonus.xml");
		LOGGER.info("Loaded Hit Condition bonuses.");
		if (GeneralConfig.DEBUG)
		{
			LOGGER.info("Front bonus: {}", frontBonus);
			LOGGER.info("Side bonus: {}", sideBonus);
			LOGGER.info("Back bonus: {}", backBonus);
			LOGGER.info("High bonus: {}", highBonus);
			LOGGER.info("Low bonus: {}", lowBonus);
			LOGGER.info("Dark bonus: {}", darkBonus);
			LOGGER.info("Rain bonus: {}", rainBonus);
		}
	}
	
	public int getLoadedElementsCount()
	{
		// FIXME Tardish way to count "loaded elements"
		return frontBonus + sideBonus + backBonus + highBonus + lowBonus + darkBonus + rainBonus;
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		for (Node d = doc.getFirstChild().getFirstChild(); d != null; d = d.getNextSibling())
		{
			NamedNodeMap attrs = d.getAttributes();
			switch (d.getNodeName())
			{
				case "front":
				{
					frontBonus = parseInteger(attrs, "val");
					break;
				}
				case "side":
				{
					sideBonus = parseInteger(attrs, "val");
					break;
				}
				case "back":
				{
					backBonus = parseInteger(attrs, "val");
					break;
				}
				case "high":
				{
					highBonus = parseInteger(attrs, "val");
					break;
				}
				case "low":
				{
					lowBonus = parseInteger(attrs, "val");
					break;
				}
				case "dark":
				{
					darkBonus = parseInteger(attrs, "val");
					break;
				}
				case "rain":
				{
					rainBonus = parseInteger(attrs, "val");
					break;
				}
			}
		}
	}
	
	/**
	 * Gets the condition bonus.
	 * @param attacker the attacking character.
	 * @param target the attacked character.
	 * @return the bonus of the attacker against the target.
	 */
	public double getConditionBonus(Creature attacker, Creature target)
	{
		double mod = 100;
		// Get high or low bonus
		if ((attacker.getZ() - target.getZ()) > 50)
		{
			mod += highBonus;
		}
		else if ((attacker.getZ() - target.getZ()) < -50)
		{
			mod += lowBonus;
		}
		
		// Get weather bonus
		if (GameTimeManager.getInstance().isNight())
		{
			mod += darkBonus;
			// else if () No rain support yet.
			// chance += hitConditionBonus.rainBonus;
		}
		
		// Get side bonus
		switch (Position.getPosition(attacker, target))
		{
			case SIDE:
				mod += sideBonus;
				break;
			case BACK:
				mod += backBonus;
				break;
			default:
				mod += frontBonus;
				break;
		}
		
		// If (mod / 100) is less than 0, return 0, because we can't lower more than 100%.
		return Math.max(mod / 100, 0);
	}
	
	/**
	 * Gets the single instance of HitConditionBonus.
	 * @return single instance of HitConditionBonus
	 */
	@InstanceGetter
	public static HitConditionBonusData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final HitConditionBonusData _instance = new HitConditionBonusData();
	}
}