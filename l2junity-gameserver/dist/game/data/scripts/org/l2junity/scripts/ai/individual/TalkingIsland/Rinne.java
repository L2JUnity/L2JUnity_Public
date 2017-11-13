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
 * Rinne AI.
 * @author Gladicek
 */
public final class Rinne extends AbstractNpcAI
{
	// NPC
	private static final int RINNE = 33234;
	private static final int ALLADA = 33220;
	// Items
	private static final int WEAPON = 15302;
	
	private Rinne()
	{
		addSpawnId(RINNE);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "TID_LEFTWALK":
			{
				npc.setRHandId(WEAPON);
				getTimers().addTimer("TID_STRAIGHTWALK", (5000 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "TID_STRAIGHTWALK":
			{
				npc.unequipWeapon();
				getTimers().addTimer("TID_LEFTWALK", (5000 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ALL_RACES_CAME_TOGETHER_TO_REBUILD_TALKING_ISLAND_VILLAGE);
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_FOLLOW":
			{
				addSpawn(ALLADA, npc.getX() + 10, npc.getY() + 10, npc.getZ(), 0, false, 0);
				break;
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addTimer("TID_LEFTWALK", (5000 + getRandom(5)) * 1000, npc, null);
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		getTimers().addTimer("NPC_FOLLOW", 100, npc, null);
		SuperpointManager.getInstance().startMoving(npc, "si_town_01");
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Rinne();
	}
}