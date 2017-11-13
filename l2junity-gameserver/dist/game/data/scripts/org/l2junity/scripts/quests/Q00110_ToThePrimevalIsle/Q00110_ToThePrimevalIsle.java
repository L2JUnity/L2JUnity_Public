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
package org.l2junity.scripts.quests.Q00110_ToThePrimevalIsle;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * To the Primeval Isle (110)
 * @author Adry_85, Gladicek
 */
public class Q00110_ToThePrimevalIsle extends Quest
{
	// NPCs
	private static final int ANTON = 31338;
	private static final int MARQUEZ = 32113;
	// Item
	private static final int ANCIENT_BOOK = 8777;
	// Misc
	private static final int MIN_LEVEL = 75;
	
	public Q00110_ToThePrimevalIsle()
	{
		super(110);
		addStartNpc(ANTON);
		addTalkId(ANTON, MARQUEZ);
		addCondMinLevel(MIN_LEVEL, "");
		registerQuestItems(ANCIENT_BOOK);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = null;
		
		switch (event)
		{
			case "31338-03.htm":
			case "31338-04.htm":
			case "32113-02.html":
			case "32113-03.html":
			{
				htmltext = event;
				break;
			}
			case "31338-05.html":
			{
				giveItems(player, ANCIENT_BOOK, 1);
				st.startQuest();
				break;
			}
			case "32113-04.html":
			case "32113-05.html":
			{
				if (st.isCond(1))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveAdena(player, 189_208, true);
						addExp(player, 887_732);
						addSp(player, 213);
						st.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
					break;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == ANTON)
				{
					htmltext = "31338-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == ANTON)
				{
					if (st.isCond(1))
					{
						htmltext = "32113-06.html";
					}
					break;
				}
				else if (npc.getId() == MARQUEZ)
				{
					if (st.isCond(1))
					{
						htmltext = "32113-01.html";
					}
					break;
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
