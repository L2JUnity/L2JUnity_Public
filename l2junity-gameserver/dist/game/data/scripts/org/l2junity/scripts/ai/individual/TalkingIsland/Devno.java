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
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Devno AI.
 * @author St3eT
 */
public final class Devno extends AbstractNpcAI
{
	// NPC
	private static final int DEVNO = 33241;
	
	private Devno()
	{
		addSpawnId(DEVNO);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_MOVE"))
		{
			if (getRandomBoolean())
			{
				addMoveToDesire(npc, GeoData.getInstance().moveCheck(npc.getLocation(), npc.getSpawn().getRandomPosition(0, 500), npc.getInstanceWorld()), 23);
				
			}
			getTimers().addTimer("NPC_MOVE", (10 + getRandom(5)) * 1000, npc, null);
		}
		else if (event.equals("NPC_SHOUT"))
		{
			switch (getRandom(4))
			{
				case 0:
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.CARRY_OUT_YOUR_QUESTS_FAITHFULLY_IN_TALKING_ISLAND_AND_YOU_LL_GET_TO_THE_1ST_CLASS_TRANSFER_IN_NO_TIME);
					break;
				case 1:
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_SEE_THAT_ADVENTURERS_ARE_RETURNING_TO_TALKING_ISLAND_FOR_THE_AWAKENING);
					break;
				case 2:
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_CAN_SEE_VARIOUS_STATISTICS_IN_THE_MUSEUM_STATS_IN_THE_MAIN_MENU);
					break;
			}
			getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addTimer("NPC_MOVE", (10 + getRandom(5)) * 1000, npc, null);
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Devno();
	}
}