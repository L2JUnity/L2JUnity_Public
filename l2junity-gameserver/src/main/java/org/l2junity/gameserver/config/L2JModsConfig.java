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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
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
@ConfigClass(fileName = "L2JMods")
public final class L2JModsConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(L2JModsConfig.class);
	
	@ConfigField(name = "ChampionEnable", value = "false", comment =
	{
		"Enable/Disable Champion Mob System."
	})
	public static boolean L2JMOD_CHAMPION_ENABLE;
	
	@ConfigField(name = "ChampionPassive", value = "false", comment =
	{
		"Force Champion mobs to be passive?",
		"To leave champion mobs to default/Aggressive, set to False.",
		"To set all champion mobs to Passive, set True."
	})
	public static boolean L2JMOD_CHAMPION_PASSIVE;
	
	@ConfigField(name = "ChampionFrequency", value = "5", comment =
	{
		"% chance for a mob to became champion (0 to disable)."
	})
	public static int L2JMOD_CHAMPION_FREQUENCY;
	
	@ConfigField(name = "ChampionTitle", value = "Champion", comment =
	{
		"Title of all Champion Mobs."
	})
	public static String L2JMOD_CHAMP_TITLE;
	
	@ConfigField(name = "ChampionMinLevel", value = "20", comment =
	{
		"Min and max levels allowed for a mob to be a Champion mob."
	})
	public static int L2JMOD_CHAMP_MIN_LVL;
	
	@ConfigField(name = "ChampionMaxLevel", value = "70")
	public static int L2JMOD_CHAMP_MAX_LVL;
	
	@ConfigField(name = "ChampionHp", value = "8", comment =
	{
		"Hp multiplier for Champion mobs."
	})
	public static int L2JMOD_CHAMPION_HP;
	
	@ConfigField(name = "ChampionRewards", value = "8", comment =
	{
		"Standard rewards multiplier for Champion mobs."
	})
	public static int L2JMOD_CHAMPION_REWARDS;
	
	@ConfigField(name = "ChampionAdenasRewards", value = "1.0", comment =
	{
		"Adena & Seal Stone rewards multiplier for Champion mobs."
	})
	public static float L2JMOD_CHAMPION_ADENAS_REWARDS;
	
	@ConfigField(name = "ChampionHpRegen", value = "1.0", comment =
	{
		"Hp Regen Multiplier for Champion mobs."
	})
	public static float L2JMOD_CHAMPION_HP_REGEN;
	
	@ConfigField(name = "ChampionAtk", value = "1.0", comment =
	{
		"P. Attack and M. Attack bonus for Champion mobs."
	})
	public static float L2JMOD_CHAMPION_ATK;
	
	@ConfigField(name = "ChampionSpdAtk", value = "1.0", comment =
	{
		"Physical/Magical Attack Speed bonus for Champion mobs."
	})
	public static float L2JMOD_CHAMPION_SPD_ATK;
	
	@ConfigField(name = "ChampionRewardLowerLvlItemChance", value = "0", comment =
	{
		"% Chance to obtain a specified reward item from a higher level Champion mob."
	})
	public static int L2JMOD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
	
	@ConfigField(name = "ChampionRewardHigherLvlItemChance", value = "0", comment =
	{
		"% Chance to obtain a specified reward item from a lower level Champion mob."
	})
	public static int L2JMOD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
	
	@ConfigField(name = "ChampionRewardItemID", value = "6393", comment =
	{
		"Specified reward item ID"
	})
	public static int L2JMOD_CHAMPION_REWARD_ID;
	
	@ConfigField(name = "ChampionRewardItemQty", value = "1", comment =
	{
		"The amount of the specified reward a player will receive if they are awarded the item."
	})
	public static int L2JMOD_CHAMPION_REWARD_QTY;
	
	@ConfigField(name = "ChampionEnableVitality", value = "false", comment =
	{
		"Do you want to enable the vitality calculation when killing champion mobs?",
		"Be aware that it can lead to huge unbalance on your server, your rate for that mob would",
		"then be 'mobXP x serverRate x vitalityRate x championXpRate",
		"Notes:",
		"	Works only if EnableVitality = True"
	})
	public static boolean L2JMOD_CHAMPION_ENABLE_VITALITY;
	
	@ConfigField(name = "ChampionEnableInInstances", value = "false", comment =
	{
		"Enable spawning of the champions in instances"
	})
	public static boolean L2JMOD_CHAMPION_ENABLE_IN_INSTANCES;
	
	@ConfigField(name = "BankingEnabled", value = "false", comment =
	{
		"Enable/Disable Banking System"
	})
	public static boolean BANKING_SYSTEM_ENABLED;
	
	@ConfigField(name = "BankingGoldbarCount", value = "1", comment =
	{
		"Amount of Goldbars a player gets when they use the '.deposit' command. Also the same amount they will lose with '.withdraw'."
	})
	public static int BANKING_SYSTEM_GOLDBARS;
	
	@ConfigField(name = "BankingAdenaCount", value = "500000000", comment =
	{
		"Amount of Adena a player gets when they use the '.withdraw' command. Also the same amount they will lose with '.deposit'."
	})
	public static int BANKING_SYSTEM_ADENA;
	
	@ConfigField(name = "OfflineTradeEnable", value = "false", comment =
	{
		"Option to enable or disable offline trade feature.",
		"Enable -> true, Disable -> false"
	})
	public static boolean OFFLINE_TRADE_ENABLE;
	
	@ConfigField(name = "OfflineCraftEnable", value = "false", comment =
	{
		"Option to enable or disable offline craft feature.",
		"Enable -> true, Disable -> false"
	})
	public static boolean OFFLINE_CRAFT_ENABLE;
	
	@ConfigField(name = "OfflineModeInPeaceZone", value = "false", comment =
	{
		"If set to True, off-line shops will be possible only peace zones."
	})
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	
	@ConfigField(name = "OfflineModeNoDamage", value = "false", comment =
	{
		"If set to True, players in off-line shop mode wont take any damage, thus they cannot be killed."
	})
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	
	@ConfigField(name = "RestoreOffliners", value = "false", comment =
	{
		"Restore offline traders/crafters after restart/shutdown. Default: false."
	})
	public static boolean RESTORE_OFFLINERS;
	
	@ConfigField(name = "OfflineMaxDays", value = "10", comment =
	{
		"Do not restore offline characters, after OfflineMaxDays days spent from first restore.",
		"Require server restart to disconnect expired shops.",
		"0 = disabled (always restore)."
	})
	public static int OFFLINE_MAX_DAYS;
	
	@ConfigField(name = "OfflineDisconnectFinished", value = "true", comment =
	{
		"Disconnect shop after finished selling, buying."
	})
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	
	@ConfigField(name = "OfflineSetNameColor", value = "false", comment =
	{
		"If set to True, name color will be changed then entering offline mode"
	})
	public static boolean OFFLINE_SET_NAME_COLOR;
	
	@ConfigField(name = "OfflineNameColor", value = "808080", comment =
	{
		"Color of the name in offline mode (if OfflineSetNameColor = True)"
	})
	public static int OFFLINE_NAME_COLOR;
	
	@ConfigField(name = "OfflineFame", value = "true", comment =
	{
		"Allow fame for characters in offline mode",
		"Enable -> true, Disable -> false"
	})
	public static boolean OFFLINE_FAME;
	
	@ConfigField(name = "EnableManaPotionSupport", value = "false", comment =
	{
		"This option will enable core support for:",
		"Mana Drug (item ID 726), using skill ID 10000.",
		"Mana Potion (item ID 728), using skill ID 10001."
	})
	public static boolean L2JMOD_ENABLE_MANA_POTIONS_SUPPORT;
	
	@ConfigField(name = "DisplayServerTime", value = "false", comment =
	{
		"This option will enable displaying of the local server time for /time command."
	})
	public static boolean L2JMOD_DISPLAY_SERVER_TIME;
	
	@ConfigField(name = "ScreenWelcomeMessageEnable", value = "false", comment =
	{
		"Show screen welcome message on character login"
	})
	public static boolean WELCOME_MESSAGE_ENABLED;
	
	@ConfigField(name = "ScreenWelcomeMessageText", value = "Welcome to L2J Unity!", comment =
	{
		"Screen welcome message text to show on character login if enabled",
		"('' for a new line, but message can have max 2 lines)"
	})
	public static String WELCOME_MESSAGE_TEXT;
	
	@ConfigField(name = "ScreenWelcomeMessageTime", value = "10000", comment =
	{
		"Show screen welcome message for x milliseconds when character log in to game if enabled"
	})
	public static int WELCOME_MESSAGE_TIME;
	
	@ConfigField(name = "AntiFeedEnable", value = "false", comment =
	{
		"This option will enable antifeed for pvp/pk/clanrep points."
	})
	public static boolean L2JMOD_ANTIFEED_ENABLE;
	
	@ConfigField(name = "AntiFeedDualbox", value = "true", comment =
	{
		"If set to True, kills from dualbox will not increase pvp/pk points",
		"and clan reputation will not be transferred."
	})
	public static boolean L2JMOD_ANTIFEED_DUALBOX;
	
	@ConfigField(name = "AntiFeedUseIP", value = "true", comment =
	{
		"If set to True, server will use the client's IP to verify if there are other similar considered as dualbox",
	})
	public static boolean L2JMOD_ANTIFEED_USE_IP;
	
	@ConfigField(name = "AntiFeedUseHWID", value = "false", comment =
	{
		"If set to True, server will use the client's IP to verify if there are other similar considered as dualbox",
		"This option will work only if you have HWID support by some anti-cheat like lameguard!"
	})
	public static boolean L2JMOD_ANTIFEED_USE_HWID;
	
	@ConfigField(name = "AntiFeedInterval", value = "120", comment =
	{
		"If character died faster than timeout - pvp/pk points for killer will not increase",
		"and clan reputation will not be transferred.",
		"Setting to 0 will disable this feature."
	})
	public static int L2JMOD_ANTIFEED_INTERVAL;
	
	@ConfigField(name = "AnnouncePkPvP", value = "false")
	public static boolean ANNOUNCE_PK_PVP;
	
	@ConfigField(name = "AnnouncePkPvPNormalMessage", value = "true", comment =
	{
		"Announce this as normal system message"
	})
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
	
	@ConfigField(name = "AnnouncePkMsg", value = "$killer has slaughtered $target", comment =
	{
		"PK message template",
		"variables: $killer, $target"
	})
	public static String ANNOUNCE_PK_MSG;
	
	@ConfigField(name = "AnnouncePvpMsg", value = "$killer has defeated $target", comment =
	{
		"Pvp message template",
		"variables: $killer, $target"
	})
	public static String ANNOUNCE_PVP_MSG;
	
	@ConfigField(name = "ChatAdmin", value = "false", comment =
	{
		"This option will enable using of the voice commands .banchat and .unbanchat",
		"for players with corresponding access level (default: 7).",
		"Check access_levels.sql and admin_command_access_rights for details."
	})
	public static boolean L2JMOD_CHAT_ADMIN;
	
	@ConfigField(name = "MultiLangEnable", value = "false", comment =
	{
		"Enable or disable multilingual support."
	})
	public static boolean L2JMOD_MULTILANG_ENABLE;
	
	@ConfigField(name = "MultiLangAllowed", value = "en,ru", comment =
	{
		"List of allowed languages, semicolon separated."
	})
	public static List<String> L2JMOD_MULTILANG_ALLOWED;
	
	@ConfigField(name = "MultiLangDefault", value = "en", comment =
	{
		"Default language, if not defined."
	})
	public static String L2JMOD_MULTILANG_DEFAULT;
	
	@ConfigField(name = "MultiLangVoiceCommand", value = "true", comment =
	{
		"Enable or disable voice command .lang for changing languages on the fly."
	})
	public static boolean L2JMOD_MULTILANG_VOICED_ALLOW;
	
	@ConfigField(name = "L2WalkerProtection", value = "false", comment =
	{
		"Basic protection against L2Walker."
	})
	public static boolean L2WALKER_PROTECTION;
	
	@ConfigField(name = "DebugVoiceCommand", value = "false", comment =
	{
		"This option will enable voice command .debug allowing players",
		"to turn on/off debugging on self only.",
		"(admin command //debug can enable debugging on any character)",
		"Use admin_command_access_rights table for defining access rights."
	})
	public static boolean L2JMOD_DEBUG_VOICE_COMMAND;
	
	@ConfigField(name = "DualboxCheckMaxPlayersPerIP", value = "0", comment =
	{
		"Maximum number of players per IP address allowed to enter game."
	})
	public static int L2JMOD_DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	
	@ConfigField(name = "DualboxCheckMaxOlympiadParticipantsPerIP", value = "0", comment =
	{
		"Maximum number of players per IP address allowed to participate in olympiad."
	})
	public static int L2JMOD_DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	
	@ConfigField(name = "DualboxCheckMaxL2EventParticipantsPerIP", value = "0", comment =
	{
		"Maximum number of players per IP address allowed to participate in events using L2J Event Engine (//event)."
	})
	public static int L2JMOD_DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	
	@ConfigField(name = "DualboxCheckWhitelist", value = "127.0.0.1,0", comment =
	{
		"Whitelist of the addresses for dualbox checks.",
		"Format: Address1,Number1;Address2,Number2...",
		"Network address can be number (127.0.0.1) or symbolic (localhost) formats.",
		"Additional connection number added to the global limits for this address.",
		"For example, if number of TvT event participants per IP address set to the 1 (no dualbox)",
		"and whitelist contains \"l2jserver.com,2\" then number of allowed participants from l2jserver.com",
		"will be 1+2=3. Use 0 or negative value for unlimited number of connections.",
		"127.0.0.1,0 means that there are no limits from localhost"
	})
	public static String _L2JMOD_DUALBOX_CHECK_WHITELIST;
	public static Map<Integer, Integer> L2JMOD_DUALBOX_CHECK_WHITELIST;
	
	@ConfigField(name = "AllowChangePassword", value = "false", comment =
	{
		"Enables .changepassword voiced command which allows the players to change their account's password ingame."
	})
	public static boolean L2JMOD_ALLOW_CHANGE_PASSWORD;
	
	@ConfigField(name = "OldDropBehavior", value = "false", comment =
	{
		"Enables L2J old drop behavior",
		"The old L2J system used to add amount of items drop per 100% range of chance.",
		"For example, if chance is 230% when rate are applied, it will do :",
		"amount dropped = (2 * getRandomAmount(min,max)) + 30% chance to get ad additional getRandomAmount(min,max)",
		"Default : False"
	})
	public static boolean L2JMOD_OLD_DROP_BEHAVIOR;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		final String[] dualboxCheckWhiteList = _L2JMOD_DUALBOX_CHECK_WHITELIST.split(";");
		L2JMOD_DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
		for (final String entry : dualboxCheckWhiteList)
		{
			final String[] entrySplit = entry.split(",");
			if (entrySplit.length != 2)
			{
				LOGGER.warn("DualboxCheck[L2JModsConfig.loadImpl()]: invalid config property -> DualboxCheckWhitelist {}", entry);
			}
			else
			{
				try
				{
					int num = Integer.parseInt(entrySplit[1]);
					num = num == 0 ? -1 : num;
					L2JMOD_DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
				}
				catch (final UnknownHostException e)
				{
					LOGGER.warn("DualboxCheck[L2JModsConfig.loadImpl()]: invalid address -> DualboxCheckWhitelist {}", entrySplit[0]);
				}
				catch (final NumberFormatException e)
				{
					LOGGER.warn("DualboxCheck[L2JModsConfig.loadImpl()]: invalid number -> DualboxCheckWhitelist {}", entrySplit[1]);
				}
			}
		}
	}
}
