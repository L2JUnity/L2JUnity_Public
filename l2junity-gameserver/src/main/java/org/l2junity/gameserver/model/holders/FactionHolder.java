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
package org.l2junity.gameserver.model.holders;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2junity.gameserver.enums.Faction;
import org.l2junity.gameserver.model.StatsSet;

/**
 * @author Sdw
 */
public class FactionHolder
{
	private final int _id;
	private final Faction _name;
	private final int _maxLevel;
	private final Map<Integer, Integer> _levelPoints = new HashMap<>();
	
	public FactionHolder(StatsSet set)
	{
		_id = set.getInt("id");
		_name = set.getEnum("name", Faction.class);
		_maxLevel = set.getInt("maxLevel");
	}
	
	public int getId()
	{
		return _id;
	}
	
	public Faction getName()
	{
		return _name;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int getLevel(int points)
	{
		return _levelPoints.entrySet().stream().filter(entry -> points < entry.getValue()).mapToInt(Entry::getKey).findFirst().orElse(0);
	}
	
	public float getProgressToNextLevel(int points)
	{
		final int currentLevel = getLevel(points);
		final int subPoints = currentLevel == 0 ? 0 : _levelPoints.get(currentLevel - 1);
		return (float) (points - subPoints) / (_levelPoints.get(currentLevel) - subPoints);
	}
	
	public void addPointsForLevel(int level, int points)
	{
		_levelPoints.put(level, points);
	}
	
	public int getPointsForLevel(int level)
	{
		return _levelPoints.get(level);
	}
}
