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
package org.l2junity.gameserver.model.events.returns;

import org.l2junity.gameserver.enums.CombatState;

/**
 * @author lord_rex
 */
public final class CombatStateReturn extends AbstractEventReturn
{
	private final CombatState _value;
	
	public CombatStateReturn(boolean override, boolean abort, CombatState value)
	{
		super(override, abort);
		_value = value;
	}
	
	public CombatState getValue()
	{
		return _value;
	}
	
	public static CombatStateReturn of(CombatState combatState)
	{
		return new CombatStateReturn(false, false, combatState);
	}
}
