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
package org.l2junity.scripts.ai.individual.TownOfGludio.Acateo;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Acateo AI.
 * @author Gladicek
 */
public final class Acateo extends AbstractNpcAI
{
	// NPC
	private static final int ACATEO = 33905;
	// Item
	private static final int ACADEMY_CIRCLET = 8181;
	private static final int ENCHANCED_ACADEMY_CIRCLET = 47211;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	private Acateo()
	{
		addStartNpc(ACATEO);
		addFirstTalkId(ACATEO);
		addTalkId(ACATEO);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33905-01.html":
			case "33905-04.html":
			{
				htmltext = event;
				break;
			}
			case "give_circlet":
			{
				if (!hasQuestItems(player, ACADEMY_CIRCLET))
				{
					if (player.getLevel() < MIN_LEVEL)
					{
						giveItems(player, ACADEMY_CIRCLET, 1);
						htmltext = "33905-02.html";
					}
				}
				else
				{
					htmltext = "33905-03.html";
				}
				break;
			}
			case "upgrade":
			{
				if ((player.getLevel() >= MIN_LEVEL) && hasQuestItems(player, ACADEMY_CIRCLET))
				{
					giveItems(player, ENCHANCED_ACADEMY_CIRCLET, 1);
					takeItems(player, ACADEMY_CIRCLET, -1);
					htmltext = "33905-05.html";
				}
				else
				{
					htmltext = "33905-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return player.isAcademyMember() ? "33905-01.html" : "33905.html";
	}
	
	@GameScript
	public static void main()
	{
		new Acateo();
	}
}