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
 * Bird Guard AI.
 * @author Gladicek
 */
public final class BirdGuard extends AbstractNpcAI
{
	// NPCs
	private static final int GUARD = 33105;
	
	private BirdGuard()
	{
		addSpawnId(GUARD);
		addMoveFinishedId(GUARD);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "NPC_SHOUT1":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.VISIT_THE_ADMINISTRATION_OFFICE_TO_COMPLETE_YOUR_CLASS_TRANSFERS);
				getTimers().addTimer("NPC_SHOUT1", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT2":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IMPROVE_YOURSELF_FASTER_AT_THE_TRAINING_GROUNDS);
				getTimers().addTimer("NPC_SHOUT1", (10 + getRandom(5)) * 1000, npc, null);
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
		
		if (npcstring == 1)
		{
			getTimers().addTimer("NPC_SHOUT1", (10 + getRandom(5)) * 1000, npc, null);
		}
		else if (npcstring == 2)
		{
			getTimers().addTimer("NPC_SHOUT2", (10 + getRandom(5)) * 1000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		if (getRandom(100) < 30)
		{
			if (getRandom(20) < 10)
			{
				SuperpointManager.getInstance().stopMoving(npc, true, false);
				npc.broadcastSocialAction(10);
				getTimers().addTimer("CONTINUE", 4500, npc, null);
			}
			else
			{
				SuperpointManager.getInstance().stopMoving(npc, true, false);
				npc.broadcastSocialAction(9);
				getTimers().addTimer("CONTINUE", 4500, npc, null);
			}
		}
	}
	
	@GameScript
	public static void main()
	{
		new BirdGuard();
	}
}