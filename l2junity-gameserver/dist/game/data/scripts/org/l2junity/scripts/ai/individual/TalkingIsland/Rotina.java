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
 * Rotina AI.
 * @author Gladicek
 */
public final class Rotina extends AbstractNpcAI
{
	// NPC
	private static final int ROTINA = 33027;
	private static final int MEI = 33280;
	// Items
	private static final int WEAPON = 15304;
	// Distances
	private static final int MIN_DISTANCE = 70;
	private static final int MAX_DISTANCE = 200;
	
	private Rotina()
	{
		addSpawnId(ROTINA);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "NPC_SHOUT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_LL_EARN_TONS_OF_ITEMS_USING_THE_TRAINING_GROUNDS);
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "WALK_AROUND_MEI":
			{
				followNpc(npc, MEI, 115, MIN_DISTANCE, MAX_DISTANCE);
				break;
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRHandId(WEAPON);
		followNpc(npc, MEI, 115, MIN_DISTANCE, MAX_DISTANCE);
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		getTimers().addRepeatingTimer("WALK_AROUND_MEI", 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Rotina();
	}
}