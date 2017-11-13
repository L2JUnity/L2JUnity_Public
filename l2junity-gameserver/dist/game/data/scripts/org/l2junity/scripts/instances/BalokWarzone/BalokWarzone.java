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
package org.l2junity.scripts.instances.BalokWarzone;

import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.Playable;
import org.l2junity.gameserver.model.actor.instance.DoorInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.Earthquake;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.scripts.instances.AbstractInstance;

/**
 * Balok Warzone instance zone.
 * @author St3eT
 */
public final class BalokWarzone extends AbstractInstance
{
	// NPCs
	private static final int BALOK = 29218;
	private static final int ROOM_WARDER = 23123;
	private static final int HELL_GATE = 19040;
	private static final int HELL_GATE_MINION = 29219;
	private static final int ENTRANCE_PORTAL = 33523;
	// Skills
	private static final SkillHolder INVUL = new SkillHolder(14190, 1);
	private static final SkillHolder REAR_DESTROY = new SkillHolder(14576, 1);
	private static final SkillHolder BLACK_VORTEX = new SkillHolder(14247, 1);
	private static final SkillHolder BLACK_VORTEX_SWAMP = new SkillHolder(14366, 1);
	private static final SkillHolder LEAP_ATTACK = new SkillHolder(14248, 1);
	private static final SkillHolder EARTH_DEMOLITION = new SkillHolder(14246, 1);
	private static final SkillHolder INPRISON = new SkillHolder(5226, 1);
	private static final SkillHolder TOUCH_OF_DEATH = new SkillHolder(14250, 1);
	private static final SkillHolder DEATH_RAP = new SkillHolder(5226, 1);
	// Items
	private static final int PRISON_KEY = 10015; // Prison Gate Key
	// Locations
	private static final Location BATTLE_PORT = new Location(153567, 143319, -12736);
	private static final Location[] PRISON_LOCS =
	{
		new Location(153587, 140369, -12707),
		new Location(154432, 140594, -12707),
		new Location(155039, 141224, -12707),
		new Location(155282, 142075, -12707),
		new Location(154410, 143531, -12707),
		new Location(152741, 143524, -12707),
		new Location(151886, 142077, -12707),
		new Location(152127, 141226, -12707),
	};
	// Misc
	private static final int TEMPLATE_ID = 167;
	private static final int MAXIMAL_MINION_COUNT = 15;
	
	public BalokWarzone()
	{
		super(TEMPLATE_ID);
		addStartNpc(ENTRANCE_PORTAL);
		addTalkId(ENTRANCE_PORTAL);
		addInstanceCreatedId(TEMPLATE_ID);
		addInstanceEnterId(TEMPLATE_ID);
		addSpellFinishedId(BALOK);
		addAttackId(BALOK);
		addKillId(BALOK, HELL_GATE_MINION, ROOM_WARDER);
		setCreatureSeeId(this::onCreatureSee, HELL_GATE_MINION);
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
				case "GATE_LOOP_TIMER":
				{
					int minionCount = instance.getAliveNpcs(HELL_GATE_MINION).size();
					
					if (minionCount < MAXIMAL_MINION_COUNT)
					{
						addSpawn(HELL_GATE_MINION, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId()).initSeenCreatures();
						minionCount++;
					}
					
					getTimers().addTimer("GATE_LOOP_TIMER", 3000 + ((minionCount * minionCount) * 500), npc, null);
					break;
				}
				case "GATE_TIME_TIMER":
				{
					npc.deleteMe();
					instance.getAliveNpcs(HELL_GATE_MINION).forEach(Npc::deleteMe);
					break;
				}
				case "BALOK_UNBEATABLE":
				{
					addSkillCastDesire(npc, npc, INVUL, 23);
					break;
				}
			}
		}
		else if (event.equals("TELEPORT_PLAYER"))
		{
			player.teleToLocation(BATTLE_PORT);
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		getTimers().addTimer("BATTLE_PORT", 3000, e ->
		{
			instance.getPlayers().forEach(p -> p.teleToLocation(BATTLE_PORT));
			instance.getDoors().forEach(DoorInstance::closeMe);
			
			getTimers().addTimer("START_SCENE", 20000, e2 ->
			{
				playMovie(instance, Movie.SI_BARLOG_OPENING);
				getTimers().addTimer("SPAWN_BALOK", 19300, e3 ->
				{
					instance.spawnGroup("balrog_bossm1");
				});
			});
		});
	}
	
	@Override
	public void onInstanceEnter(PlayerInstance player, Instance instance)
	{
		if (!instance.getAliveNpcs(BALOK).isEmpty())
		{
			getTimers().addTimer("TELEPORT_PLAYER", 2000, null, player);
		}
		super.onInstanceEnter(player, instance);
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
				case HELL_GATE_MINION:
				{
					if (getRandomBoolean())
					{
						final double distance = npc.distance3d(creature);
						
						if (distance < 200)
						{
							addSkillCastDesire(npc, creature, DEATH_RAP, 23);
						}
						else if (distance < 600)
						{
							addSkillCastDesire(npc, creature, TOUCH_OF_DEATH, 23);
						}
					}
					addAttackPlayerDesire(npc, (Playable) creature, 23);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			if (npc.getId() == BALOK)
			{
				if ((npcVars.getInt("BALOK_GUARDS_STATUS", 0) == 0) && (npc.getCurrentHpPercent() < 85))
				{
					for (Npc warder : instance.spawnGroup("balrog_boss_guardm1")) // spawn Room Warder's
					{
						addSkillCastDesire(warder, warder, INVUL, 23);
						warder.getVariables().set("INVUL_ACTIVE", true);
					}
					
					npcVars.set("BALOK_STATUS", 2);
					npcVars.set("BALOK_GUARDS_STATUS", 1);
				}
				
				if (npcVars.getInt("BALOK_INVUL_STATUS", 0) == 0)
				{
					if (npc.getCurrentHpPercent() < 40)
					{
						// TODO: timers AttackTimer_3rd, AttackTimer_death_order
						getTimers().addTimer("BALOK_UNBEATABLE", 3000, npc, null);
						npcVars.set("BALOK_STATUS", 3);
						npcVars.set("BALOK_INVUL_STATUS", 1);
					}
				}
				else if (npc.isAffectedBySkill(INVUL.getSkillId()))
				{
					final double direction = npc.calculateDirectionTo(attacker);
					
					if ((getRandom(10) < 2) && (direction < 220) && (direction > 140))
					{
						addSkillCastDesire(npc, attacker, REAR_DESTROY.getSkill(), 23);
					}
					
					if (((damage > (300 + getRandom(1000))) && (npc.distance3d(attacker) < 300) && (direction < 210) && (direction > 150)))
					{
						npc.stopSkillEffects(INVUL.getSkill());
					}
					
				}
				
				final int random = getRandom(1000);
				switch (npcVars.getInt("BALOK_STATUS", 1))
				{
					case 1:
					{
						if (random < 40)
						{
							addSkillCastDesire(npc, attacker, BLACK_VORTEX.getSkill(), 23);
						}
						else if (random < 60)
						{
							addSkillCastDesire(npc, attacker, BLACK_VORTEX_SWAMP.getSkill(), 23);
						}
						else if (random < 80)
						{
							addSkillCastDesire(npc, attacker, LEAP_ATTACK.getSkill(), 23);
						}
						break;
					}
					case 2:
					case 3:
					{
						if (random < 20)
						{
							addSkillCastDesire(npc, attacker, BLACK_VORTEX.getSkill(), 23);
						}
						else if (random < 50)
						{
							addSkillCastDesire(npc, attacker, BLACK_VORTEX_SWAMP.getSkill(), 23);
						}
						else if (random < 60)
						{
							addSkillCastDesire(npc, attacker, LEAP_ATTACK.getSkill(), 23);
						}
						else if (random < 70) // Retail value is 100 but seems like they changed it since GD
						{
							if (instance.getAliveNpcs(HELL_GATE).stream().findFirst().orElse(null) == null)
							{
								addSkillCastDesire(npc, attacker, EARTH_DEMOLITION.getSkill(), 23);
							}
						}
						else if (random < 80) // Retail value is 120 but seems like they changed it since GD
						{
							if (npc.distance3d(attacker) < 600)
							{
								addSkillCastDesire(npc, attacker, INPRISON.getSkill(), 23);
							}
						}
						else if (random < 140)
						{
							instance.getAliveNpcs(HELL_GATE_MINION).forEach(minion ->
							{
								final PlayerInstance target = instance.getPlayersInsideRadius(minion, 500).stream().findAny().orElse(null);
								
								if ((target != null) && getRandomBoolean())
								{
									final double distance = npc.distance3d(target);
									
									if (distance < 200)
									{
										addSkillCastDesire(minion, target, DEATH_RAP, 23);
									}
									else if (distance < 600)
									{
										addSkillCastDesire(minion, target, TOUCH_OF_DEATH, 23);
									}
								}
								addAttackPlayerDesire(minion, target, 23);
							});
						}
						else if (random < 150)
						{
							addSkillCastDesire(npc, attacker, BLACK_VORTEX_SWAMP.getSkill(), 23);
						}
						break;
					}
				}
				
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if (npc.getId() == BALOK)
			{
				if ((skill.getId() == EARTH_DEMOLITION.getSkillId()) && instance.getAliveNpcs(HELL_GATE).isEmpty())
				{
					final double direction = npc.calculateDirectionTo(player);
					double addToX = 0;
					double addToY = 0;
					
					if (direction < 30)
					{
						addToX = 0.5;
						addToY = 0.866;
					}
					else if (direction < 60)
					{
						addToX = 0.866;
						addToY = 0.5;
					}
					else if (direction < 90)
					{
						addToX = 1;
						addToY = 0;
					}
					else if (direction < 120)
					{
						addToX = 0.866;
						addToY = -0.5;
					}
					else if (direction < 150)
					{
						addToX = 0.5;
						addToY = -0.866;
					}
					else if (direction < 180)
					{
						addToX = 0;
						addToY = -1;
					}
					else if (direction < 210)
					{
						addToX = -0.5;
						addToY = -0.866;
					}
					else if (direction < 240)
					{
						addToX = -0.866;
						addToY = -0.5;
					}
					else if (direction < 270)
					{
						addToX = -1;
						addToY = 0;
					}
					else if (direction < 300)
					{
						addToX = -0.866;
						addToY = 0.5;
					}
					else if (direction < 330)
					{
						addToX = -0.5;
						addToY = 0.866;
					}
					else if (direction < 360)
					{
						addToX = 0;
						addToY = 1;
					}
					
					addToX = 200 * addToX;
					addToY = 200 * addToY;
					final Npc gate = addSpawn(HELL_GATE, (int) (npc.getX() + addToX), (int) (npc.getY() + addToY), npc.getZ(), npc.getHeading() + 8192, false, 0, false, instance.getId());
					instance.broadcastPacket(new Earthquake(gate, 20, 10));
					
					for (int i = 0; i < 4; i++)
					{
						addSpawn(HELL_GATE_MINION, gate.getX(), gate.getY(), gate.getZ(), 0, false, 0, false, instance.getId()).initSeenCreatures();
					}
					getTimers().addTimer("GATE_LOOP_TIMER", 3000, gate, null);
					getTimers().addTimer("GATE_TIME_TIMER", 300000, gate, null);
				}
				else if ((skill.getId() == INPRISON.getSkillId()) && (player != null))
				{
					prisonPlayer(instance, player, getRandom(8));
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private void prisonPlayer(Instance instance, PlayerInstance player, int jailId)
	{
		showOnScreenMsg(instance, NpcStringId.S1_LOCKED_AWAY_IN_THE_PRISON, ExShowScreenMessage.TOP_CENTER, 4000, player.getName());
		player.teleToLocation(PRISON_LOCS[jailId]);
		
		final Npc doorWarder = World.getInstance().getVisibleObjects(player, Npc.class, 1000).stream().findFirst().orElse(null);
		if (doorWarder != null)
		{
			doorWarder.stopSkillEffects(INVUL.getSkill());
		}
		
		instance.getDoors().forEach(door ->
		{
			if (player.distance3d(door) < 1000)
			{
				door.closeMe();
			}
		});
		
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case BALOK:
				{
					instance.finishInstance();
					instance.getAliveNpcs(ROOM_WARDER, HELL_GATE, HELL_GATE_MINION).forEach(Npc::deleteMe);
					break;
				}
				case HELL_GATE_MINION:
				{
					if (instance.getAliveNpcs(HELL_GATE_MINION).size() <= 0)
					{
						instance.getAliveNpcs(HELL_GATE).forEach(Npc::deleteMe);
					}
					break;
				}
				case ROOM_WARDER:
				{
					if (!hasQuestItems(killer, PRISON_KEY) && (getRandom(100) < 80))
					{
						giveItems(killer, PRISON_KEY, 1);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@GameScript
	public static void main()
	{
		new BalokWarzone();
	}
}