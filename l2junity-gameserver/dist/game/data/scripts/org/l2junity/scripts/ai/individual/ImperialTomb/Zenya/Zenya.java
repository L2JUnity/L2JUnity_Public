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
package org.l2junity.scripts.ai.individual.ImperialTomb.Zenya;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Zenya AI.
 * @author Nik
 */
public final class Zenya extends AbstractNpcAI
{
	// NPC
	private static final int ZENYA = 32140;
	// Location
	private static final Location TELEPORT_LOC = new Location(183399, -81012, -5320);
	// Misc
	private static final int MIN_LEVEL = 80;
	
	private Zenya()
	{
		addStartNpc(ZENYA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "teleport":
			{
				htmltext = player.getLevel() >= MIN_LEVEL ? "32140-4.htm" : "32140-5.htm";
				break;
			}
			case "imperial_tomb":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					player.teleToLocation(TELEPORT_LOC);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@GameScript
	public static void main()
	{
		new Zenya();
	}
}
