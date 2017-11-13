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

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.loader.LoadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * A table containing pre-calculated values of the previously hardcodded levelMod formula ((level + 89) / 100).
 * @author Nik
 */
public final class LevelBonusData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LevelBonusData.class);
	
	private double[] LEVEL_BONUS;
	
	protected LevelBonusData()
	{
	}
	
	@Load(group = LoadGroup.class, dependencies = @Dependency(clazz = ExperienceData.class))
	private void load() throws Exception
	{
		parseDatapackFile("data/stats/levelBonus.xml");
		
		// Check if bonuses for all levels are set.
		for (int i = 1; i < LEVEL_BONUS.length; i++)
		{
			if (LEVEL_BONUS[i] == 0)
			{
				LOGGER.warn("Level bonus for level {} is not set!", i);
			}
		}
		LOGGER.info("Loaded level bonus table. Max level with bonus is {}.", (LEVEL_BONUS.length - 1));
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		LEVEL_BONUS = new double[ExperienceData.getInstance().getMaxLevel()];
		
		forEach(doc.getFirstChild(), "levelbonus", levelBonusNode ->
		{
			final byte level = parseByte(levelBonusNode.getAttributes(), "level");
			if (level < LEVEL_BONUS.length)
			{
				LEVEL_BONUS[level] = parseDouble(levelBonusNode.getAttributes(), "value");
			}
		});
	}
	
	/**
	 * @param level
	 * @return the level bonus modifier for the given level.
	 */
	public double getLevelBonus(int level)
	{
		return level >= LEVEL_BONUS.length ? LEVEL_BONUS[LEVEL_BONUS.length - 1] : LEVEL_BONUS[level];
	}
	
	@InstanceGetter
	public static LevelBonusData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LevelBonusData INSTANCE = new LevelBonusData();
	}
}
