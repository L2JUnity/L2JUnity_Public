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
package org.l2junity.scripts.instances.TautiWarzone;

import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.OnEventTrigger;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.instances.AbstractInstance;

/**
 * Tauti Warzone instance zone.
 * @author St3eT
 */
public final class TautiWarzone extends AbstractInstance
{
	// NPCs
	private static final int TAUTI = 29233;
	private static final int TAUTI_AXE = 29236;
	private static final int ZAHAK = 19266;
	private static final int KUNDA_LORD = 19265;
	private static final int FINARIA = 33675;
	private static final int TELEPORT_DEVICE = 33678;
	private static final int INVISIBLE_NPC = 18919;
	private static final int KUNDA_1 = 19262;
	private static final int KUNDA_2 = 19263;
	private static final int KUNDA_3 = 19264;
	// Items
	private static final int TELEPORT_KEY = 34899; // Key of Darkness
	// Locations
	private static final Location BATTLEGROUND_LOC = new Location(-149013, 209856, -10032);
	// Misc
	private static final int TEMPLATE_ID = 218;
	private static final int BATTLE_DOORS = 15240002;
	
	public TautiWarzone()
	{
		super(TEMPLATE_ID);
		addSpawnId(FINARIA, KUNDA_1, ZAHAK, KUNDA_LORD, KUNDA_2, KUNDA_3, INVISIBLE_NPC, TELEPORT_DEVICE, TAUTI_AXE);
		addAttackId(ZAHAK, TAUTI);
		addKillId(ZAHAK, TAUTI_AXE);
		addFirstTalkId(TELEPORT_DEVICE);
		addTalkId(TELEPORT_DEVICE);
		setCreatureSeeId(this::onCreatureSee, INVISIBLE_NPC, TELEPORT_DEVICE);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "OPENING_MOVIE":
				{
					playMovie(instance, Movie.SC_TAUTI_OPENING);
					getTimers().addTimer("TAUTI_SPAWN", 43000, npc, null);
					instance.openCloseDoor(BATTLE_DOORS, false);
					break;
				}
				case "TAUTI_SPAWN":
				{
					instance.spawnGroup("kadif03_1524_tauti_001m1");
					break;
				}
				case "TAUTI_AXE_SPAWN":
				{
					for (Npc tautiAxe : instance.spawnGroup("kadif03_1524_tauti_002m1"))
					{
						tautiAxe.setCurrentHp(tautiAxe.getCurrentHp() * 0.1);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "insertKey":
				{
					if (npc.isScriptValue(0))
					{
						if (player.isInParty() || player.isGM())
						{
							boolean isLeader = false;
							if (player.isInParty())
							{
								final Party party = player.getParty();
								isLeader = party.isInCommandChannel() ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
							}
							
							if (isLeader || player.isGM())
							{
								if (hasQuestItems(player, TELEPORT_KEY))
								{
									takeItems(player, TELEPORT_KEY, -1);
									htmltext = "TeleportDevice-02.html";
									npc.setScriptValue(1);
									npc.broadcastPacket(new OnEventTrigger(15235001, true));
								}
								else
								{
									htmltext = "TeleportDevice-03.html";
								}
							}
							else
							{
								htmltext = "TeleportDevice-04.html";
							}
						}
						else
						{
							htmltext = "TeleportDevice-04.html";
						}
					}
					break;
				}
				case "teleportMe":
				{
					if (npc.isScriptValue(1))
					{
						player.teleToLocation(BATTLEGROUND_LOC);
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if (npc.getId() == TELEPORT_DEVICE)
			{
				return npc.isScriptValue(0) ? "TeleportDevice-01.html" : "TeleportDevice-02.html";
			}
		}
		return super.onFirstTalk(npc, player);
	}
	
	private void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && creature.isPlayer())
		{
			switch (npc.getId())
			{
				case INVISIBLE_NPC:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						getTimers().addTimer("OPENING_MOVIE", 60000, npc, null);
					}
					break;
				}
				case TELEPORT_DEVICE:
				{
					npc.broadcastPacket(new OnEventTrigger(15235001, npc.isScriptValue(1)));
					break;
				}
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case FINARIA:
				{
					showOnScreenMsg(instance, NpcStringId.IF_WE_ALL_FALL_HERE_OUR_PLAN_WILL_CERTAINLY_FAIL_PLEASE_PROTECT_MY_FRIENDS, ExShowScreenMessage.TOP_CENTER, 16000);
					break;
				}
				case TAUTI_AXE:
				{
					npc.disableCoreAI(true);
					break;
				}
				case ZAHAK:
				case KUNDA_LORD:
				case KUNDA_1:
				case KUNDA_2:
				case KUNDA_3:
				{
					npc.setRandomWalking(false);
					break;
				}
				case INVISIBLE_NPC:
				{
					npc.initSeenCreatures();
					break;
				}
				case TELEPORT_DEVICE:
				{
					npc.initSeenCreatures(1000);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (npc.getId())
			{
				case ZAHAK:
				{
					if (getRandom(10000) < 10)
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.KILL_THAT_S1_OVER_THERE_FIRST_HE_IS_TOO_UGLY, attacker.getName());
					}
					// TODO: handle heal
					break;
				}
				case TAUTI:
				{
					int tautiStage = npcVars.getInt("TAUTI_STAGE", 0);
					
					if (tautiStage == 0)
					{
						// TODO: start skill timer
						tautiStage = npcVars.increaseInt("TAUTI_STAGE", 1);
					}
					
					if (npcVars.getInt("i_ai0", 0) == 0)
					{
						if ((npc.getCurrentHpPercent() <= 85) && (tautiStage == 1))
						{
							tautiStage = npcVars.increaseInt("TAUTI_STAGE", 1);
							// TODO: myself->AddUseSkillDesire(myself->sm, i_tauti_shout_scene, @ATTACK, @MOVE_TO_TARGET, 100000000000000);
							// TODO: myself->AddTimerEx(i_select_throw_up_timer, 2000);
							// TODO: myself->AddTimerEx(i_select_earthquake_timer, i_select_earthquake_time + (gg->Rand(11) * 1000));
						}
						else if ((npc.getCurrentHpPercent() <= 65) && (tautiStage == 2))
						{
							tautiStage = npcVars.increaseInt("TAUTI_STAGE", 1);
							// TODO: myself->AddUseSkillDesire(myself->sm, i_tauti_shout_scene, @ATTACK, @MOVE_TO_TARGET, 100000000000000);
						}
						else if ((npc.getCurrentHpPercent() <= 40) && (tautiStage == 3))
						{
							tautiStage = npcVars.increaseInt("TAUTI_STAGE", 1);
							// TODO: myself->AddUseSkillDesire(myself->sm, i_tauti_shout_scene, @ATTACK, @MOVE_TO_TARGET, 100000000000000);
							// TODO: myself->AddTimerEx(i_select_zahaq_timer, 5000);
						}
						else if ((npc.getCurrentHpPercent() <= 10) && (tautiStage == 4))
						{
							tautiStage = npcVars.increaseInt("TAUTI_STAGE", 1);
							npc.deleteMe();
							playMovie(instance, Movie.SC_TAUTI_PHASE);
							getTimers().addTimer("TAUTI_AXE_SPAWN", 24000, npc, null);
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case ZAHAK:
				{
					npc.dropItem(killer, TELEPORT_KEY, 1);
					break;
				}
				case TAUTI_AXE:
				{
					playMovie(instance, Movie.SC_TAUTI_ENDING);
					instance.finishInstance();
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@GameScript
	public static void main()
	{
		new TautiWarzone();
	}
}