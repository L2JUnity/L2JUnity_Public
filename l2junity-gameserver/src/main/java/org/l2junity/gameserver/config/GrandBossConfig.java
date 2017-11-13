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

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "GrandBoss")
public final class GrandBossConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "AntharasWaitTime", value = "20", comment =
	{
		"Delay of appearance time of Antharas. Value is minute. Range 3-60"
	})
	public static int ANTHARAS_WAIT_TIME;
	
	@ConfigField(name = "IntervalOfAntharasSpawn", value = "264", comment =
	{
		"Interval time of Antharas. Value is hour. Range 1-480"
	})
	public static int ANTHARAS_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfAntharasSpawn", value = "72", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int ANTHARAS_SPAWN_RANDOM;
	
	@ConfigField(name = "ValakasWaitTime", value = "30", comment =
	{
		"Delay of appearance time of Valakas. Value is minute. Range 3-60"
	})
	public static int VALAKAS_WAIT_TIME;
	
	@ConfigField(name = "IntervalOfValakasSpawn", value = "264", comment =
	{
		"Interval time of Valakas. Value is hour. Range 1-480"
	})
	public static int VALAKAS_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfValakasSpawn", value = "72", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int VALAKAS_SPAWN_RANDOM;
	
	@ConfigField(name = "IntervalOfBaiumSpawn", value = "168", comment =
	{
		"Interval time of Baium. Value is hour. Range 1-480"
	})
	public static int BAIUM_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfBaiumSpawn", value = "48", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int BAIUM_SPAWN_RANDOM;
	
	@ConfigField(name = "IntervalOfCoreSpawn", value = "60", comment =
	{
		"Interval time of Core. Value is hour. Range 1-480"
	})
	public static int CORE_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfCoreSpawn", value = "24", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int CORE_SPAWN_RANDOM;
	
	@ConfigField(name = "IntervalOfOrfenSpawn", value = "48", comment =
	{
		"Interval time of Orfen. Value is hour. Range 1-480"
	})
	public static int ORFEN_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfOrfenSpawn", value = "20", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int ORFEN_SPAWN_RANDOM;
	
	@ConfigField(name = "IntervalOfQueenAntSpawn", value = "36", comment =
	{
		"Interval time of QueenAnt. Value is hour. Range 1-480"
	})
	public static int QUEEN_ANT_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfQueenAntSpawn", value = "17", comment =
	{
		"Random interval. Range 1-192"
	})
	public static int QUEEN_ANT_SPAWN_RANDOM;
	
	@ConfigField(name = "BelethMinPlayers", value = "36", comment =
	{
		"Minimal count of players for enter to Beleth. Retail: 36"
	})
	public static int BELETH_MIN_PLAYERS;
	
	@ConfigField(name = "IntervalOfBelethSpawn", value = "192", comment =
	{
		"Interval time of Beleth. Value is hour. Range 1-480. Retail: 192"
	})
	public static int BELETH_SPAWN_INTERVAL;
	
	@ConfigField(name = "RandomOfBelethSpawn", value = "148", comment =
	{
		"Random interval. Range 1-192. Retail: 148"
	})
	public static int BELETH_SPAWN_RANDOM;
}
