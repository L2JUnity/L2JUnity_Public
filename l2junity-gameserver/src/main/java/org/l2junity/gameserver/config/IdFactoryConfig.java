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
@ConfigClass(fileName = "IdFactory")
public final class IdFactoryConfig extends ConfigPropertiesLoader
{
	public enum IdFactoryType
	{
		BitSet,
		Stack
	}
	
	@ConfigField(name = "IDFactory", value = "BitSet", comment =
	{
		"Tell server which IDFactory Class to use:",
		"Compaction = Original method",
		"BitSet = One non compaction method",
		"Stack = Another non compaction method"
	}, reloadable = false)
	public static IdFactoryType IDFACTORY_TYPE;
	
	@ConfigField(name = "BadIdChecking", value = "true", comment =
	{
		"Check for bad ids in the database on server boot up.",
		"Much faster load time without it, but may cause problems."
	}, reloadable = false)
	public static boolean BAD_ID_CHECKING;
}
