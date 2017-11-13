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

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.lang.management.DeadlockDetector;
import org.l2junity.commons.util.PropertiesParser;
import org.l2junity.gameserver.enums.ChatType;
import org.l2junity.gameserver.enums.IllegalActionPunishmentType;
import org.l2junity.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "General")
public final class GeneralConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralConfig.class);
	
	@ConfigField(name = "DefaultAccessLevel", value = "0", comment =
	{
		"Default Access Level. For example if you set 7, everyone will have 7 access level."
	})
	public static int DEFAULT_ACCESS_LEVEL;
	
	@ConfigField(name = "ServerListBrackets", value = "false", comment =
	{
		"Setting for serverList",
		"Displays [] in front of server name on character selection"
	})
	public static boolean SERVER_LIST_BRACKET;
	
	@ConfigField(name = "ServerListType", value = "Normal", comment =
	{
		"Displays server type next to the server name on character selection.",
		"Notes:",
		"	Accepted Values: Normal, Relax, Test, Broad, Restricted, Event, Free, World, New, Classic"
	})
	public static String _SERVER_LIST_TYPE;
	public static int SERVER_LIST_TYPE;
	
	@ConfigField(name = "ServerListAge", value = "0", comment =
	{
		"Displays server minimum age to the server name on character selection.",
		"Notes:",
		"	Accepted values: 0, 15, 18"
	})
	public static int SERVER_LIST_AGE;
	
	@ConfigField(name = "ServerGMOnly", value = "false", comment =
	{
		"If True, only accounts with GM access can enter the server."
	})
	public static boolean SERVER_GMONLY;
	
	@ConfigField(name = "GameGuardEnforce", value = "false", comment =
	{
		"Enforce gameguard for clients. Sends a gameguard query on character login."
	})
	public static boolean GAMEGUARD_ENFORCE;
	
	@ConfigField(name = "GameGuardProhibitAction", value = "false", comment =
	{
		"Don't allow player to perform trade, talk with npc, or move until gameguard reply is received."
	})
	public static boolean GAMEGUARD_PROHIBITACTION;
	
	@ConfigField(name = "LogChat", value = "false", comment =
	{
		"Logging settings. The following four settings, while enabled, will increase writing to your hard drive(s) considerably. Depending on the size of your server, the amount of players, and other factors, you may suffer a noticable performance hit."
	})
	public static boolean LOG_CHAT;
	
	@ConfigField(name = "LogAutoAnnouncements", value = "false")
	public static boolean LOG_AUTO_ANNOUNCEMENTS;
	
	@ConfigField(name = "LogItems", value = "false")
	public static boolean LOG_ITEMS;
	
	@ConfigField(name = "LogItemsSmallLog", value = "false", comment =
	{
		"Log only Adena and equippable items if LogItems is enabled"
	})
	public static boolean LOG_ITEMS_SMALL_LOG;
	
	@ConfigField(name = "LogItemEnchants", value = "false")
	public static boolean LOG_ITEM_ENCHANTS;
	
	@ConfigField(name = "LogSkillEnchants", value = "false")
	public static boolean LOG_SKILL_ENCHANTS;
	
	@ConfigField(name = "GMAudit", value = "false")
	public static boolean GMAUDIT;
	
	@ConfigField(name = "SkillCheckEnable", value = "false", comment =
	{
		"Check players for non-allowed skills"
	})
	public static boolean SKILL_CHECK_ENABLE;
	
	@ConfigField(name = "SkillCheckRemove", value = "false", comment =
	{
		"If true, remove invalid skills from player and database.",
		"Report only, if false."
	})
	public static boolean SKILL_CHECK_REMOVE;
	
	@ConfigField(name = "SkillCheckGM", value = "true", comment =
	{
		"Check also GM characters (only if SkillCheckEnable = True)"
	})
	public static boolean SKILL_CHECK_GM;
	
	@ConfigField(name = "InstanceDebug", value = "false", comment =
	{
		"Instances debugging"
	})
	public static boolean DEBUG_INSTANCES;
	
	@ConfigField(name = "HtmlActionCacheDebug", value = "false", comment =
	{
		"Html action cache debugging"
	})
	public static boolean HTML_ACTION_CACHE_DEBUG;
	
	@ConfigField(name = "PacketHandlerDebug", value = "false", comment =
	{
		"Packet handler debug output"
	})
	public static boolean PACKET_HANDLER_DEBUG;
	
	@ConfigField(name = "Developer", value = "false")
	public static boolean DEVELOPER;
	
	@ConfigField(name = "AltDevNoSpawns", value = "false", comment =
	{
		"Don't load spawntable."
	})
	public static boolean ALT_DEV_NO_SPAWNS;
	
	@ConfigField(name = "AltDevShowQuestsLoadInLogs", value = "false", comment =
	{
		"Show quests while loading them."
	})
	public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
	
	@ConfigField(name = "AltDevShowScriptsLoadInLogs", value = "false", comment =
	{
		"Show scripts while loading them."
	})
	public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
	
	@ConfigField(name = "UrgentPacketThreadCoreSize", value = "2")
	public static int IO_PACKET_THREAD_CORE_SIZE;
	
	@ConfigField(name = "DeadLockDetector", value = "true", comment =
	{
		"Dead Lock Detector (a separate thread for detecting deadlocks).",
		"For improved crash logs and automatic restart in deadlock case if enabled.",
		"Check interval is in seconds."
	})
	public static boolean DEADLOCK_DETECTOR;
	
	@ConfigField(name = "DeadLockCheckInterval", value = "20")
	public static int DEADLOCK_CHECK_INTERVAL;
	
	@ConfigField(name = "RestartOnDeadlock", value = "false")
	public static boolean RESTART_ON_DEADLOCK;
	
	@ConfigField(name = "AllowDiscardItem", value = "true", comment =
	{
		"Items on ground management.",
		"Allow players to drop items on the ground."
	})
	public static boolean ALLOW_DISCARDITEM;
	
	@ConfigField(name = "AutoDestroyDroppedItemAfter", value = "600", comment =
	{
		"Delete dropped reward items from world after a specified amount of seconds. Disabled = 0."
	})
	public static int AUTODESTROY_ITEM_AFTER;
	
	@ConfigField(name = "AutoDestroyHerbTime", value = "60", comment =
	{
		"Time in seconds after which dropped herb will be auto-destroyed"
	})
	public static int HERB_AUTO_DESTROY_TIME;
	
	@ConfigField(name = "ListOfProtectedItems", value = "0", comment =
	{
		"List of item id that will not be destroyed (separated by ",
		" like 57,5575,6673).",
		"Notes:",
		"	Make sure the lists do NOT CONTAIN trailing spaces or spaces between the numbers!",
		"	Items on this list will be protected regardless of the following options."
	})
	public static List<Integer> LIST_PROTECTED_ITEMS;
	
	@ConfigField(name = "DatabaseCleanUp", value = "true", comment =
	{
		"Cleans up the server database on startup.",
		"The bigger the database is, the longer it will take to clean up the database(the slower the server will start).",
		"Sometimes this ends up with 0 elements cleaned up, and a lot of wasted time on the server startup.",
		"If you want a faster server startup, set this to 'false', but its recommended to clean up the database from time to time."
	})
	public static boolean DATABASE_CLEAN_UP;
	
	@ConfigField(name = "CharacterDataStoreInterval", value = "15", comment =
	{
		"This is the interval (in minutes), that the gameserver will update a players information such as location.",
		"The higher you set this number, there will be less character information saving so you will have less accessing of the database and your hard drive(s).",
		"The lower you set this number, there will be more frequent character information saving so you will have more access to the database and your hard drive(s).",
		"A value of 0 disables periodic saving.",
		"Independent of this setting the character is always saved after leaving the world."
	})
	public static int CHAR_DATA_STORE_INTERVAL;
	
	@ConfigField(name = "ClanVariablesStoreInterval", value = "15", comment =
	{
		"This is the interval (in minutes), that the game server will update a clan's variables information into the database.",
		"The higher you set this number, there will be less clan's variables information saving so you will have less accessing of the database and your hard drive(s).",
		"The lower you set this number, there will be more frequent clan's variables information saving so you will have more access to the database and your hard drive(s).",
		"A value of 0 disables periodic saving."
	})
	public static int CLAN_VARIABLES_STORE_INTERVAL;
	
	@ConfigField(name = "LazyItemsUpdate", value = "false", comment =
	{
		"This enables the server to only update items when saving the character.",
		"Enabling this greatly reduces DB usage and improves performance.",
		"WARNING: This option causes item loss during crashes."
	})
	public static boolean LAZY_ITEMS_UPDATE;
	
	@ConfigField(name = "UpdateItemsOnCharStore", value = "false", comment =
	{
		"When enabled, this forces (even if using lazy item updates) the items owned by the character to be updated into DB when saving its character."
	})
	public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
	
	@ConfigField(name = "DestroyPlayerDroppedItem", value = "false", comment =
	{
		"Also delete from world misc. items dropped by players (all except equip-able items).",
		"Notes:",
		"	Works only if AutoDestroyDroppedItemAfter is greater than 0."
	})
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	
	@ConfigField(name = "DestroyEquipableItem", value = "false", comment =
	{
		"Destroy dropped equippable items (armor, weapon, jewelry).",
		"Notes:",
		"	Works only if DestroyPlayerDroppedItem = True"
	})
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	
	@ConfigField(name = "SaveDroppedItem", value = "false", comment =
	{
		"Save dropped items into the database for restoring after restart."
	})
	public static boolean SAVE_DROPPED_ITEM;
	
	@ConfigField(name = "EmptyDroppedItemTableAfterLoad", value = "false", comment =
	{
		"Enable/Disable the emptying of the stored dropped items table after items are loaded into memory (safety setting).",
		"If the server crashed before saving items, on next start old items will be restored and players may already have picked up some of them so this will prevent duplicates."
	})
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	
	@ConfigField(name = "SaveDroppedItemInterval", value = "60", comment =
	{
		"Time interval in minutes to save in DB items on ground. Disabled = 0.",
		"Notes:",
		"	If SaveDroppedItemInterval is disabled, items will be saved into the database only at server shutdown."
	})
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	
	@ConfigField(name = "ClearDroppedItemTable", value = "false", comment =
	{
		"Delete all saved items from the database on next restart?",
		"Notes:",
		"	Works only if SaveDroppedItem = False."
	})
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	
	@ConfigField(name = "AutoDeleteInvalidQuestData", value = "false", comment =
	{
		"Delete invalid quest from players."
	})
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	
	// @ConfigField - not used anymore
	// public static boolean PRECISE_DROP_CALCULATION;
	
	@ConfigField(name = "MultipleItemDrop", value = "true", comment =
	{
		"Allow creating multiple non-stackable items at one time?"
	})
	public static boolean MULTIPLE_ITEM_DROP;
	
	@ConfigField(name = "ForceInventoryUpdate", value = "false", comment =
	{
		"Forces full item inventory packet to be sent for any item change.",
		"Notes:",
		"	This can increase network traffic"
	})
	public static boolean FORCE_INVENTORY_UPDATE;
	
	@ConfigField(name = "LazyCache", value = "true", comment =
	{
		"True = Load html's into cache only on first time html is requested.",
		"False = Load all html's into cache on server startup."
	})
	public static boolean LAZY_CACHE;
	
	@ConfigField(name = "CacheCharNames", value = "true", comment =
	{
		"Cache all character names in to memory on server startup",
		"False - names are loaded from Db when they are requested",
		"True - decrease Db usage , increase memory consumption"
	})
	public static boolean CACHE_CHAR_NAMES;
	
	@ConfigField(name = "MinNPCAnimation", value = "10", comment =
	{
		"Minimum and maximum variables in seconds for npc animation delay.",
		"You must keep MinNPCAnimation < = MaxNPCAnimation."
	})
	public static int MIN_NPC_ANIMATION;
	
	@ConfigField(name = "MaxNPCAnimation", value = "20")
	public static int MAX_NPC_ANIMATION;
	
	@ConfigField(name = "MinMonsterAnimation", value = "5")
	public static int MIN_MONSTER_ANIMATION;
	
	@ConfigField(name = "MaxMonsterAnimation", value = "20")
	public static int MAX_MONSTER_ANIMATION;
	
	@ConfigField(name = "EnableFallingDamage", value = "true", comment =
	{
		"Allow characters to receive damage from falling.",
		"CoordSynchronize = 2 is recommended."
	})
	public static boolean ENABLE_FALLING_DAMAGE;
	
	@ConfigField(name = "GridsAlwaysOn", value = "false", comment =
	{
		"Grid options: Grids can turn themselves on and off.  This also affects the loading and processing of all AI tasks and (in the future) geodata within this grid.",
		"Turn on for a grid with a person in it is immediate, but it then turns on the 8 neighboring grids based on the specified number of seconds.",
		"Turn off for a grid and neighbors occurs after the specified number of seconds have passed during which a grid has had no players in or in any of its neighbors.",
		"The always on option allows to ignore all this and let all grids be active at all times (not suggested)."
	})
	public static boolean GRIDS_ALWAYS_ON;
	
	@ConfigField(name = "GridNeighborTurnOnTime", value = "1")
	public static int GRID_NEIGHBOR_TURNON_TIME;
	
	@ConfigField(name = "GridNeighborTurnOffTime", value = "90")
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	
	@ConfigField(name = "PeaceZoneMode", value = "0", comment =
	{
		"Peace Zone Modes:",
		"0 = Peace All the Time",
		"1 = PVP During Siege for siege participants",
		"2 = PVP All the Time"
	})
	public static int PEACE_ZONE_MODE;
	
	@ConfigField(name = "GlobalChat", value = "ON", comment =
	{
		"Global Chat.",
		"Available Options: ON, OFF, GM, GLOBAL"
	})
	public static String DEFAULT_GLOBAL_CHAT;
	
	@ConfigField(name = "TradeChat", value = "ON", comment =
	{
		"Trade Chat.",
		"Available Options: ON, OFF, GM, GLOBAL"
	})
	public static String DEFAULT_TRADE_CHAT;
	
	@ConfigField(name = "MinimumChatLevel", value = "20", comment =
	{
		"Minimum level for chat, 0 = disable"
	})
	public static int MINIMUM_CHAT_LEVEL;
	
	@ConfigField(name = "MinimumTradeChatLevel", value = "84", comment =
	{
		"Minimum level for trade chat, 0 = disable"
	})
	public static int MINIMUM_TRADE_CHAT_LEVEL;
	
	@ConfigField(name = "AllowWarehouse", value = "true", comment =
	{
		"If you are experiencing problems with Warehouse transactions, feel free to disable them here."
	})
	public static boolean ALLOW_WAREHOUSE;
	
	@ConfigField(name = "WarehouseCache", value = "false", comment =
	{
		"Enable Warehouse Cache. If warehouse is not used will server clear memory used by this warehouse."
	})
	public static boolean WAREHOUSE_CACHE;
	
	@ConfigField(name = "WarehouseCacheTime", value = "15", comment =
	{
		"How long warehouse should be stored in memory."
	})
	public static int WAREHOUSE_CACHE_TIME;
	
	@ConfigField(name = "AllowRefund", value = "true")
	public static boolean ALLOW_REFUND;
	
	@ConfigField(name = "AllowMail", value = "true")
	public static boolean ALLOW_MAIL;
	
	@ConfigField(name = "AllowAttachments", value = "true")
	public static boolean ALLOW_ATTACHMENTS;
	
	@ConfigField(name = "AllowWear", value = "true", comment =
	{
		"If True player can try on weapon and armor in shop."
	})
	public static boolean ALLOW_WEAR;
	
	@ConfigField(name = "WearDelay", value = "5")
	public static int WEAR_DELAY;
	
	@ConfigField(name = "WearPrice", value = "10", comment =
	{
		"Adena cost to try on an item."
	})
	public static int WEAR_PRICE;
	
	@ConfigField(name = "DefaultFinishTime", value = "5", comment =
	{
		"When is instance finished, is set time to destruction currency instance.",
		"Time in minutes."
	})
	public static int INSTANCE_FINISH_TIME;
	
	@ConfigField(name = "RestorePlayerInstance", value = "false", comment =
	{
		"---------------------------------------------------------------------------",
		"Instances",
		"---------------------------------------------------------------------------",
		"Restores the player to their previous instance (ie. an instanced area/dungeon) on EnterWorld."
	})
	public static boolean RESTORE_PLAYER_INSTANCE;
	
	@ConfigField(name = "EjectDeadPlayerTime", value = "1", comment =
	{
		"When a player dies, is removed from instance after a fixed period of time.",
		"Time in minutes."
	})
	public static int EJECT_DEAD_PLAYER_TIME;
	
	@ConfigField(name = "AllowLottery", value = "true")
	public static boolean ALLOW_LOTTERY;
	
	@ConfigField(name = "AllowRace", value = "true")
	public static boolean ALLOW_RACE;
	
	@ConfigField(name = "AllowWater", value = "true")
	public static boolean ALLOW_WATER;
	
	@ConfigField(name = "AllowRentPet", value = "false", comment =
	{
		"Enable pets for rent (wyvern & strider) from pet managers."
	})
	public static boolean ALLOW_RENTPET;
	
	@ConfigField(name = "AllowFishing", value = "true")
	public static boolean ALLOWFISHING;
	
	@ConfigField(name = "AllowBoat", value = "true")
	public static boolean ALLOW_BOAT;
	
	@ConfigField(name = "BoatBroadcastRadius", value = "20000", comment =
	{
		"Boat broadcast radius.",
		"If players getting annoyed by boat shouts then radius can be decreased."
	})
	public static int BOAT_BROADCAST_RADIUS;
	
	@ConfigField(name = "AllowCursedWeapons", value = "true")
	public static boolean ALLOW_CURSED_WEAPONS;
	
	@ConfigField(name = "AllowManor", value = "true")
	public static boolean ALLOW_MANOR;
	
	@ConfigField(name = "ShowServerNews", value = "false", comment =
	{
		"Show 'data/html/servnews.htm' when a character enters world."
	})
	public static boolean SERVER_NEWS;
	
	@ConfigField(name = "EnableCommunityBoard", value = "true", comment =
	{
		"Enable the Community Board."
	})
	public static boolean ENABLE_COMMUNITY_BOARD;
	
	@ConfigField(name = "BBSDefault", value = "_bbshome", comment =
	{
		"Default Community Board page."
	})
	public static String BBS_DEFAULT;
	
	@ConfigField(name = "UseChatFilter", value = "false", comment =
	{
		"Enable chat filter"
	})
	public static boolean USE_SAY_FILTER;
	
	@ConfigField(name = "ChatFilterChars", value = "^_^", comment =
	{
		"Replace filter words with following chars"
	})
	public static String CHAT_FILTER_CHARS;
	
	@ConfigField(name = "BanChatChannels", value = "GENERAL,SHOUT,WORLD,TRADE,HERO_VOICE", comment =
	{
		"Banchat for channels, split \";\"",
		"GENERAL (white)",
		"SHOUT (!)",
		"TELL (\")",
		"PARTY (#)",
		"CLAN (@)",
		"GM (//gmchat)",
		"PETITION_PLAYER (*)",
		"PETITION_GM (*)",
		"TRADE (+)",
		"ALLIANCE ($)",
		"ANNOUNCEMENT",
		"BOAT",
		"FRIEND",
		"MSNCHAT",
		"PARTYMATCH_ROOM",
		"PARTYROOM_COMMANDER (Yellow)",
		"PARTYROOM_ALL (Red)",
		"HERO_VOICE (%)",
		"CRITICAL_ANNOUNCE",
		"SCREEN_ANNOUNCE",
		"BATTLEFIELD",
		"MPCC_ROOM",
		"NPC_GENERAL",
		"NPC_SHOUT",
		"NEW_TELL",
		"WORLD (&)",
	})
	public static Set<ChatType> BAN_CHAT_CHANNELS;
	
	@ConfigField(name = "WorldChatMinLevel", value = "94", comment =
	{
		"---------------------------------------------------------------------------",
		"World chat settings",
		"---------------------------------------------------------------------------",
		"The minimum level to use this chat"
	})
	public static int WORLD_CHAT_MIN_LEVEL;
	
	@ConfigField(name = "WorldChatPointsPerDay", value = "3", comment =
	{
		"The amount of points player will have at his disposal every day"
	})
	public static int WORLD_CHAT_POINTS_PER_DAY;
	
	@ConfigField(name = "WorldChatInterval", value = "20secs")
	public static Duration WORLD_CHAT_INTERVAL;
	
	@ConfigField(name = "AltManorRefreshTime", value = "20", comment =
	{
		"Manor refresh time in military hours."
	})
	public static int ALT_MANOR_REFRESH_TIME;
	
	@ConfigField(name = "AltManorRefreshMin", value = "00", comment =
	{
		"Manor refresh time (minutes)."
	})
	public static int ALT_MANOR_REFRESH_MIN;
	
	@ConfigField(name = "AltManorApproveTime", value = "4", comment =
	{
		"Manor period approve time in military hours."
	})
	public static int ALT_MANOR_APPROVE_TIME;
	
	@ConfigField(name = "AltManorApproveMin", value = "30", comment =
	{
		"Manor period approve time (minutes)."
	})
	public static int ALT_MANOR_APPROVE_MIN;
	
	@ConfigField(name = "AltManorMaintenanceMin", value = "6", comment =
	{
		"Manor maintenance time (minutes)."
	})
	public static int ALT_MANOR_MAINTENANCE_MIN;
	
	@ConfigField(name = "AltManorSaveAllActions", value = "false", comment =
	{
		"Manor Save Type.",
		"True = Save data into the database after every action"
	})
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	
	@ConfigField(name = "AltManorSavePeriodRate", value = "2", comment =
	{
		"Manor Save Period (used only if AltManorSaveAllActions = False)"
	})
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	
	@ConfigField(name = "AltLotteryPrize", value = "50000", comment =
	{
		"Initial Lottery prize."
	})
	public static long ALT_LOTTERY_PRIZE;
	
	@ConfigField(name = "AltLotteryTicketPrice", value = "2000", comment =
	{
		"Lottery Ticket Price"
	})
	public static long ALT_LOTTERY_TICKET_PRICE;
	
	@ConfigField(name = "AltLottery5NumberRate", value = "0.6", comment =
	{
		"What part of jackpot amount should receive characters who pick 5 wining numbers"
	})
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	
	@ConfigField(name = "AltLottery4NumberRate", value = "0.2", comment =
	{
		"What part of jackpot amount should receive characters who pick 4 wining numbers"
	})
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	
	@ConfigField(name = "AltLottery3NumberRate", value = "0.2", comment =
	{
		"What part of jackpot amount should receive characters who pick 3 wining numbers"
	})
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	
	@ConfigField(name = "AltLottery2and1NumberPrize", value = "200", comment =
	{
		"How much Adena receive characters who pick two or less of the winning number"
	})
	public static long ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	@ConfigField(name = "AltItemAuctionEnabled", value = "true", comment =
	{
		""
	})
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	
	@ConfigField(name = "AltItemAuctionExpiredAfter", value = "14", comment =
	{
		"Number of days before auction cleared from database with all bids."
	})
	public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
	
	@ConfigField(name = "AltItemAuctionTimeExtendsOnBid", value = "0", comment =
	{
		"Auction extends to specified amount of seconds if one or more new bids added.",
		"By default auction extends only two times, by 5 and 3 minutes, this custom value used after it.",
		"Values higher than 60s is not recommended."
	})
	public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
	
	@ConfigField(name = "TimeOfAttack", value = "50")
	public static int FS_TIME_ATTACK;
	
	@ConfigField(name = "TimeOfCoolDown", value = "5")
	public static int FS_TIME_COOLDOWN;
	
	@ConfigField(name = "TimeOfEntry", value = "3")
	public static int FS_TIME_ENTRY;
	
	@ConfigField(name = "TimeOfWarmUp", value = "2")
	public static int FS_TIME_WARMUP;
	
	@ConfigField(name = "NumberOfNecessaryPartyMembers", value = "4")
	public static int FS_PARTY_MEMBER_COUNT;
	
	@ConfigField(name = "RiftMinPartySize", value = "2", comment =
	{
		"Minimal party size to enter rift. Min = 2, Max = 9.",
		"If while inside the rift, the party becomes smaller, all members will be teleported back."
	})
	public static int RIFT_MIN_PARTY_SIZE;
	
	@ConfigField(name = "RiftSpawnDelay", value = "10000", comment =
	{
		"Time in ms the party has to wait until the mobs spawn when entering a room. C4 retail: 10s"
	})
	public static int RIFT_SPAWN_DELAY;
	
	@ConfigField(name = "MaxRiftJumps", value = "4", comment =
	{
		"Number of maximum jumps between rooms allowed, after this time party will be teleported back"
	})
	public static int RIFT_MAX_JUMPS;
	
	@ConfigField(name = "AutoJumpsDelayMin", value = "480", comment =
	{
		"Time between automatic jumps in seconds"
	})
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	
	@ConfigField(name = "AutoJumpsDelayMax", value = "600")
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	
	@ConfigField(name = "BossRoomTimeMultiply", value = "1.5", comment =
	{
		"Time Multiplier for stay in the boss room"
	})
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	@ConfigField(name = "RecruitCost", value = "18", comment =
	{
		"Cost in dimension fragments to enter the rift, each party member must own this amount"
	})
	public static int RIFT_ENTER_COST_RECRUIT;
	
	@ConfigField(name = "SoldierCost", value = "21")
	public static int RIFT_ENTER_COST_SOLDIER;
	
	@ConfigField(name = "OfficerCost", value = "24")
	public static int RIFT_ENTER_COST_OFFICER;
	
	@ConfigField(name = "CaptainCost", value = "27")
	public static int RIFT_ENTER_COST_CAPTAIN;
	
	@ConfigField(name = "CommanderCost", value = "30")
	public static int RIFT_ENTER_COST_COMMANDER;
	
	@ConfigField(name = "HeroCost", value = "33")
	public static int RIFT_ENTER_COST_HERO;
	
	@ConfigField(name = "DefaultPunish", value = "KICK", comment =
	{
		"Player punishment for illegal actions:",
		"BROADCAST - broadcast warning to gms only",
		"KICK - kick player(default)",
		"KICKBAN - kick & ban player",
		"JAIL - jail player (define minutes of jail with param: 0 = infinite)"
	})
	public static IllegalActionPunishmentType DEFAULT_PUNISH;
	
	@ConfigField(name = "DefaultPunishParam", value = "0", comment =
	{
		"This setting typically specifies the duration of the above punishment."
	})
	public static int DEFAULT_PUNISH_PARAM;
	
	@ConfigField(name = "OnlyGMItemsFree", value = "true", comment =
	{
		"Apply default punish if player buy items for zero Adena."
	})
	public static boolean ONLY_GM_ITEMS_FREE;
	
	@ConfigField(name = "JailIsPvp", value = "false", comment =
	{
		"Jail is a PvP zone."
	})
	public static boolean JAIL_IS_PVP;
	
	@ConfigField(name = "JailDisableChat", value = "true", comment =
	{
		"Disable all chat in jail (except normal one)"
	})
	public static boolean JAIL_DISABLE_CHAT;
	
	@ConfigField(name = "JailDisableTransaction", value = "false", comment =
	{
		"Disable all transaction in jail",
		"Trade/Store/Drop"
	})
	public static boolean JAIL_DISABLE_TRANSACTION;
	
	@ConfigField(name = "CustomSpawnlistTable", value = "false")
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	
	@ConfigField(name = "CustomNpcData", value = "false")
	public static boolean CUSTOM_NPC_DATA;
	
	@ConfigField(name = "CustomTeleportTable", value = "false")
	public static boolean CUSTOM_TELEPORT_TABLE;
	
	@ConfigField(name = "CustomNpcBufferTables", value = "false")
	public static boolean CUSTOM_NPCBUFFER_TABLES;
	
	@ConfigField(name = "CustomSkillsLoad", value = "false")
	public static boolean CUSTOM_SKILLS_LOAD;
	
	@ConfigField(name = "CustomItemsLoad", value = "false")
	public static boolean CUSTOM_ITEMS_LOAD;
	
	@ConfigField(name = "CustomMultisellLoad", value = "false")
	public static boolean CUSTOM_MULTISELL_LOAD;
	
	@ConfigField(name = "CustomBuyListLoad", value = "false")
	public static boolean CUSTOM_BUYLIST_LOAD;
	
	@ConfigField(name = "AltBirthdayGift", value = "22187", comment =
	{
		"Gift sent with Mail System"
	})
	public static int ALT_BIRTHDAY_GIFT;
	
	@ConfigField(name = "AltBirthdayMailSubject", value = "Happy Birthday!", comment =
	{
		"Mail Subject"
	})
	public static String ALT_BIRTHDAY_MAIL_SUBJECT;
	
	@ConfigField(name = "AltBirthdayMailText", value = "Hello Adventurer!! Seeing as you're one year older now, I thought I would send you some birthday cheer :) Please find your birthday pack attached. May these gifts bring you joy and happiness on this very special day.\\n\\nSincerely, Alegria", comment =
	{
		"Mail Content",
		"$c1: Player name",
		"$s1: Age"
	})
	public static String ALT_BIRTHDAY_MAIL_TEXT;
	
	@ConfigField(name = "EnableBlockCheckerEvent", value = "true", comment =
	{
		"Enable the Handy's Block Checker event"
	})
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	
	@ConfigField(name = "BlockCheckerMinTeamMembers", value = "2", comment =
	{
		"Minimum number of members on each team before",
		"be able to start the event",
		"Min: 1",
		"Max: 6",
		"Retail: 2"
	})
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	
	@ConfigField(name = "HBCEFairPlay", value = "true", comment =
	{
		"Fair play",
		"Players can choose what team to play. However, by",
		"enabling this property to true, the teams will be",
		"balanced in the teleport to the arena"
	})
	public static boolean HBCE_FAIR_PLAY;
	
	@ConfigField(name = "NormalEnchantCostMultipiler", value = "1", comment =
	{
		"Enchant Skill Details Settings"
	})
	public static int NORMAL_ENCHANT_COST_MULTIPLIER;
	
	@ConfigField(name = "SafeEnchantCostMultipiler", value = "5")
	public static int SAFE_ENCHANT_COST_MULTIPLIER;
	
	@ConfigField(name = "EnableBotReportButton", value = "true", comment =
	{
		"Enable the bot report button on the desired game servers."
	})
	public static boolean BOTREPORT_ENABLE;
	
	@ConfigField(name = "BotReportPointsResetHour", value = "00,00", comment =
	{
		"Report points restart hour. Format: HH:MM ( PM mode, 24 hours clock)"
	})
	public static String[] BOTREPORT_RESETPOINT_HOUR;
	
	@ConfigField(name = "BotReportDelay", value = "30", comment =
	{
		"Delay between reports from the same player (in minutes)"
	})
	public static long BOTREPORT_REPORT_DELAY;
	
	@ConfigField(name = "AllowReportsFromSameClanMembers", value = "false", comment =
	{
		"Allow players from the same clan to report the same bot"
	})
	public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;
	
	@ConfigField(name = "AccountVariablesClassWhitelist", value = "", comment =
	{
		"An array of fully qualified classe names that are allowed to present in AccountVariables's database",
		"Used to prevent from code injection",
		"Example: org.l2junity.gameserver.model.holders.ItemHolder,org.l2junity.gameserver.model.holders.SkillHolder"
	})
	public static String[] ACCOUNT_VARIABLES_CLASS_WHITELIST;
	
	@ConfigField(name = "Debug", value = "false")
	public static boolean DEBUG;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		// Do we really need these tedious checks below?
		if (FS_TIME_ATTACK <= 0)
		{
			FS_TIME_ATTACK = 50;
		}
		if (FS_TIME_COOLDOWN <= 0)
		{
			FS_TIME_COOLDOWN = 5;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		
		if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1)
		{
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
		}
		else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6)
		{
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
		}
		
		SERVER_LIST_TYPE = getServerTypeId(_SERVER_LIST_TYPE.split(","));
		
		DeadlockDetector.ALLOWED = DEADLOCK_DETECTOR;
		DeadlockDetector.RESTART_ON_DEADLOCK = RESTART_ON_DEADLOCK;
		DeadlockDetector.CHECK_INTERVAL = DEADLOCK_CHECK_INTERVAL;
		
		if (RESTART_ON_DEADLOCK)
		{
			DeadlockDetector.RESTART_EVENT = () ->
			{
				Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
			};
		}
	}
	
	public static int getServerTypeId(String[] serverTypes)
	{
		int serverType = 0;
		for (String cType : serverTypes)
		{
			switch (cType.trim().toLowerCase())
			{
				case "normal":
					serverType |= 0x01;
					break;
				case "relax":
					serverType |= 0x02;
					break;
				case "test":
					serverType |= 0x04;
					break;
				case "broad":
					serverType |= 0x08;
					break;
				case "restricted":
					serverType |= 0x10;
					break;
				case "event":
					serverType |= 0x20;
					break;
				case "free":
					serverType |= 0x40;
					break;
				case "world":
					serverType |= 0x100;
					break;
				case "new":
					serverType |= 0x200;
					break;
				case "classic":
					serverType |= 0x400;
					break;
			}
		}
		return serverType;
	}
}
