/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.scripts.instances.FinalEmperialTomb;

import org.l2junity.commons.util.ArrayUtil;
import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.instancemanager.ZoneManager;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Attackable;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.zone.type.EffectZone;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.instances.AbstractInstance;

/**
 * @author Sdw
 */
public class FinalEmperialTomb extends AbstractInstance
{
	// NPCs
	private static final int IMPERIAL_TOMB_GUIDE = 32011;
	private static final int HALL_ALARM = 18328;
	private static final int HALL_KEEPER_CAPTAIN = 18329;
	private static final int HALL_KEEPER_WIZARD = 18330;
	private static final int HALL_KEEPER_GUARD = 18331;
	private static final int HALL_KEEPER_SUICIDAL_SOLDIER = 18333;
	private static final int DARK_CHOIR_CAPTAIN = 18334;
	private static final int DARK_CHOIR_PRIMA_DONNA = 18335;
	private static final int DARK_CHOIR_LANCER = 18336;
	private static final int DARK_CHOIR_ARCHER = 18337;
	private static final int DARK_CHOIR_SHAMAN = 18338;
	private static final int DARK_CHOIR_PLAYER = 18339;
	private static final int FRINTENZA_SEEKER = 29059;
	private static final int[] HALL_A_NPCS =
	{
		HALL_KEEPER_CAPTAIN,
		HALL_KEEPER_WIZARD,
		HALL_KEEPER_GUARD,
		HALL_KEEPER_SUICIDAL_SOLDIER
	};
	private static final int[] HALL_B_NPCS =
	{
		DARK_CHOIR_CAPTAIN,
		DARK_CHOIR_PRIMA_DONNA,
		DARK_CHOIR_LANCER,
		DARK_CHOIR_ARCHER,
		DARK_CHOIR_SHAMAN,
		DARK_CHOIR_PLAYER
	};
	// Items
	private static final int SOUL_BREAKING_ARROW = 8192; // Soul Breaking Arrow
	private static final int DEWDROP_OF_DESTRUCTION = 8556; // Dewdrop of Destruction
	// Locations
	private static final Location HALL_A_ROOM_CENTER = new Location(-87869, -141239, -9170);
	private static final Location HALL_B_ROOM_CENTER = new Location(-87952, -147079, -9188);
	// Doors
	private static final int[] HALL_A_START_DOORS =
	{
		17130051,
		17130052,
		17130053,
		17130054,
		17130055,
		17130056,
		17130057,
		17130058
	};
	private static final int[] HALL_A_END_DOORS =
	{
		17130042,
		17130043,
		17130045
	};
	private static final int[] HALL_B_PLAYER_ATTACKED_DOORS_1 =
	{
		17130042,
		17130043,
		17130045,
		17130046
	};
	private static final int[] HALL_B_PLAYER_ATTACKED_DOORS_2 =
	{
		17130061,
		17130062,
		17130063,
		17130064,
		17130065,
		17130066,
		17130067,
		17130068,
		17130069,
		17130070
	};
	private static final int[] HALL_B_END_DOORS =
	{
		17130045,
		17130046
	};
	// Misc
	private static final String[] HALL_A_GROUPS =
	{
		"godard32_1713_011m1",
		"godard32_1713_012m1",
		"godard32_1713_013m1",
		"godard32_1713_014m1",
		"godard32_1713_015m1",
		"godard32_1713_016m1",
		"godard32_1713_017m1",
		"godard32_1713_018m1",
	
	};
	private static final String[] HALL_B_GROUPS =
	{
		"godard32_1713_023m1",
		"godard32_1713_024m1",
		"godard32_1713_025m1",
		"godard32_1713_026m1",
		"godard32_1713_027m1",
		"godard32_1713_028m1",
		"godard32_1713_029m1"
	
	};
	// private final static String FRINTEZZA_ROOM_GROUP = "godard32_1713_102m1";
	private final static String[] DAMAGE_ZONE =
	{
		"25_15_frintessaE_02",
		"25_15_frintessaE_03",
		"25_15_frintessaE_04"
	};
	private static final int TEMPLATE_ID = 136;
	
	public FinalEmperialTomb()
	{
		super(TEMPLATE_ID);
		addStartNpc(IMPERIAL_TOMB_GUIDE);
		addTalkId(IMPERIAL_TOMB_GUIDE);
		addSpawnId(HALL_ALARM, DARK_CHOIR_CAPTAIN, FRINTENZA_SEEKER);
		addSpawnId(HALL_A_NPCS);
		addSpawnId(HALL_B_NPCS);
		addKillId(HALL_ALARM, HALL_KEEPER_CAPTAIN, HALL_KEEPER_GUARD, DARK_CHOIR_CAPTAIN, DARK_CHOIR_PLAYER);
		addAttackId(HALL_A_NPCS);
		addAttackId(HALL_B_NPCS);
		addSkillSeeId(HALL_A_NPCS);
		addSkillSeeId(HALL_B_NPCS);
		addFactionCallId(HALL_A_NPCS);
		addFactionCallId(HALL_B_NPCS);
		setCreatureSeeId(this::onCreatureSee, FRINTENZA_SEEKER);
		setCreatureSeeId(this::onCreatureSee, HALL_A_NPCS);
		setCreatureSeeId(this::onCreatureSee, HALL_B_NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
			
			final ItemInstance dewdropOfDestruction = player.getInventory().getItemByItemId(DEWDROP_OF_DESTRUCTION);
			if (dewdropOfDestruction != null)
			{
				player.getInventory().destroyItem("Quest", dewdropOfDestruction, player, player);
			}
			
			final ItemInstance soulBreakingArrow = player.getInventory().getItemByItemId(SOUL_BREAKING_ARROW);
			if (soulBreakingArrow != null)
			{
				player.getInventory().destroyItem("Quest", soulBreakingArrow, player, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case HALL_ALARM:
				{
					npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.INTRUDERS_SOUND_THE_ALARM);
					break;
				}
				case DARK_CHOIR_CAPTAIN:
				{
					npc.disableCoreAI(true);
					npc.setRandomWalking(false);
					for (String zoneName : DAMAGE_ZONE)
					{
						final EffectZone zone = ZoneManager.getInstance().getZoneByName(zoneName, EffectZone.class);
						if (zone != null)
						{
							zone.setEnabled(true, instance.getId());
						}
					}
					break;
				}
				case FRINTENZA_SEEKER:
				{
					npc.initSeenCreatures();
					break;
				}
			}
			if (ArrayUtil.contains(HALL_A_NPCS, npc.getId()))
			{
				npc.initSeenCreatures();
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
			switch (npc.getId())
			{
				case DARK_CHOIR_SHAMAN:
				case HALL_KEEPER_WIZARD:
				{
					final Skill attackSkill = npc.getParameters().getSkillHolder(npc.distance2d(attacker) > 100 ? "W_LongRangeDDMagic" : "W_ShortRangeDDMagic").getSkill();
					if ((attackSkill != null) && !npc.isSkillDisabled(attackSkill))
					{
						addSkillCastDesire(npc, attacker, attackSkill, 1000000);
						addAttackDesire(npc, attacker);
					}
					else
					{
						addAttackDesire(npc, attacker);
					}
					break;
				}
				case HALL_KEEPER_CAPTAIN:
				case HALL_KEEPER_GUARD:
				case DARK_CHOIR_LANCER:
				case DARK_CHOIR_PRIMA_DONNA:
				{
					final Skill attackSkill = npc.getParameters().getSkillHolder("PhysicalSpecial").getSkill();
					if ((attackSkill != null) && !npc.isSkillDisabled(attackSkill))
					{
						addSkillCastDesire(npc, attacker, attackSkill, 1000000);
						addAttackDesire(npc, attacker);
					}
					else
					{
						addAttackDesire(npc, attacker);
					}
					break;
				}
				case HALL_KEEPER_SUICIDAL_SOLDIER:
				{
					if (npc.distance2d(attacker) < 200)
					{
						final Skill suicideSkill = npc.getParameters().getSkillHolder("SelfRangeDDMagic").getSkill();
						if ((suicideSkill != null) && !npc.isSkillDisabled(suicideSkill))
						{
							addSkillCastDesire(npc, attacker, suicideSkill, 1000000);
							addAttackDesire(npc, attacker);
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onFactionCall(Npc npc, Npc caller, PlayerInstance attacker, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case DARK_CHOIR_SHAMAN:
				case HALL_KEEPER_WIZARD:
				{
					final Skill attackSkill = npc.getParameters().getSkillHolder(npc.distance2d(attacker) > 100 ? "W_LongRangeDDMagic" : "W_ShortRangeDDMagic").getSkill();
					if ((attackSkill != null) && !npc.isSkillDisabled(attackSkill))
					{
						addSkillCastDesire(npc, attacker, attackSkill, 1000000);
						addAttackDesire(npc, attacker);
					}
					else
					{
						addAttackDesire(npc, attacker);
					}
					break;
				}
				case HALL_KEEPER_CAPTAIN:
				case HALL_KEEPER_GUARD:
				case DARK_CHOIR_LANCER:
				case DARK_CHOIR_PRIMA_DONNA:
				{
					addAttackDesire(npc, attacker);
					break;
				}
			}
		}
		return super.onFactionCall(npc, caller, attacker, isSummon);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case DARK_CHOIR_SHAMAN:
				case HALL_KEEPER_WIZARD:
				{
					if ((Rnd.get(100) < 50) && caster.isInCategory(CategoryType.CLERIC_GROUP))
					{
						final Skill attackSkill = npc.getParameters().getSkillHolder(npc.distance2d(caster) > 100 ? "W_LongRangeDDMagic" : "W_ShortRangeDDMagic").getSkill();
						if ((attackSkill != null) && !npc.isSkillDisabled(attackSkill))
						{
							addSkillCastDesire(npc, caster, attackSkill, 1000000);
							addAttackDesire(npc, caster);
						}
						else
						{
							addAttackDesire(npc, caster);
						}
					}
					break;
				}
				case HALL_KEEPER_CAPTAIN:
				case HALL_KEEPER_GUARD:
				case DARK_CHOIR_LANCER:
				case DARK_CHOIR_ARCHER:
				case DARK_CHOIR_PRIMA_DONNA:
				{
					if ((Rnd.get(100) < 20) && caster.isInCategory(CategoryType.CLERIC_GROUP))
					{
						addAttackDesire(npc, caster);
					}
					break;
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	private void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Attackable npc = event.getSeer().asAttackable();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && creature.isPlayable())
		{
			switch (npc.getId())
			{
				case DARK_CHOIR_SHAMAN:
				case HALL_KEEPER_WIZARD:
				{
					final Skill skill = npc.getParameters().getSkillHolder(npc.distance2d(creature) > 100 ? "W_LongRangeDDMagic" : "W_ShortRangeDDMagic").getSkill();
					if ((skill != null) && !npc.isSkillDisabled(skill))
					{
						addSkillCastDesire(npc, creature, skill, 1000000);
						addAttackDesire(npc, creature);
					}
					else
					{
						addAttackDesire(npc, creature);
					}
					if (creature.isInCategory(CategoryType.SUMMON_NPC_GROUP))
					{
						npc.addDamageHate(creature, 0, 200);
					}
					break;
				}
				case HALL_KEEPER_CAPTAIN:
				case HALL_KEEPER_GUARD:
				case DARK_CHOIR_LANCER:
				case DARK_CHOIR_PRIMA_DONNA:
				{
					final Skill selfBuff = npc.getParameters().getSkillHolder("SelfBuff").getSkill();
					if ((selfBuff != null) && !creature.isInCategory(CategoryType.SUMMON_NPC_GROUP) && !npc.isSkillDisabled(selfBuff))
					{
						addSkillCastDesire(npc, npc, selfBuff, 1000000);
						addAttackDesire(npc, creature);
					}
					else if (npc.isInMyTerritory())
					{
						addAttackDesire(npc, creature);
					}
					break;
				}
				case HALL_KEEPER_SUICIDAL_SOLDIER:
				{
					if (npc.isInMyTerritory() && !creature.isInCategory(CategoryType.SUMMON_NPC_GROUP))
					{
						addAttackDesire(npc, creature);
					}
					break;
				}
				case DARK_CHOIR_ARCHER:
				{
					final Skill skill = npc.getParameters().getSkillHolder("PhysicalSpecial").getSkill();
					if ((skill != null) && !npc.isSkillDisabled(skill))
					{
						addSkillCastDesire(npc, creature, skill, 1000000);
						addAttackDesire(npc, creature);
					}
					else
					{
						addAttackDesire(npc, creature);
					}
					break;
				}
				case FRINTENZA_SEEKER:
				{
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case HALL_ALARM:
				{
					for (int doorId : HALL_A_START_DOORS)
					{
						instance.openCloseDoor(doorId, true);
					}
					npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.ALARM_SYSTEM_DESTROYED_INTRUDER_EXCLUDED);
					for (String group : HALL_A_GROUPS)
					{
						instance.getNpcsOfGroup(group).forEach(mob ->
						{
							mob.setIsRunning(true);
							mob.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, HALL_A_ROOM_CENTER);
						});
					}
					instance.setStatus(1);
					break;
				}
				case HALL_KEEPER_CAPTAIN:
				case HALL_KEEPER_GUARD:
				case DARK_CHOIR_LANCER:
				{
					if (Rnd.get(100) < 5)
					{
						npc.dropItem(killer, DEWDROP_OF_DESTRUCTION, 1);
					}
					break;
				}
				case DARK_CHOIR_PLAYER:
				{
					if (instance.getAliveNpcs(npc.getId()).isEmpty())
					{
						for (int doorId : HALL_B_PLAYER_ATTACKED_DOORS_1)
						{
							instance.openCloseDoor(doorId, false);
						}
						for (int doorId : HALL_B_PLAYER_ATTACKED_DOORS_2)
						{
							instance.openCloseDoor(doorId, true);
						}
						
						for (String group : HALL_B_GROUPS)
						{
							instance.getNpcsOfGroup(group).forEach(mob ->
							{
								mob.setIsRunning(true);
								mob.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, HALL_B_ROOM_CENTER);
							});
						}
					}
					break;
				}
				case DARK_CHOIR_CAPTAIN:
				{
					if (instance.getAliveNpcs(npc.getId()).isEmpty())
					{
						for (int doorId : HALL_B_END_DOORS)
						{
							instance.openCloseDoor(doorId, true);
						}
					}
					if (Rnd.get(100) < 50)
					{
						npc.dropItem(killer, SOUL_BREAKING_ARROW, 1);
					}
					break;
				}
			}
			
			switch (instance.getStatus())
			{
				case 1:
				{
					boolean hallAEmpty = true;
					for (String group : HALL_A_GROUPS)
					{
						hallAEmpty &= instance.getNpcsOfGroup(group, mob -> !mob.isDead()).isEmpty();
					}
					if (hallAEmpty)
					{
						for (int doorId : HALL_A_END_DOORS)
						{
							instance.openCloseDoor(doorId, true);
						}
						instance.setStatus(2);
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
		new FinalEmperialTomb();
	}
}
