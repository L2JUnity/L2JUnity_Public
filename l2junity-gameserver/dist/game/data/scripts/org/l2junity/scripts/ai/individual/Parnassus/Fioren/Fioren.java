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
package org.l2junity.scripts.ai.individual.Parnassus.Fioren;

import org.l2junity.gameserver.enums.Movie;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.OnEventTrigger;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Fioren AI.
 * @author St3eT
 */
public final class Fioren extends AbstractNpcAI
{
	// NPCs
	private static final int FIOREN = 33044;
	// Misc
	private static final int BAYLOR_TRIGGER = 24230010;
	private static final int BALOK_TRIGGER = 24230012;
	
	private Fioren()
	{
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addFirstTalkId(FIOREN);
		addSeeCreatureId(FIOREN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("startMovie"))
		{
			playMovie(player, Movie.SI_BARLOG_STORY);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			creature.getActingPlayer().sendPacket(new OnEventTrigger(BAYLOR_TRIGGER, true));
			creature.getActingPlayer().sendPacket(new OnEventTrigger(BALOK_TRIGGER, true));
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@GameScript
	public static void main()
	{
		new Fioren();
	}
}