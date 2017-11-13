/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.instances.BaylorWarzone;

import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.DoorInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.instances.AbstractInstance;

/**
 * Baylor Warzone instance zone.
 * @author St3eT
 */
public final class BaylorWarzone extends AbstractInstance
{
	// NPCs
	private static final int BAYLOR = 29213;
	private static final int ENTRANCE_PORTAL = 33523;
	private static final int INVISIBLE_NPC = 29106;
	// Locations
	private static final Location BATTLE_PORT = new Location(153567, 143319, -12736);
	// Misc
	private static final int TEMPLATE_ID = 166;
	
	public BaylorWarzone()
	{
		super(TEMPLATE_ID);
		addStartNpc(ENTRANCE_PORTAL);
		addTalkId(ENTRANCE_PORTAL);
		addInstanceCreatedId(TEMPLATE_ID);
		addSpawnId(INVISIBLE_NPC);
		setCreatureSeeId(this::onCreatureSee, INVISIBLE_NPC);
		setCreatureKillId(this::onBossKill, BAYLOR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "START_SCENE":
				{
					playMovie(instance, Movie.BARLER_SCENE);
					getTimers().addTimer("SPAWN_BAYLOR", 17200, npc, null);
					break;
				}
				case "SPAWN_BAYLOR":
				{
					for (Npc baylor : instance.spawnGroup("BAYLOR"))
					{
						baylor.setRandomAnimation(false);
						baylor.setRandomWalking(false);
						((Attackable) baylor).setCanReturnToSpawnPoint(false);
					}
					npc.deleteMe();
					break;
				}
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		getTimers().addTimer("BATTLE_PORT", 3000, e ->
		{
			instance.getPlayers().forEach(p -> p.teleToLocation(BATTLE_PORT));
			instance.getDoors().forEach(DoorInstance::closeMe);
		});
	}
	
	private void onBossKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			if (instance.getAliveNpcs(BAYLOR).isEmpty())
			{
				instance.finishInstance();
			}
			else
			{
				instance.setReenterTime();
			}
		}
	}
	
	private void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && creature.isPlayer() && npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			getTimers().addTimer("START_SCENE", 5000, npc, null);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == INVISIBLE_NPC)
		{
			npc.initSeenCreatures();
		}
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new BaylorWarzone();
	}
}