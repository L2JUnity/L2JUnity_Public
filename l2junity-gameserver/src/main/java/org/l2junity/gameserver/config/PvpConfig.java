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

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "PVP")
public final class PvpConfig extends ConfigPropertiesLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(PvpConfig.class);
	
	@ConfigField(name = "CanGMDropEquipment", value = "false")
	public static boolean KARMA_DROP_GM;
	
	@ConfigField(name = "AwardPKKillPVPPoint", value = "false", comment =
	{
		"Should we award a pvp point for killing a player with karma?"
	})
	public static boolean KARMA_AWARD_PK_KILL;
	
	@ConfigField(name = "MinimumPKRequiredToDrop", value = "6")
	public static int KARMA_PK_LIMIT;
	
	@ConfigField(name = "ListOfPetItems", value = "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650", comment =
	{
		"Warning: Make sure the lists do NOT CONTAIN",
		"trailing spaces or spaces between the numbers!",
		"List of pet items we cannot drop."
	})
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	
	@ConfigField(name = "ListOfNonDroppableItems", value = "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,7694,8181,5575,7694,9388,9389,9390", comment =
	{
		"Lists of items which should NEVER be dropped (note, Adena will",
		"never be dropped) whether on this list or not"
	})
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	@ConfigField(name = "PvPVsNormalTime", value = "120000", comment =
	{
		"How much time one stays in PvP mode after hitting an innocent (in ms)"
	})
	public static int PVP_NORMAL_TIME;
	
	@ConfigField(name = "PvPVsPvPTime", value = "60000", comment =
	{
		"Length one stays in PvP mode after hitting a purple player (in ms)"
	})
	public static int PVP_PVP_TIME;
	
	@ConfigField(name = "MaxReputation", value = "500", comment =
	{
		"Max count of positive reputation"
	})
	public static int MAX_REPUTATION;
	
	@ConfigField(name = "ReputationIncrease", value = "100", comment =
	{
		"Reputation increase for kill one PK"
	})
	public static int REPUTATION_INCREASE;
}
