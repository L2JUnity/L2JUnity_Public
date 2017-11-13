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
package org.l2junity.scripts.instances.LabyrinthOfBelis;

import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.zone.ZoneType;
import org.l2junity.gameserver.model.zone.type.EffectZone;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.instances.AbstractInstance;
import org.l2junity.scripts.quests.Q10331_StartOfFate.Q10331_StartOfFate;

/**
 * Labyrinth of Belis Instance Zone.
 * @author Gladicek
 */
public final class LabyrinthOfBelis extends AbstractInstance
{
	// NPC's
	private static final int SEBION = 32972;
	private static final int INFILTRATION_OFFICER = 19155;
	private static final int BELIS_VERITIFICATION_SYSTEM = 33215;
	private static final int OPERATIVE = 22998;
	private static final int HANDYMAN = 22997;
	private static final int ELECTRICITY_GENERATOR = 33216;
	private static final int NEMERTESS = 22984;
	// Items
	private static final int SARIL_NECKLACE = 17580;
	private static final int BELIS_MARK = 17615;
	// Skills
	private static final SkillHolder CURRENT_SHOCK = new SkillHolder(14698, 1);
	// Locations
	private static final Location INFILTRATION_OFFICER_ROOM_2 = new Location(-117044, 212520, -8592);
	private static final Location INFILTRATION_OFFICER_ROOM_3 = new Location(-117869, 214231, -8592);
	private static final Location INFILTRATION_OFFICER_ROOM_4 = new Location(-119162, 213713, -8592);
	private static final Location SPAWN_ATTACKERS = new Location(-116809, 213275, -8606);
	private static final Location GENERATOR_SPAWN = new Location(-118253, 214706, -8584);
	private static final Location NEMERTESS_SPAWN = new Location(-118336, 212973, -8680);
	// Misc
	private static final int TEMPLATE_ID = 178;
	private static final int DOOR_ID_ROOM_1_2 = 16240002;
	private static final int DOOR_ID_ROOM_2_1 = 16240003;
	private static final int DOOR_ID_ROOM_2_2 = 16240004;
	private static final int DOOR_ID_ROOM_3_1 = 16240005;
	private static final int DOOR_ID_ROOM_3_2 = 16240006;
	private static final int DOOR_ID_ROOM_4_1 = 16240007;
	private static final int DOOR_ID_ROOM_4_2 = 16240008;
	private static final int DAMAGE_ZONE = 12014;
	
	public LabyrinthOfBelis()
	{
		super(TEMPLATE_ID);
		addStartNpc(SEBION, INFILTRATION_OFFICER, BELIS_VERITIFICATION_SYSTEM);
		addFirstTalkId(INFILTRATION_OFFICER, ELECTRICITY_GENERATOR, BELIS_VERITIFICATION_SYSTEM);
		addTalkId(SEBION, INFILTRATION_OFFICER, BELIS_VERITIFICATION_SYSTEM);
		addSpawnId(INFILTRATION_OFFICER);
		addAttackId(INFILTRATION_OFFICER);
		addMoveFinishedId(INFILTRATION_OFFICER);
		addKillId(OPERATIVE, HANDYMAN, INFILTRATION_OFFICER, NEMERTESS);
		setCreatureDamageReceivedId(this::onCreatureDamageReceived, OPERATIVE, HANDYMAN);
		setCreatureKillId(this::onCreatureKill, INFILTRATION_OFFICER, OPERATIVE, HANDYMAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enter_instance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (isInInstance(world))
			{
				switch (event)
				{
					case "room1":
					{
						if (world.isStatus(0))
						{
							world.setStatus(1);
							world.openCloseDoor(DOOR_ID_ROOM_1_2, true);
							world.spawnGroup("operatives");
							npc.setScriptValue(1);
							npc.getAI().startFollow(player);
						}
						break;
					}
					case "room2":
					{
						if (world.isStatus(3))
						{
							world.setStatus(4);
							world.openCloseDoor(DOOR_ID_ROOM_2_2, true);
							npc.setScriptValue(1);
							npc.getAI().startFollow(player);
							getTimers().addRepeatingTimer("MESSAGE", 10000, npc, player);
						}
						break;
					}
					case "room3":
					{
						if (world.isStatus(5))
						{
							world.setStatus(6);
							final ZoneType zone = ZoneManager.getInstance().getZoneById(DAMAGE_ZONE, EffectZone.class);
							if (zone != null)
							{
								zone.setEnabled(true, world.getId());
							}
							world.openCloseDoor(DOOR_ID_ROOM_3_2, true);
							final Npc generator = addSpawn(ELECTRICITY_GENERATOR, GENERATOR_SPAWN, false, 0, true, world.getId());
							npc.setScriptValue(1);
							npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_COME_BACK_HERE);
							addAttackDesire(npc, generator);
							getTimers().addRepeatingTimer("MESSAGE", 7000, npc, null);
							getTimers().addRepeatingTimer("EFFECT", 500, generator, player);
							getTimers().addRepeatingTimer("ATTACKERS", 12500, npc, player);
						}
						break;
					}
					case "room4":
					{
						if (world.isStatus(7))
						{
							world.setStatus(8);
							world.openCloseDoor(DOOR_ID_ROOM_4_2, true);
							npc.setScriptValue(1);
							playMovie(player, Movie.SC_TALKING_ISLAND_BOSS_OPENING);
							getTimers().addTimer("SPAWN_NEMERTESS", 50000, npc, null);
						}
						break;
					}
					case "giveBelisMark":
					{
						if (world.isStatus(4))
						{
							if (hasAtLeastOneQuestItem(player, BELIS_MARK))
							{
								takeItems(player, BELIS_MARK, 1);
								
								switch (npc.getScriptValue())
								{
									case 0:
									{
										npc.setScriptValue(1);
										return "33215-01.html";
									}
									case 1:
									{
										npc.setScriptValue(2);
										return "33215-02.html";
									}
									case 2:
									{
										world.setStatus(5);
										getTimers().addTimer("ROOM_2_DONE", 500, npc, null);
										return "33215-03.html";
									}
								}
							}
							return "33215-04.html";
						}
						return "33215-05.html";
					}
					case "finish":
					{
						world.finishInstance(0);
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		
		if (isInInstance(world))
		{
			switch (world.getStatus())
			{
				case 3:
				{
					npc.setScriptValue(0);
					npc.setHeading(npc.getHeading() + 32500);
					npc.broadcastInfo();
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HEY_YOU_RE_NOT_ALL_BAD_LET_ME_KNOW_WHEN_YOU_RE_READY);
					break;
				}
				case 5:
				{
					npc.setScriptValue(0);
					npc.setHeading(npc.getHeading() + 32500);
					npc.broadcastInfo();
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.READY_LET_ME_KNOW);
					break;
				}
				case 7:
				{
					npc.setScriptValue(0);
					npc.setHeading(npc.getHeading() + 32500);
					npc.broadcastInfo();
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SOMETHING_OMINOUS_IN_THERE_I_HOPE_YOU_RE_REALLY_READY_FOR_THIS_LET_ME_KNOW);
					break;
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		String htmltext = null;
		
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case INFILTRATION_OFFICER:
				{
					if (npc.isScriptValue(0))
					{
						switch (world.getStatus())
						{
							case 0:
								htmltext = "19155-01.html";
								break;
							case 3:
								htmltext = "19155-03.html";
								break;
							case 5:
								htmltext = "19155-04.html";
								break;
							case 7:
								htmltext = "19155-05.html";
								break;
							case 9:
								htmltext = "19155-06.html";
								break;
						}
					}
					else
					{
						htmltext = "19155-02.html";
						break;
					}
					break;
				}
				case BELIS_VERITIFICATION_SYSTEM:
					htmltext = "33215.html";
					break;
				case ELECTRICITY_GENERATOR:
					htmltext = "33216.html";
					break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case OPERATIVE:
				{
					if (world.isStatus(1))
					{
						if (world.getAliveNpcs(OPERATIVE).isEmpty())
						{
							world.setStatus(2);
							getTimers().addTimer("ROOM_1_DONE", 500, npc, null);
						}
					}
					else if (world.isStatus(6) && npc.isScriptValue(1))
					{
						final int counter = world.getParameters().getInt("counter", 0);
						if (counter == 6)
						{
							getTimers().addTimer("ROOM_3_DONE", 2000, npc, player);
						}
					}
					break;
				}
				case HANDYMAN:
				{
					if (world.isStatus(4))
					{
						if (getRandom(100) > 60)
						{
							npc.dropItem(player, BELIS_MARK, 1);
						}
					}
					else if (world.isStatus(6) && npc.isScriptValue(1))
					{
						final int counter = world.getParameters().getInt("counter", 0);
						if (counter == 6)
						{
							getTimers().addTimer("ROOM_3_DONE", 2000, npc, player);
						}
					}
					break;
				}
				case NEMERTESS:
				{
					final QuestState qs = player.getQuestState(Q10331_StartOfFate.class.getSimpleName());
					if (qs.isCond(1))
					{
						qs.setCond(2, true);
						giveItems(player, SARIL_NECKLACE, 1);
					}
					npc.deleteMe();
					playMovie(player, Movie.SC_TALKING_ISLAND_BOSS_ENDING);
					getTimers().addTimer("ROOM_4_DONE", 30000, npc, null);
					break;
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		
		if (isInInstance(world))
		{
			final Npc officer = world.getNpc(INFILTRATION_OFFICER);
			
			switch (event)
			{
				case "MESSAGE":
				{
					switch (world.getStatus())
					{
						case 0:
							npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.LET_ME_KNOW_WHEN_YOU_RE_ALL_READY);
							break;
						case 4:
							showOnScreenMsg(player, NpcStringId.MARK_OF_BELIS_CAN_BE_ACQUIRED_FROM_ENEMIES_NUSE_THEM_IN_THE_BELIS_VERIFICATION_SYSTEM, ExShowScreenMessage.TOP_CENTER, 4500);
							break;
						case 6:
							npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_COME_BACK_HERE);
							break;
						default:
							getTimers().cancelTimer("MESSAGE", npc, null);
							break;
					}
					break;
				}
				case "ATTACKERS":
				{
					if (world.isStatus(6))
					{
						final int counter = world.getParameters().getInt("counter", 0) + 1;
						if (counter == 6)
						{
							getTimers().cancelTimer("ATTACKERS", npc, player);
						}
						world.setParameter("counter", counter);
						
						showOnScreenMsg(player, (getRandomBoolean() ? NpcStringId.IF_TERAIN_DIES_THE_MISSION_WILL_FAIL : NpcStringId.BEHIND_YOU_THE_ENEMY_IS_AMBUSHING_YOU), ExShowScreenMessage.TOP_CENTER, 4500);
						final Attackable mob = (Attackable) addSpawn((getRandomBoolean() ? OPERATIVE : HANDYMAN), SPAWN_ATTACKERS, false, 0, true, world.getId());
						mob.broadcastSay(ChatType.NPC_GENERAL, (getRandomBoolean() ? NpcStringId.KILL_THE_GUY_MESSING_WITH_THE_ELECTRIC_DEVICE : NpcStringId.FOCUS_ON_ATTACKING_THE_GUY_IN_THE_ROOM));
						mob.addDamageHate(officer, 0, 9999);
						addAttackDesire(mob, officer);
						mob.setScriptValue(1);
					}
					else
					{
						getTimers().cancelTimer("ATTACKERS", npc, player);
					}
					break;
				}
				case "ROOM_1_DONE":
				{
					if (world.isStatus(2))
					{
						world.setStatus(3);
						world.openCloseDoor(DOOR_ID_ROOM_2_1, true);
						officer.setIsRunning(true);
						officer.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, INFILTRATION_OFFICER_ROOM_2);
						officer.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ALL_RIGHT_LET_S_MOVE_OUT);
					}
					break;
				}
				case "ROOM_2_DONE":
				{
					world.openCloseDoor(DOOR_ID_ROOM_3_1, true);
					officer.setIsRunning(true);
					officer.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, INFILTRATION_OFFICER_ROOM_3);
					officer.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.COME_ON_ONTO_THE_NEXT_PLACE);
					break;
				}
				case "ROOM_3_DONE":
				{
					if (world.isStatus(6))
					{
						world.setStatus(7);
						final ZoneType zone = ZoneManager.getInstance().getZoneById(DAMAGE_ZONE, EffectZone.class);
						if (zone != null)
						{
							zone.setEnabled(false, world.getId());
						}
						world.openCloseDoor(DOOR_ID_ROOM_4_1, true);
						showOnScreenMsg(player, NpcStringId.ELECTRONIC_DEVICE_HAS_BEEN_DESTROYED, ExShowScreenMessage.TOP_CENTER, 4500);
						final Npc generator = world.getNpc(ELECTRICITY_GENERATOR);
						generator.deleteMe();
						officer.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, INFILTRATION_OFFICER_ROOM_4);
						officer.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DEVICE_DESTROYED_LET_S_GO_ONTO_THE_NEXT);
					}
					break;
				}
				case "ROOM_4_DONE":
				{
					if (world.isStatus(8))
					{
						world.setStatus(9);
						officer.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, NEMERTESS_SPAWN);
						officer.setScriptValue(0);
					}
					break;
				}
				case "SPAWN_NEMERTESS":
				{
					addSpawn(NEMERTESS, NEMERTESS_SPAWN, false, 0, false, world.getId());
					break;
				}
				case "DEBUFF":
				{
					npc.doInstantCast(player, CURRENT_SHOCK);
					break;
				}
				case "OFFICER_FOLLOW":
				{
					if ((world.isStatus(1)) || (world.isStatus(4)))
					{
						officer.setIsRunning(true);
						officer.getAI().startFollow(player);
						break;
					}
				}
				case "EFFECT":
				{
					npc.setState(1);
					break;
				}
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Attackable officer = (Attackable) npc;
		officer.setIsRunning(true);
		officer.setCanReturnToSpawnPoint(false);
		getTimers().addRepeatingTimer("MESSAGE", 15000, npc, null);
		return super.onSpawn(npc);
	}
	
	public void onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance world = npc.getInstanceWorld();
		final Npc officer = world.getNpc(INFILTRATION_OFFICER);
		
		if ((isInInstance(world) && npc.isNpc() && event.getAttacker().isPlayer()))
		{
			if ((world.isStatus(1)) || (world.isStatus(4)))
			{
				addAttackDesire(officer, npc);
			}
		}
	}
	
	public void onCreatureKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance world = npc.getInstanceWorld();
		final PlayerInstance player = world.getFirstPlayer();
		
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case OPERATIVE:
				case HANDYMAN:
				{
					if ((world.isStatus(1)) || (world.isStatus(4)))
					{
						getTimers().addTimer("OFFICER_FOLLOW", 1000, npc, player);
					}
					break;
				}
				case INFILTRATION_OFFICER:
				{
					world.finishInstance(0);
					break;
				}
			}
		}
	}
	
	@GameScript
	public static void main()
	{
		new LabyrinthOfBelis();
	}
}