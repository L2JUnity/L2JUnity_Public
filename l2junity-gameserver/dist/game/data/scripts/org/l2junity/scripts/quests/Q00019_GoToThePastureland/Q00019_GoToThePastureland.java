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
package org.l2junity.scripts.quests.Q00019_GoToThePastureland;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Go to the Pastureland (19)
 * @author malyelfik
 */
public final class Q00019_GoToThePastureland extends Quest
{
	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int TUNATUN = 31537;
	// Items
	private static final int VEAL = 15532;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public Q00019_GoToThePastureland()
	{
		super(19);
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, TUNATUN);
		registerQuestItems(VEAL);
		addCondMinLevel(MIN_LEVEL, "31302-03.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equalsIgnoreCase("31302-02.htm"))
		{
			st.startQuest();
			giveItems(player, VEAL, 1);
		}
		else if (event.equalsIgnoreCase("31537-02.htm"))
		{
			if (hasQuestItems(player, VEAL))
			{
				if ((player.getLevel() >= MIN_LEVEL))
				{
					giveAdena(player, 299_928, true);
					addExp(player, 1_456_218);
					addSp(player, 349);
					st.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
				}
			}
			else
			{
				htmltext = "31537-03.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (npc.getId() == VLADIMIR)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = "31302-01.html";
					break;
				}
				case State.STARTED:
				{
					htmltext = "31302-04.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if ((npc.getId() == TUNATUN) && (st.isCond(1)))
		{
			htmltext = "31537-01.html";
		}
		return htmltext;
	}
}
