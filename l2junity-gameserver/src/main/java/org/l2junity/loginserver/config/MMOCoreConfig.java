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
package org.l2junity.loginserver.config;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "MMO")
public final class MMOCoreConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "SleepTime", value = "20", comment =
	{
		"Sleep time for all Selectors",
		"After he finished his job the Selector waits the given time in milliseconds",
		"Lower values will speed up the loop and the Ping is smaller"
	})
	public static int MMO_SELECTOR_SLEEP_TIME;
	
	@ConfigField(name = "MaxSendPerPass", value = "12", comment =
	{
		"Every loop it send a maximum of the given packages to each connection",
		"Lower values will speed up the loop and the Ping is smaller but cause less output"
	})
	public static int MMO_MAX_SEND_PER_PASS;
	
	@ConfigField(name = "MaxReadPerPass", value = "12", comment =
	{
		"Every loop it read a maximum of the given packages from each connection",
		"Lower values will speed up the loop and the Ping is smaller but cause less input"
	})
	public static int MMO_MAX_READ_PER_PASS;
	
	@ConfigField(name = "HelperBufferCount", value = "20", comment =
	{
		"Each unfinished read/write need a TEMP storage Buffer",
		"on large player amount we need more Buffers",
		"if there are not enough buffers new ones are generated but not stored for future usage"
	})
	public static int MMO_HELPER_BUFFER_COUNT;
	
	@ConfigField(name = "TcpNoDelay", value = "false", comment =
	{
		"Setting this to True will lower your ping, at the cost of an increase in bandwidth consumption."
	})
	public static boolean MMO_TCP_NODELAY;
	
}
