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
@ConfigClass(fileName = "Rates")
public final class RatesConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(RatesConfig.class);
	
	@ConfigField(name = "RateXp", value = "1", comment =
	{
		"Experience multiplier"
	})
	public static float RATE_XP;
	
	@ConfigField(name = "RateSp", value = "1", comment =
	{
		"Skill points multiplier"
	})
	public static float RATE_SP;
	
	@ConfigField(name = "RatePartyXp", value = "1", comment =
	{
		"Experience multiplier (Party)"
	})
	public static float RATE_PARTY_XP;
	
	@ConfigField(name = "RatePartySp", value = "1", comment =
	{
		"Skill points multiplier (Party)"
	})
	public static float RATE_PARTY_SP;
	
	@ConfigField(name = "RateInstanceXp", value = "-1", comment =
	{
		"Instance rates",
		"Those rates are used as absolute rate within instances, does not applies on top of RateXp for example!",
		"Instance Experience multiplier"
	})
	public static float RATE_INSTANCE_XP;
	
	@ConfigField(name = "RateInstanceSp", value = "-1", comment =
	{
		"Instance Skill points multiplier"
	})
	public static float RATE_INSTANCE_SP;
	
	@ConfigField(name = "RateInstancePartyXp", value = "-1", comment =
	{
		"Instance Experience multiplier (Party)"
	})
	public static float RATE_INSTANCE_PARTY_XP;
	
	@ConfigField(name = "RateInstancePartySp", value = "-1", comment =
	{
		"Instance Skill Point multiplier (Party)"
	})
	public static float RATE_INSTANCE_PARTY_SP;
	
	@ConfigField(name = "RateRaidbossPointsReward", value = "1", comment =
	{
		"Raidboss points multipler"
	})
	public static float RATE_RAIDBOSS_POINTS;
	
	@ConfigField(name = "RateExtractable", value = "1.", comment =
	{
		"Modify the rate of reward of all extractable items and skills."
	})
	public static float RATE_EXTRACTABLE;
	
	@ConfigField(name = "RateDropManor", value = "1")
	public static int RATE_DROP_MANOR;
	
	@ConfigField(name = "RateQuestDrop", value = "1", comment =
	{
		"Quest item drop multiplier"
	})
	public static float RATE_QUEST_DROP;
	
	@ConfigField(name = "RateQuestReward", value = "1", comment =
	{
		"Default reward multiplier",
		"When UseRewardMultipliers=False - default multiplier is used for any reward",
		"When UseRewardMultipliers=True  - default multiplier is used for all items not affected by additional multipliers"
	})
	public static float RATE_QUEST_REWARD;
	
	@ConfigField(name = "RateQuestRewardXP", value = "1", comment =
	{
		"Exp/SP reward multipliers"
	})
	public static float RATE_QUEST_REWARD_XP;
	
	@ConfigField(name = "RateQuestRewardSP", value = "1")
	public static float RATE_QUEST_REWARD_SP;
	
	@ConfigField(name = "RateQuestRewardAdena", value = "1", comment =
	{
		"Adena reward multiplier"
	})
	public static float RATE_QUEST_REWARD_ADENA;
	
	@ConfigField(name = "UseQuestRewardMultipliers", value = "false", comment =
	{
		"Use additional item multipliers?"
	})
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	
	@ConfigField(name = "RateQuestRewardPotion", value = "1", comment =
	{
		"Additional quest-reward multipliers based on item type"
	})
	public static float RATE_QUEST_REWARD_POTION;
	
	@ConfigField(name = "RateQuestRewardScroll", value = "1")
	public static float RATE_QUEST_REWARD_SCROLL;
	
	@ConfigField(name = "RateQuestRewardRecipe", value = "1")
	public static float RATE_QUEST_REWARD_RECIPE;
	
	@ConfigField(name = "RateQuestRewardMaterial", value = "1")
	public static float RATE_QUEST_REWARD_MATERIAL;
	
	@ConfigField(name = "BaseLuckDropChanceMultiplier", value = "0.025", comment =
	{
		"BASE chance multiplier for luck drop (fortune pocket), this base rate is applied to items chance"
	})
	public static float BASE_LUCK_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "DeathDropAmountMultiplier", value = "1", comment =
	{
		"Multiplies the amount of items dropped from monster on ground when it dies."
	})
	public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
	
	@ConfigField(name = "CorpseDropAmountMultiplier", value = "1", comment =
	{
		"Multiplies the amount of items looted from monster when a skill like Sweeper(Spoil) is used."
	})
	public static float RATE_CORPSE_DROP_AMOUNT_MULTIPLIER;
	
	@ConfigField(name = "HerbDropAmountMultiplier", value = "1", comment =
	{
		"Multiplies the amount of items dropped from monster on ground when it dies."
	})
	public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
	
	@ConfigField(name = "LuckDropAmountMultiplier", value = "1", comment =
	{
		"Multiplies the amount of items dropped from monster when lucky."
	})
	public static float RATE_LUCK_DROP_AMOUNT_MULTIPLIER;
	
	@ConfigField(name = "DeathDropChanceMultiplier", value = "1", comment =
	{
		"Multiplies the chance of items that can be dropped from monster on ground when it dies."
	})
	public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "CorpseDropChanceMultiplier", value = "1", comment =
	{
		"Multiplies the chance of items that can be looted from monster when a skill like Sweeper(Spoil) is used."
	})
	public static float RATE_CORPSE_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "HerbDropChanceMultiplier", value = "1", comment =
	{
		"Multiplies the chance of items that can be dropped from monster on ground when it dies."
	})
	public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "LuckDropChanceMultiplier", value = "1", comment =
	{
		"Multiplies the chance of items that can be dropped from monster when lucky."
	})
	public static float RATE_LUCK_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "DropAmountMultiplierByItemId", value = "57,1", comment =
	{
		"List of items affected by custom drop rate by id, used now for Adena rate too.",
		"Usage: itemId1,multiplier1;itemId2,multiplier2;...",
		"Note: Make sure the lists do NOT CONTAIN trailing spaces or spaces between the numbers!",
		"Example for Raid boss 1x jewelry: 6656,1;6657,1;6658,1;6659,1;6660,1;6661,1;6662,1;8191,1;10170,1;10314,1;"
	})
	public static String _RATE_DROP_AMOUNT_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_AMOUNT_MULTIPLIER;
	
	@ConfigField(name = "DropChanceMultiplierByItemId", value = "57,1", comment =
	{
		"List of items affected by custom drop rate by id, used now for Adena rate too.",
		"Usage: itemId1,multiplier1;itemId2,multiplier2;...",
		"Note: Make sure the lists do NOT CONTAIN trailing spaces or spaces between the numbers!",
		"Example for Raid boss 1x jewelry: 6656,1;6657,1;6658,1;6659,1;6660,1;6661,1;6662,1;8191,1;10170,1;10314,1;"
	})
	public static String _RATE_DROP_CHANCE_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_CHANCE_MULTIPLIER;
	
	@ConfigField(name = "RateKarmaLost", value = "-1", comment =
	{
		"Karma decreasing rate",
		"Note: -1 means RateXp so it means it will use retail rate for decreasing karma upon death or receiving exp by farming mobs."
	})
	public static float RATE_KARMA_LOST;
	
	@ConfigField(name = "RateKarmaExpLost", value = "1")
	public static float RATE_KARMA_EXP_LOST;
	
	@ConfigField(name = "RateSiegeGuardsPrice", value = "1")
	public static float RATE_SIEGE_GUARDS_PRICE;
	
	@ConfigField(name = "PlayerDropLimit", value = "0")
	public static int PLAYER_DROP_LIMIT;
	
	@ConfigField(name = "PlayerRateDrop", value = "0", comment =
	{
		"in %"
	})
	public static int PLAYER_RATE_DROP;
	
	@ConfigField(name = "PlayerRateDropItem", value = "0", comment =
	{
		"in %"
	})
	public static int PLAYER_RATE_DROP_ITEM;
	
	@ConfigField(name = "PlayerRateDropEquip", value = "0", comment =
	{
		"in %"
	})
	public static int PLAYER_RATE_DROP_EQUIP;
	
	@ConfigField(name = "PlayerRateDropEquipWeapon", value = "0", comment =
	{
		"in %"
	})
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	@ConfigField(name = "PetXpRate", value = "1")
	public static float PET_XP_RATE;
	
	@ConfigField(name = "PetFoodRate", value = "1")
	public static int PET_FOOD_RATE;
	
	@ConfigField(name = "SinEaterXpRate", value = "1")
	public static float SINEATER_XP_RATE;
	
	@ConfigField(name = "KarmaDropLimit", value = "10")
	public static int KARMA_DROP_LIMIT;
	
	@ConfigField(name = "KarmaRateDrop", value = "40")
	public static int KARMA_RATE_DROP;
	
	@ConfigField(name = "KarmaRateDropItem", value = "50")
	public static int KARMA_RATE_DROP_ITEM;
	
	@ConfigField(name = "KarmaRateDropEquip", value = "40")
	public static int KARMA_RATE_DROP_EQUIP;
	
	@ConfigField(name = "KarmaRateDropEquipWeapon", value = "10")
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	@ConfigField(name = "RateVitalityExpMultiplier", value = "2.", comment =
	{
		"The following configures the XP multiplier of each vitality level. Basically, you have",
		"Take care setting these values according to your server rates, as the can lead to huge differences!",
		"Example with a server rate 15x and vitality = 2. => final server rate = 30 (15x2)!"
	})
	public static float RATE_VITALITY_EXP_MULTIPLIER;
	
	@ConfigField(name = "VitalityMaxItemsAllowed", value = "999", comment =
	{
		"Maximum vitality items allowed to be used for a week by a player."
	})
	public static int VITALITY_MAX_ITEMS_ALLOWED;
	
	@ConfigField(name = "RateVitalityLost", value = "1.", comment =
	{
		"This option manages the multiplier of vitality amount that is lost when a player kills mobs or raids.",
		"Default value is 1."
	})
	public static float RATE_VITALITY_LOST;
	
	@ConfigField(name = "RateVitalityGain", value = "0.1", comment =
	{
		"This option manages the multiplier of vitality amount that is gained instead of lost when a player kills mobs or raids during vitality recovery effect.",
		"Default value is 0.1 (10% of original vitality to be lost, independant of the above config)"
	})
	public static float RATE_VITALITY_GAIN;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		if (RATE_INSTANCE_XP < 0)
		{
			RATE_INSTANCE_XP = RATE_XP;
		}
		if (RATE_INSTANCE_SP < 0)
		{
			RATE_INSTANCE_SP = RATE_SP;
		}
		if (RATE_INSTANCE_PARTY_XP < 0)
		{
			RATE_INSTANCE_PARTY_XP = RATE_PARTY_XP;
		}
		if (RATE_INSTANCE_PARTY_SP < 0)
		{
			RATE_INSTANCE_PARTY_SP = RATE_PARTY_SP;
		}
		
		if (RATE_KARMA_LOST == -1)
		{
			RATE_KARMA_LOST = RATE_XP;
		}
		
		String[] dropAmountMultiplier = _RATE_DROP_AMOUNT_MULTIPLIER.split(";");
		RATE_DROP_AMOUNT_MULTIPLIER = new HashMap<>(dropAmountMultiplier.length);
		if (!dropAmountMultiplier[0].isEmpty())
		{
			for (String item : dropAmountMultiplier)
			{
				String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOGGER.warn("Config.load(): invalid config property -> RateDropItemsById \"{}\"", item);
				}
				else
				{
					try
					{
						RATE_DROP_AMOUNT_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOGGER.warn("Config.load(): invalid config property -> RateDropItemsById \"{}\"", item);
						}
					}
				}
			}
		}
		
		String[] dropChanceMultiplier = _RATE_DROP_CHANCE_MULTIPLIER.split(";");
		RATE_DROP_CHANCE_MULTIPLIER = new HashMap<>(dropChanceMultiplier.length);
		if (!dropChanceMultiplier[0].isEmpty())
		{
			for (String item : dropChanceMultiplier)
			{
				String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOGGER.warn("Config.load(): invalid config property -> RateDropItemsById \"{}\"", item);
				}
				else
				{
					try
					{
						RATE_DROP_CHANCE_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOGGER.warn("Config.load(): invalid config property -> RateDropItemsById \"{}\"", item);
						}
					}
				}
			}
		}
	}
}
