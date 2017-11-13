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
@ConfigClass(fileName = "GraciaSeeds")
public final class GraciaSeedsConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "TiatKillCountForNextState", value = "10", comment =
	{
		"Count of Kills which needed for Stage 2"
	})
	public static int SOD_TIAT_KILL_COUNT;
	
	@ConfigField(name = "Stage2Length", value = "720", comment =
	{
		"Length of Stage 2 before the Defense state starts (in minutes)."
	})
	public static long SOD_STAGE_2_LENGTH;
}
