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
package org.l2junity.gameserver.enums;

import org.l2junity.gameserver.model.items.Armor;
import org.l2junity.gameserver.model.items.L2Item;
import org.l2junity.gameserver.model.items.Weapon;

/**
 * @author Nik
 */
public enum CrystallizationType
{
	NONE,
	WEAPON,
	ARMOR,
	ACCESORY;
	
	public static CrystallizationType getByItem(L2Item item)
	{
		if (item instanceof Weapon)
		{
			return WEAPON;
		}
		if (item instanceof Armor)
		{
			return ARMOR;
		}
		switch (item.getBodyPart())
		{
			case L2Item.SLOT_R_EAR:
			case L2Item.SLOT_L_EAR:
			case L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_FINGER:
			case L2Item.SLOT_NECK:
			case L2Item.SLOT_HAIR:
			case L2Item.SLOT_HAIR2:
			case L2Item.SLOT_HAIRALL:
			{
				return ACCESORY;
			}
		}
		
		return NONE;
	}
}
