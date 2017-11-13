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
 * @author Sdw
 */
@ConfigClass(fileName = "TrainigCamp")
public class TrainingCampConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "TrainingCampEnable", value = "false", comment =
	{
		"Enable or disable Training Camp"
	})
	public static boolean ENABLE;
	
	@ConfigField(name = "TrainingCampPremiumOnly", value = "true", comment =
	{
		"Only Premium account can access training camp"
	})
	public static boolean PREMIUM_ONLY;
	
	@ConfigField(name = "TrainingCampDuration", value = "18000", comment =
	{
		"Max duration for Training Camp in seconds. NA : 18000, RU : 36000"
	})
	public static int MAX_DURATION;
	
	@ConfigField(name = "TrainingCampMinLevel", value = "18", comment =
	{
		"Min level to enter Training Camp"
	})
	public static int MIN_LEVEL;
	
	@ConfigField(name = "TrainingCampMaxLevel", value = "127", comment =
	{
		"Max level to enter Training Camp"
	})
	public static int MAX_LEVEL;
}
