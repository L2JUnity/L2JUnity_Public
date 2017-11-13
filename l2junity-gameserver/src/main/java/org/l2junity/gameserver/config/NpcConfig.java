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
package org.l2junity.gameserver.config;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "NPC")
public final class NpcConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(NpcConfig.class);
	
	@ConfigField(name = "AnnounceMammonSpawn", value = "false", comment =
	{
		"Global announcements will be made indicating Blacksmith/Merchant of Mammon",
		"Spawning points."
	})
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	
	@ConfigField(name = "AltMobAgroInPeaceZone", value = "true", comment =
	{
		"True - Mobs can be aggressive while in peace zones.",
		"False - Mobs can NOT be aggressive while in peace zones."
	})
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	
	@ConfigField(name = "AltAttackableNpcs", value = "true", comment =
	{
		"Defines whether NPCs are attackable by default",
		"Retail: True"
	})
	public static boolean ALT_ATTACKABLE_NPCS;
	
	@ConfigField(name = "AltGameViewNpc", value = "false", comment =
	{
		"Allows non-GM players to view NPC stats via shift-click"
	})
	public static boolean ALT_GAME_VIEWNPC;
	
	@ConfigField(name = "MaxDriftRange", value = "300", comment =
	{
		"Maximum distance mobs can randomly go from spawn point.",
		"DEFAULT NEEDS TO BE VERIFIED, MUST BE CHANGED HERE AND IN CONFIG.JAVA IF NOT CORRECT"
	})
	public static int MAX_DRIFT_RANGE;
	
	@ConfigField(name = "ShowNpcLevel", value = "false")
	public static boolean SHOW_NPC_LVL;
	
	@ConfigField(name = "EnableRandomEnchantEffect", value = "false", comment =
	{
		"Custom random EnchantEffect",
		"All npcs with weapons get random weapon enchanted value",
		"Enchantment is only visual, range is 4-21"
	})
	public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
	
	@ConfigField(name = "MinNPCLevelForDmgPenalty", value = "78", comment =
	{
		"The minimum NPC level for the Gracia Epilogue rule:",
		"'The amount of damage inflicted on monsters will be lower if your character is 2 or more levels below that of the level 78+ monster.'",
		"Notes:",
		"	If you want to disable this feature then set it 99"
	})
	public static int MIN_NPC_LVL_DMG_PENALTY;
	
	@ConfigField(name = "DmgPenaltyForLvLDifferences", value = "0.8, 0.6, 0.5, 0.42, 0.36, 0.32, 0.28, 0.25", comment =
	{
		"The penalty in percent for -2 till -9 level differences"
	})
	public static String _NPC_DMG_PENALTY;
	public static Map<Integer, Float> NPC_DMG_PENALTY;
	
	@ConfigField(name = "CritDmgPenaltyForLvLDifferences", value = "0.8, 0.6, 0.5, 0.42, 0.36, 0.32, 0.28, 0.25")
	public static String _NPC_CRIT_DMG_PENALTY;
	public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
	
	@ConfigField(name = "SkillDmgPenaltyForLvLDifferences", value = "0.8, 0.6, 0.5, 0.42, 0.36, 0.32, 0.28, 0.25")
	public static String _NPC_SKILL_DMG_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
	
	@ConfigField(name = "MinNPCLevelForMagicPenalty", value = "78", comment =
	{
		"The minimum NPC level for the Gracia Epilogue rule:",
		"'When a character's level is 3 or more levels lower than that of a monsters level the chance that the monster will be able to resist a magic spell will increase.'",
		"Notes:",
		"	If you want to disable this feature then set it 99"
	})
	public static int MIN_NPC_LVL_MAGIC_PENALTY;
	
	@ConfigField(name = "SkillChancePenaltyForLvLDifferences", value = "2.5, 3.0, 3.25, 3.5", comment =
	{
		"The penalty in percent for -3 till -6 level differences"
	})
	public static String _NPC_SKILL_CHANCE_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
	
	@ConfigField(name = "DecayTimeTask", value = "5000", comment =
	{
		"Decay Time Task (don't set it too low!) (in milliseconds):"
	})
	public static int DECAY_TIME_TASK;
	
	@ConfigField(name = "DefaultCorpseTime", value = "7", comment =
	{
		"This is the default corpse time (in seconds)."
	})
	public static int DEFAULT_CORPSE_TIME;
	
	@ConfigField(name = "SpoiledCorpseExtendTime", value = "10", comment =
	{
		"This is the time that will be added to spoiled corpse time (in seconds)."
	})
	public static int SPOILED_CORPSE_EXTEND_TIME;
	
	@ConfigField(name = "CorpseConsumeSkillAllowedTimeBeforeDecay", value = "2000", comment =
	{
		"The time allowed to use a corpse consume skill before the corpse decays."
	})
	public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
	
	@ConfigField(name = "GuardAttackAggroMob", value = "false", comment =
	{
		"True - Allows guards to attack aggressive mobs within range."
	})
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	
	@ConfigField(name = "AllowWyvernUpgrader", value = "false", comment =
	{
		"This option enables or disables the Wyvern manager located in every castle",
		"to train Wyverns and Striders from Hatchlings."
	})
	public static boolean ALLOW_WYVERN_UPGRADER;
	
	@ConfigField(name = "RaidHpRegenMultiplier", value = "1.0", comment =
	{
		"Multiplier of HP and MP regeneration for raid bosses.",
		"Example: Setting HP to 0.1 will cause raid boss HP to regenerate 90% slower than normal."
	})
	public static double RAID_HP_REGEN_MULTIPLIER;
	
	@ConfigField(name = "RaidMpRegenMultiplier", value = "1.0")
	public static double RAID_MP_REGEN_MULTIPLIER;
	
	@ConfigField(name = "RaidPDefenceMultiplier", value = "1.0", comment =
	{
		"Multiplier of physical and magical defense for raid bosses.",
		"Example: A setting of 0.1 will cause defense to be 90% lower than normal,",
		"while 1.1 will cause defense to be 10% higher than normal."
	})
	public static double RAID_PDEFENCE_MULTIPLIER;
	
	@ConfigField(name = "RaidMDefenceMultiplier", value = "1.0")
	public static double RAID_MDEFENCE_MULTIPLIER;
	
	@ConfigField(name = "RaidPAttackMultiplier", value = "1.0", comment =
	{
		"Multiplier of physical and magical attack for raid bosses.",
		"Example: A setting of 0.1 will cause attack to be 90% lower than normal,",
		"while 1.1 will cause attack to be 10% higher than normal."
	})
	public static double RAID_PATTACK_MULTIPLIER;
	
	@ConfigField(name = "RaidMAttackMultiplier", value = "1.0")
	public static double RAID_MATTACK_MULTIPLIER;
	
	@ConfigField(name = "RaidMinionRespawnTime", value = "300000", comment =
	{
		"Configure the interval at which raid boss minions will re-spawn.",
		"This time is in milliseconds, 1 minute is 60000 milliseconds."
	})
	public static double RAID_MINION_RESPAWN_TIMER;
	
	@ConfigField(name = "CustomMinionsRespawnTime", value = "22450,30;22371,120;22543,0;25545,0;22424,30;22425,30;22426,30;22427,30;22428,30;22429,30;22430,30;22432,30;22433,30;22434,30;22435,30;22436,30;22437,30;22438,30;25596,30;25605,0;25606,0;25607,0;25608,0", comment =
	{
		"Let's make handling of minions with non-standard static respawn easier - no additional code, just config.",
		"Format: minionId1,timeInSec1;minionId2,timeInSec2"
	})
	public static String _MINIONS_RESPAWN_TIME;
	public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
	
	@ConfigField(name = "RaidMinRespawnMultiplier", value = "1.0", comment =
	{
		"Configure Minimum and Maximum time multiplier between raid boss re-spawn.",
		"By default 12Hours*1.0 for Minimum Time and 24Hours*1.0 for Maximum Time.",
		"Example: Setting RaidMaxRespawnMultiplier to 2 will make the time between",
		"re-spawn 24 hours to 48 hours."
	})
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	
	@ConfigField(name = "RaidMaxRespawnMultiplier", value = "1.0")
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	
	@ConfigField(name = "DisableRaidCurse", value = "false", comment =
	{
		"Disable Raid Curse if raid more than 8 levels lower.",
		"Caution: drop will be reduced or even absent if DeepBlue drop rules enabled."
	})
	public static boolean RAID_DISABLE_CURSE;
	
	@ConfigField(name = "RaidChaosTime", value = "10", comment =
	{
		"Configure the interval at which raid bosses and minions wont reconsider their target",
		"This time is in seconds, 1 minute is 60 seconds."
	})
	public static int RAID_CHAOS_TIME;
	
	@ConfigField(name = "GrandChaosTime", value = "10")
	public static int GRAND_CHAOS_TIME;
	
	@ConfigField(name = "MinionChaosTime", value = "10")
	public static int MINION_CHAOS_TIME;
	
	@ConfigField(name = "MaximumSlotsForPet", value = "12", comment =
	{
		"This will control the inventory space limit for pets (NOT WEIGHT LIMIT)."
	})
	public static int INVENTORY_MAXIMUM_PET;
	
	@ConfigField(name = "PetHpRegenMultiplier", value = "1.0", comment =
	{
		"HP/MP Regen Multiplier for Pets"
	})
	public static double PET_HP_REGEN_MULTIPLIER;
	
	@ConfigField(name = "PetMpRegenMultiplier", value = "1.0")
	public static double PET_MP_REGEN_MULTIPLIER;
	
	@ConfigField(name = "DropAdenaMinLevelDifference", value = "8", comment =
	{
		"The min and max level difference used for level gap calculation",
		"this is only for how many levels higher the player is than the monster"
	})
	public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
	
	@ConfigField(name = "DropAdenaMaxLevelDifference", value = "15")
	public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
	
	@ConfigField(name = "DropAdenaMinLevelGapChance", value = "10", comment =
	{
		"This is the minimum level gap chance meaning for 10 that the monster will have 10% chance",
		"to allow dropping the item if level difference is bigger than DropAdenaMaxLevelDifference",
		"Note: This value is scalling from 100 to the specified value for DropAdenaMinLevelDifference to DropAdenaMaxLevelDifference limits"
	})
	public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
	
	@ConfigField(name = "DropItemMinLevelDifference", value = "5", comment =
	{
		"The min and max level difference used for level gap calculation",
		"this is only for how many levels higher the player is than the monster"
	})
	public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
	
	@ConfigField(name = "DropItemMaxLevelDifference", value = "10")
	public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
	
	@ConfigField(name = "DropItemMinLevelGapChance", value = "10", comment =
	{
		"This is the minimum level gap chance meaning for 10 that the monster will have 10% chance",
		"to allow dropping the item if level difference is bigger than DropAdenaMaxLevelDifference",
		"Note: This value is scalling from 100 to the specified value for DropAdenaMinLevelDifference to DropAdenaMaxLevelDifference limits"
	})
	public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
	
	@ConfigField(name = "VitalityConsumeByMob", value = "2250", comment =
	{
		"---------------------------------------------------------------------------",
		"Vitality",
		"---------------------------------------------------------------------------",
		"Vitality decrease multiplier for mob after awakening"
	})
	public static int VITALITY_CONSUME_BY_MOB;
	
	@ConfigField(name = "VitalityConsumeByBoss", value = "1125", comment =
	{
		"Vitality increase multiplier for raid and world boss after awakening"
	})
	public static int VITALITY_CONSUME_BY_BOSS;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		NPC_DMG_PENALTY = parseConfigLine(_NPC_DMG_PENALTY);
		NPC_CRIT_DMG_PENALTY = parseConfigLine(_NPC_CRIT_DMG_PENALTY);
		NPC_SKILL_DMG_PENALTY = parseConfigLine(_NPC_SKILL_DMG_PENALTY);
		NPC_SKILL_CHANCE_PENALTY = parseConfigLine(_NPC_SKILL_CHANCE_PENALTY);
		
		final String[] propertySplit = _MINIONS_RESPAWN_TIME.split(";");
		MINIONS_RESPAWN_TIME = new HashMap<>(propertySplit.length);
		for (final String property : propertySplit)
		{
			final String[] propSplit = property.split(",");
			if (propSplit.length != 2)
			{
				LOGGER.warn("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime {}", property);
			}
			
			try
			{
				MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
			}
			catch (final NumberFormatException nfe)
			{
				if (!property.isEmpty())
				{
					LOGGER.warn("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime {}", property);
				}
			}
		}
	}
	
	/**
	 * @param line the string line to parse
	 * @return a parsed float map
	 */
	private static Map<Integer, Float> parseConfigLine(String line)
	{
		String[] propertySplit = line.split(",");
		Map<Integer, Float> ret = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			ret.put(i++, Float.parseFloat(value));
		}
		return ret;
	}
}
