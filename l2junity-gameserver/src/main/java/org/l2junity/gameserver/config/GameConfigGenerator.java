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

import org.l2junity.commons.config.generator.AbstractConfigGenerator;
import org.l2junity.commons.util.AppInit;
import org.l2junity.commons.util.AppInit.ApplicationMode;

/**
 * @author lord_rex
 */
public final class GameConfigGenerator extends AbstractConfigGenerator
{
	@Override
	protected String getPackageName()
	{
		return GameConfigMarker.class.getPackage().getName();
	}
	
	public static void main(String[] args)
	{
		AppInit.MODE = ApplicationMode.GAME;
		new GameConfigGenerator();
	}
}
