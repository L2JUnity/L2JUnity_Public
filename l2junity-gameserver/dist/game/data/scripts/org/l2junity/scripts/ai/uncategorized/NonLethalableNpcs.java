/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.ai.uncategorized;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class NonLethalableNpcs extends AbstractNpcAI
{
	private static final int[] NPCS =
	{
		35062, // Headquarters
	};
	
	public NonLethalableNpcs()
	{
		addSpawnId(NPCS);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setLethalable(false);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new NonLethalableNpcs();
	}
}
