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
package org.l2junity.scripts.ai.individual.TalkingIsland;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Scarecrow AI.
 * @author ivantotov
 */
public final class Scarecrow extends AbstractNpcAI
{
	// NPCs
	private static final int SCARECROW = 27457;
	private static final int TRAINING_DUMMY = 19546;
	
	private Scarecrow()
	{
		addSpawnId(SCARECROW, TRAINING_DUMMY);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.disableCoreAI(true);
		npc.setIsImmobilized(true);
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new Scarecrow();
	}
}
