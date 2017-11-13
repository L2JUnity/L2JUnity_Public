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

import org.l2junity.gameserver.enums.AttributeType;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;

/**
 * @author Sdw
 */
public final class ChangeAttributeRequest extends AbstractRequest
{
	private volatile int _changeAttributeItemObjectId;
	private volatile int _changeAttributeCrystalObjectId;
	private AttributeType _attributeType = null;
	
	public ChangeAttributeRequest(PlayerInstance activeChar, int changeAttributeCrystalObjectId)
	{
		super(activeChar);
		_changeAttributeCrystalObjectId = changeAttributeCrystalObjectId;
	}
	
	public ItemInstance getChangeAttributeItem()
	{
		return getActiveChar().getInventory().getItemByObjectId(_changeAttributeItemObjectId);
	}
	
	public void setChangeAttributeItem(int objectId)
	{
		_changeAttributeItemObjectId = objectId;
	}
	
	public ItemInstance getChangeAttributeCrystal()
	{
		return getActiveChar().getInventory().getItemByObjectId(_changeAttributeCrystalObjectId);
	}
	
	public void setChangeAttributeCrystal(int objectId)
	{
		_changeAttributeCrystalObjectId = objectId;
	}
	
	public void setAttributeType(AttributeType attributeType)
	{
		_attributeType = attributeType;
	}
	
	public AttributeType getAttributeType()
	{
		return _attributeType;
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
		return (objectId > 0) && ((objectId == _changeAttributeItemObjectId) || (objectId == _changeAttributeCrystalObjectId));
	}
}
