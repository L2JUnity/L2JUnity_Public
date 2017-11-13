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
package org.l2junity.scripts.quests.Q00119_LastImperialPrince;

import org.l2junity.gameserver.enums.QuestType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Last Imperial Prince (119)
 * @author Adry_85
 */
public class Q00119_LastImperialPrince extends Quest
{
	// NPCs
	private static final int NAMELESS_SPIRIT = 31453;
	private static final int DEVORIN = 32009;
	// Item
	private static final int ANTIQUE_BROOCH = 7262;
	// Misc
	private static final int MIN_LEVEL = 74;
	
	public Q00119_LastImperialPrince()
	{
		super(119);
		addStartNpc(NAMELESS_SPIRIT);
		addTalkId(NAMELESS_SPIRIT, DEVORIN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31453-02.htm":
			case "31453-03.htm":
			case "31453-10.html":
			{
				htmltext = event;
				break;
			}
			case "31453-04.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "31453-11.html":
			{
				if (st.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveAdena(player, 407_970, true);
						addExp(player, 1_919_448);
						addSp(player, 460);
						st.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
			case "brooch":
			{
				htmltext = (hasQuestItems(player, ANTIQUE_BROOCH)) ? "32009-02.html" : "32009-03.html";
				break;
			}
			case "32009-04.html":
			{
				if (st.isCond(1) && hasQuestItems(player, ANTIQUE_BROOCH))
				{
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				if (npc.getId() == NAMELESS_SPIRIT)
				{
					htmltext = "31453-06.html";
				}
				break;
			}
			case State.CREATED:
			{
				htmltext = ((player.getLevel() >= MIN_LEVEL) && hasQuestItems(player, ANTIQUE_BROOCH)) ? "31453-01.htm" : "31453-05.htm";
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NAMELESS_SPIRIT)
				{
					if (st.isCond(1))
					{
						if (hasQuestItems(player, ANTIQUE_BROOCH))
						{
							htmltext = "31453-07.html";
						}
						else
						{
							htmltext = "31453-08.html";
							st.exitQuest(QuestType.REPEATABLE);
						}
					}
					else if (st.isCond(2))
					{
						htmltext = "31453-09.html";
					}
				}
				else if (npc.getId() == DEVORIN)
				{
					if (st.isCond(1))
					{
						htmltext = "32009-01.html";
					}
					else if (st.isCond(2))
					{
						htmltext = "32009-05.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
