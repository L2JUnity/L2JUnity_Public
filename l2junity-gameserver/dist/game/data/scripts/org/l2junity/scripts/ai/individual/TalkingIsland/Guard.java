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
import org.l2junity.gameserver.instancemanager.SuperpointManager;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Guard AI.
 * @author Gladicek
 */
public final class Guard extends AbstractNpcAI
{
	// NPCs
	private static final int GUARD = 33106;
	
	private Guard()
	{
		addSpawnId(GUARD);
		addMoveFinishedId(GUARD);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final int rand = getRandom(3);
		switch (event)
		{
			
			case "NPC_SHOUT1":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.INFORMATION_IS_COLLECTED_IN_THE_MUSEUM_WHERE_CAN_LEARN_ABOUT_THE_HEROES);
				getTimers().addTimer("NPC_SHOUT1", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT2":
			{
				if (rand == 0)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.VISIT_THE_ADMINISTRATION_OFFICE_TO_FORM_A_CLAN);
				}
				else if (rand == 1)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.KAKAI_MANAGES_THE_ADMINISTRATION_OFFICE);
				}
				getTimers().addTimer("NPC_SHOUT2", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT3":
			{
				if (rand == 0)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TRAINING_GROUNDS_HAS_UNDERGROUND_FACILITIES_AS_WELL);
				}
				else if (rand == 1)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IF_YOU_WISH_TO_USE_THE_TRAINING_GROUNDS_MEET_SHANNON);
				}
				getTimers().addTimer("NPC_SHOUT3", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT4":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_IS_TALKING_ISLAND_VILLAGE_RIGHT);
				getTimers().addTimer("NPC_SHOUT4", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "CONTINUE":
			{
				SuperpointManager.getInstance().resumeMoving(npc);
				break;
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final int npcstring = npc.getParameters().getInt("npcstring", -1);
		
		switch (npcstring)
		{
			case 1:
				getTimers().addTimer("NPC_SHOUT1", (10 + getRandom(5)) * 1000, npc, null);
				break;
			case 2:
				getTimers().addTimer("NPC_SHOUT2", (10 + getRandom(5)) * 1000, npc, null);
				break;
			case 3:
				getTimers().addTimer("NPC_SHOUT3", (10 + getRandom(5)) * 1000, npc, null);
				break;
			case 4:
				getTimers().addTimer("NPC_SHOUT4", (10 + getRandom(5)) * 1000, npc, null);
				break;
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		if (getRandom(100) < 30)
		{
			SuperpointManager.getInstance().stopMoving(npc, true, false);
			npc.broadcastSocialAction(2);
			getTimers().addTimer("CONTINUE", 4500, npc, null);
		}
	}
	
	@GameScript
	public static void main()
	{
		new Guard();
	}
}