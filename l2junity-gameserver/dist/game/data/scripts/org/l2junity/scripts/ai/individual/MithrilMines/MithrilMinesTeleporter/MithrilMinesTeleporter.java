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
package org.l2junity.scripts.ai.individual.MithrilMines.MithrilMinesTeleporter;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Mithril Mines teleport AI.
 * @author Charus
 */
public final class MithrilMinesTeleporter extends AbstractNpcAI
{
	// NPC
	private final static int TELEPORT_CRYSTAL = 32652;
	// Location
	private static final Location[] LOCS =
	{
		new Location(171946, -173352, 3440),
		new Location(175499, -181586, -904),
		new Location(173462, -174011, 3480),
		new Location(179299, -182831, -224),
		new Location(178591, -184615, -360),
		new Location(175499, -181586, -904)
	};
	
	private MithrilMinesTeleporter()
	{
		addStartNpc(TELEPORT_CRYSTAL);
		addFirstTalkId(TELEPORT_CRYSTAL);
		addTalkId(TELEPORT_CRYSTAL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		int index = Integer.parseInt(event) - 1;
		if (LOCS.length > index)
		{
			Location loc = LOCS[index];
			player.teleToLocation(loc, false);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (npc.isInRadius2d(173147, -173762, Npc.INTERACTION_DISTANCE))
		{
			return "32652-01.htm";
		}
		
		if (npc.isInRadius2d(181941, -174614, Npc.INTERACTION_DISTANCE))
		{
			return "32652-02.htm";
		}
		
		if (npc.isInRadius2d(179560, -182956, Npc.INTERACTION_DISTANCE))
		{
			return "32652-03.htm";
		}
		return super.onFirstTalk(npc, player);
	}
	
	@GameScript
	public static void main()
	{
		new MithrilMinesTeleporter();
	}
}
