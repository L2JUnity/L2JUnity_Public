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

import java.util.ArrayList;
import java.util.List;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.util.PropertiesParser;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Olympiad")
public final class OlympiadConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(OlympiadConfig.class);
	
	@ConfigField(name = "AltOlyStartTime", value = "18", comment =
	{
		"Olympiad Start Time in Military hours Default 6pm (18)"
	})
	public static int ALT_OLY_START_TIME;
	
	@ConfigField(name = "AltOlyMin", value = "00", comment =
	{
		"Olympiad Start Time for Min's, Default 00 so at the start of the hour."
	})
	public static int ALT_OLY_MIN;
	
	@ConfigField(name = "AltOlyMaxBuffs", value = "5", comment =
	{
		"Maximum number of buffs."
	})
	public static int ALT_OLY_MAX_BUFFS;
	
	@ConfigField(name = "AltOlyPeriod", value = "1", comment =
	{
		"1 = Period will be in months.",
		"2 = Period will be in weeks.",
		"3 = Period will be in days."
	})
	public static int ALT_OLY_PERIOD;
	
	@ConfigField(name = "AltOlyPeriodMultiplier", value = "1", comment =
	{
		"Every how many months/weeks/days olympaid period will end."
	})
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	
	@ConfigField(name = "AltOlyCPeriod", value = "21600000", comment =
	{
		"Olympiad Competition Period, Default 6 hours.",
		"(If set different, should be increment by 10mins)"
	})
	public static long ALT_OLY_CPERIOD;
	
	@ConfigField(name = "AltOlyBattle", value = "300000", comment =
	{
		"Olympiad Battle Period, Default 5 minutes."
	})
	public static long ALT_OLY_BATTLE;
	
	@ConfigField(name = "AltOlyWPeriod", value = "604800000", comment =
	{
		"Olympiad Weekly Period, Default 1 week",
		"Used for adding points to nobles"
	})
	public static long ALT_OLY_WPERIOD;
	
	@ConfigField(name = "AltOlyVPeriod", value = "86400000", comment =
	{
		"Olympiad Validation Period, Default 24 Hours."
	})
	public static long ALT_OLY_VPERIOD;
	
	@ConfigField(name = "AltOlyStartPoints", value = "10", comment =
	{
		"Points for reaching Noblesse for the first time"
	})
	public static int ALT_OLY_START_POINTS;
	
	@ConfigField(name = "AltOlyWeeklyPoints", value = "10", comment =
	{
		"Points every week"
	})
	public static int ALT_OLY_WEEKLY_POINTS;
	
	@ConfigField(name = "AltOlyClassedParticipants", value = "11", comment =
	{
		"Required number of participants for the class based games"
	})
	public static int ALT_OLY_CLASSED;
	
	@ConfigField(name = "AltOlyNonClassedParticipants", value = "11", comment =
	{
		"Required number of participants for the non-class based games"
	})
	public static int ALT_OLY_NONCLASSED;
	
	@ConfigField(name = "AltOlyRegistrationDisplayNumber", value = "100", comment =
	{
		"Number used for displaying amount of registered participants, messages 'Fewer than ...' or 'More than ...'.",
		"0 for displaying digits instead of text phrase (old style)."
	})
	public static int ALT_OLY_REG_DISPLAY;
	
	@ConfigField(name = "AltOlyClassedReward", value = "13722,50", comment =
	{
		"Reward for the class based games",
		"Format: itemId1,itemNum1;itemId2,itemNum2..."
	})
	public static String _ALT_OLY_CLASSED_REWARD;
	public static List<ItemHolder> ALT_OLY_CLASSED_REWARD;
	
	@ConfigField(name = "AltOlyNonClassedReward", value = "13722,40", comment =
	{
		"Reward for the non-class based games",
		"Format: itemId1,itemNum1;itemId2,itemNum2..."
	})
	public static String _ALT_OLY_NONCLASSED_REWARD;
	public static List<ItemHolder> ALT_OLY_NONCLASSED_REWARD;
	
	@ConfigField(name = "AltOlyTeamReward", value = "13722,85", comment =
	{
		"Reward for the 3x3 teams games",
		"Format: itemId1,itemNum1;itemId2,itemNum2..."
	})
	public static String _ALT_OLY_TEAM_REWARD;
	public static List<ItemHolder> ALT_OLY_TEAM_REWARD;
	
	@ConfigField(name = "AltOlyCompRewItem", value = "45584", comment =
	{
		"ItemId used for exchanging to the points."
	})
	public static int ALT_OLY_COMP_RITEM;
	
	@ConfigField(name = "AltOlyMinMatchesForPoints", value = "10", comment =
	{
		"The minimal matches you need to participate to receive point rewards"
	})
	public static int ALT_OLY_MIN_MATCHES;
	
	@ConfigField(name = "AltOlyMarkPerPoint", value = "20", comment =
	{
		"Rate to exchange points to reward item."
	})
	public static int ALT_OLY_MARK_PER_POINT;
	
	@ConfigField(name = "AltOlyHeroPoints", value = "30", comment =
	{
		"Noblesse points awarded to Heroes."
	})
	public static int ALT_OLY_HERO_POINTS;
	
	@ConfigField(name = "AltOlyRank1Points", value = "60", comment =
	{
		"Noblesse points awarded to Rank 1 members."
	})
	public static int ALT_OLY_RANK1_POINTS;
	
	@ConfigField(name = "AltOlyRank2Points", value = "50", comment =
	{
		"Noblesse points awarded to Rank 2 members."
	})
	public static int ALT_OLY_RANK2_POINTS;
	
	@ConfigField(name = "AltOlyRank3Points", value = "45", comment =
	{
		"Noblesse points awarded to Rank 3 members."
	})
	public static int ALT_OLY_RANK3_POINTS;
	
	@ConfigField(name = "AltOlyRank4Points", value = "40", comment =
	{
		"Noblesse points awarded to Rank 4 members."
	})
	public static int ALT_OLY_RANK4_POINTS;
	
	@ConfigField(name = "AltOlyRank5Points", value = "30", comment =
	{
		"Noblesse points awarded to Rank 5 members."
	})
	public static int ALT_OLY_RANK5_POINTS;
	
	@ConfigField(name = "AltOlyMaxPoints", value = "10", comment =
	{
		"Maximum points that player can gain/lose on a match."
	})
	public static int ALT_OLY_MAX_POINTS;
	
	@ConfigField(name = "AltOlyDividerClassed", value = "5", comment =
	{
		"Divider for points in classed and non-classed games"
	})
	public static int ALT_OLY_DIVIDER_CLASSED;
	
	@ConfigField(name = "AltOlyDividerNonClassed", value = "5")
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	
	@ConfigField(name = "AltOlyMaxWeeklyMatches", value = "70", comment =
	{
		"Maximum number of matches a Noblesse character can join per week"
	})
	public static int ALT_OLY_MAX_WEEKLY_MATCHES;
	
	@ConfigField(name = "AltOlyMaxWeeklyMatchesNonClassed", value = "60", comment =
	{
		"Maximum number of Class-Irrelevant Individual matches a character can join per week"
	})
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
	
	@ConfigField(name = "AltOlyMaxWeeklyMatchesClassed", value = "30", comment =
	{
		"Maximum number of Class Individual matches a character can join per week"
	})
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
	
	@ConfigField(name = "AltOlyMaxWeeklyMatchesTeam", value = "10", comment =
	{
		"Maximum number of Class-Irrelevant Team matches a character can join per week"
	})
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
	
	@ConfigField(name = "AltOlyLogFights", value = "false", comment =
	{
		"Log all Olympiad fights and outcome to olympiad.csv file."
	})
	public static boolean ALT_OLY_LOG_FIGHTS;
	
	@ConfigField(name = "AltOlyShowMonthlyWinners", value = "true", comment =
	{
		"Hero tables show last month's winners or current status."
	})
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	
	@ConfigField(name = "AltOlyAnnounceGames", value = "true", comment =
	{
		"Olympiad Managers announce each start of fight."
	})
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	
	@ConfigField(name = "AltOlyRestrictedItems", value = "6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,9388,9389,9390,17049,17050,17051,17052,17053,17054,17055,17056,17057,17058,17059,17060,17061,20759,20775,20776,20777,20778,14774", comment =
	{
		"Restrict specified items in Olympiad. ItemID's need to be separated with a comma (ex. 1,200,350)",
		"Equipped items will be moved to inventory during port."
	})
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
	
	@ConfigField(name = "AltOlyEnchantLimit", value = "-1", comment =
	{
		"Enchant limit for items during Olympiad battles. Disabled = -1."
	})
	public static int ALT_OLY_ENCHANT_LIMIT;
	
	@ConfigField(name = "AltOlyWaitTime", value = "60", comment =
	{
		"Time to wait before teleported to arena."
	})
	public static int ALT_OLY_WAIT_TIME;
	
	@Override
	protected void loadImpl(PropertiesParser properties, PropertiesParser override)
	{
		ALT_OLY_CLASSED_REWARD = parseItemsList(_ALT_OLY_CLASSED_REWARD);
		ALT_OLY_NONCLASSED_REWARD = parseItemsList(_ALT_OLY_NONCLASSED_REWARD);
		ALT_OLY_TEAM_REWARD = parseItemsList(_ALT_OLY_TEAM_REWARD);
	}
	
	/**
	 * Parse a config value from its string representation to a two-dimensional int array.<br>
	 * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
	 * @param line the value of the parameter to parse
	 * @return the parsed list or {@code null} if nothing was parsed
	 */
	private static List<ItemHolder> parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
		{
			// nothing to do here
			return null;
		}
		
		String[] valueSplit;
		final List<ItemHolder> result = new ArrayList<>(propertySplit.length);
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				LOGGER.warn("parseItemsList[Config.load()]: invalid entry -> \"{}\", should be itemId,itemNumber. Skipping to the next entry in the list.", valueSplit[0]);
				continue;
			}
			
			int itemId = -1;
			try
			{
				itemId = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warn("parseItemsList[Config.load()]: invalid itemId -> \"{}\", value must be an integer. Skipping to the next entry in the list.", valueSplit[0]);
				continue;
			}
			int count = -1;
			try
			{
				count = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warn("parseItemsList[Config.load()]: invalid item number -> \"{}\", value must be an integer. Skipping to the next entry in the list.", valueSplit[1]);
				continue;
			}
			if ((itemId > 0) && (count > 0))
			{
				result.add(new ItemHolder(itemId, count));
			}
		}
		return result;
	}
}
