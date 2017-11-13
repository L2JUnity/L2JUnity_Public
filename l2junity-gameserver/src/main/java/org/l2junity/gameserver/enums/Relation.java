/*
 * Copyright (C) 2004-2017 L2J Unity
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

/**
 * @author Sdw
 */
public enum Relation
{
	INSIDE_BATTLEFIELD(0x00001),
	IN_PVP(0x00002),
	CHAOTIC(0x00004),
	IN_PARTY(0x00008),
	PARTY_LEADER(0x00010),
	SAME_PARTY(0x00020),
	IN_PLEDGE(0x00040),
	PLEDGE_LEADER(0x00080),
	SAME_PLEDGE(0x00100),
	SIEGE_PARTICIPANT(0x00200),
	SIEGE_ATTACKER(0x00400),
	SIEGE_ALLY(0x00800),
	SIEGE_ENEMY(0x01000),
	CLAN_WAR_ATTACKER(0x04000),
	CLAN_WAR_ATTACKED(0x08000),
	IN_ALLIANCE(0x10000),
	ALLIANCE_LEADER(0x20000),
	SAME_ALLIANCE(0x40000);
	
	private final int _mask;
	
	private Relation(int mask)
	{
		_mask = mask;
	}
	
	public int getMask()
	{
		return _mask;
	}
}
