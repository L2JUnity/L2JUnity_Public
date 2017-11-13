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
package org.l2junity.gameserver.model.actor.instance;

import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * This class manages all chest.
 * @author Julian
 */
public final class TreasureInstance extends L2MonsterInstance
{
	private volatile boolean _dropEnabled;
	
	public TreasureInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2ChestInstance);
		setRandomWalking(false);
	}
	
	public void setDropEnabled(boolean val)
	{
		_dropEnabled = val;
	}
	
	@Override
	public void doItemDrop(L2NpcTemplate npcTemplate, Creature lastAttacker)
	{
		if (!_dropEnabled)
		{
			return;
		}
		
		super.doItemDrop(npcTemplate, lastAttacker);
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	@Override
	public boolean isTreasure()
	{
		return true;
	}
	
	@Override
	public TreasureInstance asTreasure()
	{
		return this;
	}
}
