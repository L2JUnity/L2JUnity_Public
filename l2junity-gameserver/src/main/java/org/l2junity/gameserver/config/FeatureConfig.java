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

import java.util.List;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Feature")
public final class FeatureConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "CastleTeleportFunctionFeeRatio", value = "604800000", comment =
	{
		"Teleport Function price",
		"Price = 7 days"
	})
	public static long CS_TELE_FEE_RATIO;
	
	@ConfigField(name = "CastleTeleportFunctionFeeLvl1", value = "1000")
	public static int CS_TELE1_FEE;
	
	@ConfigField(name = "CastleTeleportFunctionFeeLvl2", value = "10000")
	public static int CS_TELE2_FEE;
	
	@ConfigField(name = "CastleMpRegenerationFunctionFeeRatio", value = "604800000", comment =
	{
		"MP Regeneration price",
		"Price = 7 day"
	})
	public static long CS_MPREG_FEE_RATIO;
	
	@ConfigField(name = "CastleMpRegenerationFeeLvl1", value = "45000")
	public static int CS_MPREG1_FEE;
	
	@ConfigField(name = "CastleMpRegenerationFeeLvl2", value = "65000")
	public static int CS_MPREG2_FEE;
	
	@ConfigField(name = "CastleHpRegenerationFunctionFeeRatio", value = "604800000", comment =
	{
		"Hp Regeneration price",
		"Price = 7 day"
	})
	public static long CS_HPREG_FEE_RATIO;
	
	@ConfigField(name = "CastleHpRegenerationFeeLvl1", value = "12000")
	public static int CS_HPREG1_FEE;
	
	@ConfigField(name = "CastleHpRegenerationFeeLvl2", value = "20000")
	public static int CS_HPREG2_FEE;
	
	@ConfigField(name = "CastleExpRegenerationFunctionFeeRatio", value = "604800000", comment =
	{
		"Exp Regeneration price",
		"Price = 7 day"
	})
	public static long CS_EXPREG_FEE_RATIO;
	
	@ConfigField(name = "CastleExpRegenerationFeeLvl1", value = "63000")
	public static int CS_EXPREG1_FEE;
	
	@ConfigField(name = "CastleExpRegenerationFeeLvl2", value = "70000")
	public static int CS_EXPREG2_FEE;
	
	@ConfigField(name = "CastleSupportFunctionFeeRatio", value = "604800000", comment =
	{
		"Support magic buff price",
		"Price = 7 day"
	})
	public static long CS_SUPPORT_FEE_RATIO;
	
	@ConfigField(name = "CastleSupportFeeLvl1", value = "49000")
	public static int CS_SUPPORT1_FEE;
	
	@ConfigField(name = "CastleSupportFeeLvl2", value = "120000")
	public static int CS_SUPPORT2_FEE;
	
	@ConfigField(name = "SiegeHourList", value = "16,20", comment =
	{
		"Siege Time rules",
		"Which hours can change Castle Lords"
	})
	public static List<Integer> SIEGE_HOUR_LIST;
	
	@ConfigField(name = "BuyTaxForNeutralSide", value = "15", comment =
	{
		"Taxes for castles",
		"Buy tax in percent when is castle owned by npc's.",
		"Defualt: 15"
	})
	public static int CASTLE_BUY_TAX_NEUTRAL;
	
	@ConfigField(name = "BuyTaxForLightSide", value = "0", comment =
	{
		"Buy tax in percent when is castle owned by player's and castle is on light side.",
		"Defualt: 0"
	})
	public static int CASTLE_BUY_TAX_LIGHT;
	
	@ConfigField(name = "BuyTaxForDarkSide", value = "30", comment =
	{
		"Buy tax in percent when is castle owned by player's and castle is on dark side.",
		"Defualt: 30"
	})
	public static int CASTLE_BUY_TAX_DARK;
	
	@ConfigField(name = "SellTaxForNeutralSide", value = "15", comment =
	{
		"Sell tax in percent when is castle owned by npc's.",
		"Defualt: 15"
	})
	public static int CASTLE_SELL_TAX_NEUTRAL;
	
	@ConfigField(name = "SellTaxForLightSide", value = "0", comment =
	{
		"Sell tax in percent when is castle owned by player's and castle is on light side.",
		"Defualt: 0"
	})
	public static int CASTLE_SELL_TAX_LIGHT;
	
	@ConfigField(name = "SellTaxForDarkSide", value = "30", comment =
	{
		"Sell tax in percent when is castle owned by player's and castle is on dark side.",
		"Defualt: 30"
	})
	public static int CASTLE_SELL_TAX_DARK;
	
	@ConfigField(name = "OuterDoorUpgradePriceLvl2", value = "3000000", comment =
	{
		"Outer Door upgrade price"
	})
	public static int OUTER_DOOR_UPGRADE_PRICE2;
	
	@ConfigField(name = "OuterDoorUpgradePriceLvl3", value = "4000000")
	public static int OUTER_DOOR_UPGRADE_PRICE3;
	
	@ConfigField(name = "OuterDoorUpgradePriceLvl5", value = "5000000")
	public static int OUTER_DOOR_UPGRADE_PRICE5;
	
	@ConfigField(name = "InnerDoorUpgradePriceLvl2", value = "750000", comment =
	{
		"Inner Door upgrade price"
	})
	public static int INNER_DOOR_UPGRADE_PRICE2;
	
	@ConfigField(name = "InnerDoorUpgradePriceLvl3", value = "900000")
	public static int INNER_DOOR_UPGRADE_PRICE3;
	
	@ConfigField(name = "InnerDoorUpgradePriceLvl5", value = "1000000")
	public static int INNER_DOOR_UPGRADE_PRICE5;
	
	@ConfigField(name = "WallUpgradePriceLvl2", value = "1600000", comment =
	{
		"Wall upgrade price"
	})
	public static int WALL_UPGRADE_PRICE2;
	
	@ConfigField(name = "WallUpgradePriceLvl3", value = "1800000")
	public static int WALL_UPGRADE_PRICE3;
	
	@ConfigField(name = "WallUpgradePriceLvl5", value = "2000000")
	public static int WALL_UPGRADE_PRICE5;
	
	@ConfigField(name = "TrapUpgradePriceLvl1", value = "3000000", comment =
	{
		"Trap upgrade price"
	})
	public static int TRAP_UPGRADE_PRICE1;
	
	@ConfigField(name = "TrapUpgradePriceLvl2", value = "4000000")
	public static int TRAP_UPGRADE_PRICE2;
	
	@ConfigField(name = "TrapUpgradePriceLvl3", value = "5000000")
	public static int TRAP_UPGRADE_PRICE3;
	
	@ConfigField(name = "TrapUpgradePriceLvl4", value = "6000000")
	public static int TRAP_UPGRADE_PRICE4;
	
	@ConfigField(name = "FortressTeleportFunctionFeeRatio", value = "604800000", comment =
	{
		"Teleport Function price",
		"Price = 7 days"
	})
	public static long FS_TELE_FEE_RATIO;
	
	@ConfigField(name = "FortressTeleportFunctionFeeLvl1", value = "1000")
	public static int FS_TELE1_FEE;
	
	@ConfigField(name = "FortressTeleportFunctionFeeLvl2", value = "10000")
	public static int FS_TELE2_FEE;
	
	@ConfigField(name = "FortressMpRegenerationFunctionFeeRatio", value = "86400000", comment =
	{
		"MP Regeneration price",
		"Price = 1 day"
	})
	public static long FS_MPREG_FEE_RATIO;
	
	@ConfigField(name = "FortressMpRegenerationFeeLvl1", value = "6500")
	public static int FS_MPREG1_FEE;
	
	@ConfigField(name = "FortressMpRegenerationFeeLvl2", value = "9300")
	public static int FS_MPREG2_FEE;
	
	@ConfigField(name = "FortressHpRegenerationFunctionFeeRatio", value = "86400000", comment =
	{
		"Hp Regeneration price",
		"Price = 1 day"
	})
	public static long FS_HPREG_FEE_RATIO;
	
	@ConfigField(name = "FortressHpRegenerationFeeLvl1", value = "2000")
	public static int FS_HPREG1_FEE;
	
	@ConfigField(name = "FortressHpRegenerationFeeLvl2", value = "3500")
	public static int FS_HPREG2_FEE;
	
	@ConfigField(name = "FortressExpRegenerationFunctionFeeRatio", value = "86400000", comment =
	{
		"Exp Regeneration price",
		"Price = 1 day"
	})
	public static long FS_EXPREG_FEE_RATIO;
	
	@ConfigField(name = "FortressExpRegenerationFeeLvl1", value = "9000")
	public static int FS_EXPREG1_FEE;
	
	@ConfigField(name = "FortressExpRegenerationFeeLvl2", value = "10000")
	public static int FS_EXPREG2_FEE;
	
	@ConfigField(name = "FortressSupportFunctionFeeRatio", value = "86400000", comment =
	{
		"Support magic buff price",
		"Price = 1 day"
	})
	public static long FS_SUPPORT_FEE_RATIO;
	
	@ConfigField(name = "FortressSupportFeeLvl1", value = "7000")
	public static int FS_SUPPORT1_FEE;
	
	@ConfigField(name = "FortressSupportFeeLvl2", value = "17000")
	public static int FS_SUPPORT2_FEE;
	
	@ConfigField(name = "FortressBloodOathCount", value = "1", comment =
	{
		"The number of Blood Oath which given to the Fort owner clan when Fort Updater runs"
	})
	public static int FS_BLOOD_OATH_COUNT;
	
	@ConfigField(name = "FortressPeriodicUpdateFrequency", value = "360", comment =
	{
		"This is the time frequently when Fort owner gets Blood Oath, supply level raised and Fort fee is payed",
		"Default 360 mins"
	})
	public static int FS_UPDATE_FRQ;
	
	@ConfigField(name = "FortressMaxSupplyLevel", value = "6", comment =
	{
		"The maximum Fort supply level",
		"Max lvl what you can define here is 21!"
	})
	public static int FS_MAX_SUPPLY_LEVEL;
	
	@ConfigField(name = "FortressFeeForCastle", value = "25000", comment =
	{
		"Fort fee which payed to the Castle"
	})
	public static int FS_FEE_FOR_CASTLE;
	
	@ConfigField(name = "FortressMaximumOwnTime", value = "168", comment =
	{
		"The maximum time while a clan can own a fortress",
		"Deafault: 168 hours"
	})
	public static int FS_MAX_OWN_TIME;
	
	@ConfigField(name = "TakeFortPoints", value = "200", comment =
	{
		"---------------------------------------------------------------------------",
		"Clan Reputation Points",
		"---------------------------------------------------------------------------",
		"Reputation score gained by taking Fortress."
	})
	public static int TAKE_FORT_POINTS;
	
	@ConfigField(name = "LooseFortPoints", value = "0", comment =
	{
		"Reputation score reduced by loosing Fortress in battle."
	})
	public static int LOOSE_FORT_POINTS;
	
	@ConfigField(name = "TakeCastlePoints", value = "1500", comment =
	{
		"Reputation score gained by taking Castle."
	})
	public static int TAKE_CASTLE_POINTS;
	
	@ConfigField(name = "LooseCastlePoints", value = "3000", comment =
	{
		"Reputation score reduced by loosing Castle in battle."
	})
	public static int LOOSE_CASTLE_POINTS;
	
	@ConfigField(name = "CastleDefendedPoints", value = "750", comment =
	{
		"Reputation score gained by defended Castle."
	})
	public static int CASTLE_DEFENDED_POINTS;
	
	@ConfigField(name = "FestivalOfDarknessWin", value = "200", comment =
	{
		"Reputation score gained per clan members of festival winning party."
	})
	public static int FESTIVAL_WIN_POINTS;
	
	@ConfigField(name = "HeroPoints", value = "1000", comment =
	{
		"Reputation score gained for per hero clan members."
	})
	public static int HERO_POINTS;
	
	@ConfigField(name = "CreateRoyalGuardCost", value = "5000", comment =
	{
		"Reputation score reduced by creating Royal Guard."
	})
	public static int ROYAL_GUARD_COST;
	
	@ConfigField(name = "CreateKnightUnitCost", value = "10000", comment =
	{
		"Reputation score reduced by creating Knight Unit."
	})
	public static int KNIGHT_UNIT_COST;
	
	@ConfigField(name = "ReinforceKnightUnitCost", value = "5000", comment =
	{
		"Reputation score reduced by reinforcing Knight Unit (if clan level is 9 or more)."
	})
	public static int KNIGHT_REINFORCE_COST;
	
	@ConfigField(name = "KillBallistaPoints", value = "500", comment =
	{
		"Reputation score gained per killed ballista."
	})
	public static int BALLISTA_POINTS;
	
	@ConfigField(name = "BloodAlliancePoints", value = "500", comment =
	{
		"Reputation score gained for one Blood Alliance."
	})
	public static int BLOODALLIANCE_POINTS;
	
	@ConfigField(name = "BloodOathPoints", value = "200", comment =
	{
		"Reputation score gained for 10 Blood Oaths."
	})
	public static int BLOODOATH_POINTS;
	
	@ConfigField(name = "KnightsEpaulettePoints", value = "20", comment =
	{
		"Reputation score gained for 100 Knight's Epaulettes."
	})
	public static int KNIGHTSEPAULETTE_POINTS;
	
	@ConfigField(name = "ReputationScorePerKill", value = "1", comment =
	{
		"Reputation score gained/reduced per kill during a clan war or siege war."
	})
	public static int REPUTATION_SCORE_PER_KILL;
	
	@ConfigField(name = "CompleteAcademyMinPoints", value = "190", comment =
	{
		"Minimum Reputation score gained after completing 2nd class transfer under Academy."
	})
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	
	@ConfigField(name = "CompleteAcademyMaxPoints", value = "650", comment =
	{
		"Maximum Reputation score gained after completing 2nd class transfer under Academy."
	})
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
	
	@ConfigField(name = "ClanLevel6Cost", value = "5000", comment =
	{
		"Reputation score reduced by increasing clan level."
	})
	public static int CLAN_LEVEL_6_COST;
	
	@ConfigField(name = "ClanLevel7Cost", value = "10000")
	public static int CLAN_LEVEL_7_COST;
	
	@ConfigField(name = "ClanLevel8Cost", value = "20000")
	public static int CLAN_LEVEL_8_COST;
	
	@ConfigField(name = "ClanLevel9Cost", value = "40000")
	public static int CLAN_LEVEL_9_COST;
	
	@ConfigField(name = "ClanLevel10Cost", value = "40000")
	public static int CLAN_LEVEL_10_COST;
	
	@ConfigField(name = "ClanLevel11Cost", value = "75000")
	public static int CLAN_LEVEL_11_COST;
	
	@ConfigField(name = "ClanLevel6Requirement", value = "30", comment =
	{
		"Number of clan members needed to increase clan level."
	})
	public static int CLAN_LEVEL_6_REQUIREMENT;
	
	@ConfigField(name = "ClanLevel7Requirement", value = "50")
	public static int CLAN_LEVEL_7_REQUIREMENT;
	
	@ConfigField(name = "ClanLevel8Requirement", value = "80")
	public static int CLAN_LEVEL_8_REQUIREMENT;
	
	@ConfigField(name = "ClanLevel9Requirement", value = "120")
	public static int CLAN_LEVEL_9_REQUIREMENT;
	
	@ConfigField(name = "ClanLevel10Requirement", value = "140")
	public static int CLAN_LEVEL_10_REQUIREMENT;
	
	@ConfigField(name = "ClanLevel11Requirement", value = "170")
	public static int CLAN_LEVEL_11_REQUIREMENT;
	
	@ConfigField(name = "AllowRideWyvernAlways", value = "false", comment =
	{
		"Allow riding wyvern ignoring 7 Signs status",
		"This will allow Castle Lords to ride wyvern even when Dusk has won Seal of Strife"
	})
	public static boolean ALLOW_WYVERN_ALWAYS;
	
	@ConfigField(name = "AllowRideWyvernDuringSiege", value = "true", comment =
	{
		"Allow riding wyvern during Castle/Fort Siege"
	})
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
}
