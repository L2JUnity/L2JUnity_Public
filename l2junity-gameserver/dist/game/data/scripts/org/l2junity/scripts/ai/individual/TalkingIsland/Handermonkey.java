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
package org.l2junity.scripts.ai.individual.TalkingIsland;

import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Handermonkey AI.
 * @author Gladicek
 */
public final class Handermonkey extends AbstractNpcAI
{
	// NPC
	private static final int HANDERMONKEY = 33203;
	
	private Handermonkey()
	{
		addSpawnId(HANDERMONKEY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_MOVE") && (npc != null))
		{
			if (getRandom(100) < 70)
			{
				final double x = npc.getSpawn().getX() + (getRandom(-100, 100));
				final double y = npc.getSpawn().getY() + (getRandom(-100, 100));
				final Location loc = GeoData.getInstance().moveCheck(npc.getX(), npc.getY(), npc.getZ(), x, y, npc.getZ(), npc.getInstanceWorld());
				addMoveToDesire(npc, loc, 0);
			}
			else
			{
				npc.broadcastSocialAction(9);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRunning();
		startQuestTimer("NPC_MOVE", 5000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Handermonkey();
	}
}