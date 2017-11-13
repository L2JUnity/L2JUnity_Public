/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.scripts.ai.individual.DragonValley.SeparatedSoul;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Separated Soul AI.
 * @author UnAfraid, Adry_85, Gladicek
 */
public final class SeparatedSoul extends AbstractNpcAI
{
	// Npc
	private static final int SEPARATED_SOULS = 32864;
	// Items
	private static final int WILL_OF_ANTHARAS = 17266;
	private static final int SEALED_BLOOD_CRYSTAL = 17267;
	private static final int ANTHARAS_BLOOD_CRYSTAL = 17268;
	
	private SeparatedSoul()
	{
		addFirstTalkId(SEPARATED_SOULS);
		addStartNpc(SEPARATED_SOULS);
		addTalkId(SEPARATED_SOULS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "32864-1.html":
			{
				htmltext = event;
				break;
			}
			case "synthesis":
			{
				if (hasQuestItems(player, WILL_OF_ANTHARAS, SEALED_BLOOD_CRYSTAL))
				{
					takeItems(player, WILL_OF_ANTHARAS, 1);
					takeItems(player, SEALED_BLOOD_CRYSTAL, 1);
					giveItems(player, ANTHARAS_BLOOD_CRYSTAL, 1);
				}
				else
				{
					htmltext = "no-items.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@GameScript
	public static void main()
	{
		new SeparatedSoul();
	}
}