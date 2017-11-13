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
package org.l2junity.gameserver.model.actor.request;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

/**
 * A request that handles crystallization estimate and crystallization action.
 * @author Nik
 */
public class CrystallizationRequest extends AbstractRequest
{
	private final int _objectId;
	private final long _count;
	
	public CrystallizationRequest(PlayerInstance activeChar, int objectId, long count)
	{
		super(activeChar);
		_objectId = objectId;
		_count = count;
	}
	
	public ItemInstance getItem()
	{
		return getActiveChar().getInventory().getItemByObjectId(_objectId);
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	@Override
	public boolean isItemRequest()
	{
		return true;
	}
	
	@Override
	public boolean canWorkWith(AbstractRequest request)
	{
		return !request.isItemRequest();
	}
	
	@Override
	public boolean isUsing(int objectId)
	{
		return (objectId > 0) && (objectId == _objectId);
	}
}
