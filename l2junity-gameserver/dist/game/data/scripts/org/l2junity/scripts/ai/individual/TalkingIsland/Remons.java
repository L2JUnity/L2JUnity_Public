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

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Remons AI.
 * @author Gladicek
 */
public final class Remons extends AbstractNpcAI
{
	// NPC
	private static final int REMONS = 33570;
	private static final int SOROS = 33218;
	// Distances
	private static final int MIN_DISTANCE = 70;
	private static final int MAX_DISTANCE = 200;
	
	private Remons()
	{
		addSpawnId(REMONS);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "NPC_SHOUT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.PERHAPS_EVEN_THE_VILLAGE_BECOMES_DANGEROUS);
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "WALK_AROUND_SOROS":
			{
				followNpc(npc, SOROS, 240, MIN_DISTANCE, MAX_DISTANCE);
				break;
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		followNpc(npc, SOROS, 240, MIN_DISTANCE, MAX_DISTANCE);
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		getTimers().addRepeatingTimer("WALK_AROUND_SOROS", 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Remons();
	}
}