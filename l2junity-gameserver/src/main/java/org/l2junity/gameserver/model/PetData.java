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
package org.l2junity.gameserver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class hold information about basic pet stats which are same on each level.
 * @author JIV
 */
public class PetData
{
	private final Map<Integer, PetLevelData> _levelStats = new HashMap<>();
	
	private final int _npcId;
	private final int _itemId;
	private int _load = 20000;
	private boolean _syncLevel = false;
	
	public PetData(int npcId, int itemId)
	{
		_npcId = npcId;
		_itemId = itemId;
	}
	
	/**
	 * @return the npc id representing this pet.
	 */
	public int getNpcId()
	{
		return _npcId;
	}
	
	/**
	 * @return the item id that could summon this pet.
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * @param level the pet's level.
	 * @param data the pet's data.
	 */
	public void addNewStat(int level, PetLevelData data)
	{
		_levelStats.put(level, data);
	}
	
	/**
	 * @param petLevel the pet's level.
	 * @return the pet data associated to that pet level.
	 */
	public PetLevelData getPetLevelData(int petLevel)
	{
		return _levelStats.get(petLevel);
	}
	
	/**
	 * @return the pet's weight load.
	 */
	public int getLoad()
	{
		return _load;
	}
	
	/**
	 * @return {@code true} if pet synchronizes it's level with his master's
	 */
	public boolean isSynchLevel()
	{
		return _syncLevel;
	}
	
	/**
	 * @param load the weight load to set.
	 */
	public void setLoad(int load)
	{
		_load = load;
	}
	
	/**
	 * @param val synchronizes level with master or not.
	 */
	public void setSyncLevel(boolean val)
	{
		_syncLevel = val;
	}
}
