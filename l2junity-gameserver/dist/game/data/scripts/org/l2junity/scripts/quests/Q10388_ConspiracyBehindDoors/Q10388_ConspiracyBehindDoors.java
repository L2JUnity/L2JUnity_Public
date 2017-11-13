/*
 * Copyright (C) 2004-2017 L2J Unity
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
package org.l2junity.scripts.quests.Q10388_ConspiracyBehindDoors;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Conspiracy Behind Doors (10388)
 * @author netvirus
 */

public class Q10388_ConspiracyBehindDoors extends Quest
{
	// NPCs
	private static final int ELIYAH = 31329;
	private static final int KARGOS = 33821;
	private static final int KITCHEN = 33820;
	private static final int RAZEN = 33803;
	// Items
	private static final int VISITORS_BADGE = 8064;
	private static final int FADED_VISITORS_BADGE = 8065;
	private static final int PAGANS_BADGE = 8067;
	private static final int MARK_OF_KARGOS = 36227;
	private static final int VISITORS_BADGE1 = 36228;
	// Misc
	private static final int MIN_LVL = 97;
	
	public Q10388_ConspiracyBehindDoors()
	
	{
		super(10388);
		addStartNpc(ELIYAH);
		addTalkId(ELIYAH, KARGOS, KITCHEN, RAZEN);
		addCondMinLevel(MIN_LVL, "31329-02.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		final long pagansBadge = getQuestItemsCount(player, PAGANS_BADGE);
		final long fadedVisitorsBadge = getQuestItemsCount(player, FADED_VISITORS_BADGE);
		final long visitorsBadge = getQuestItemsCount(player, VISITORS_BADGE);
		switch (event)
		{
			case "33803-02.html":
			{
				if (st.isCond(3))
				{
					htmltext = event;
				}
				break;
			}
			case "33803-03.html":
			{
				if (st.isCond(3))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						addExp(player, 29638350);
						addSp(player, 7113);
						giveAdena(player, 65136, true);
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
			case "33820-02.html":
			{
				if (st.isCond(2))
				{
					htmltext = event;
					giveItems(player, VISITORS_BADGE1, 1);
					takeItems(player, MARK_OF_KARGOS, 1);
					st.setCond(3, true);
				}
				break;
			}
			case "33821-03":
			{
				if (st.isCond(1))
				{
					if ((pagansBadge == 0) && (fadedVisitorsBadge == 0) && (visitorsBadge == 0))
					{
						htmltext = "33821-03.html";
					}
					else if ((pagansBadge >= 1) || (fadedVisitorsBadge >= 1) || (visitorsBadge >= 1))
					{
						htmltext = "33821-03a.html";
						giveItems(player, VISITORS_BADGE1, 1);
						giveAdena(player, 65136, true);
						takeItems(player, VISITORS_BADGE, 1);
						takeItems(player, FADED_VISITORS_BADGE, 1);
						st.setCond(2, true);
					}
				}
				break;
			}
			case "33821-04.html":
			{
				if (st.isCond(1) && (pagansBadge == 0) && (fadedVisitorsBadge == 0) && (visitorsBadge == 0))
				{
					htmltext = event;
				}
				break;
			}
			case "33821-05.html":
			{
				if (st.isCond(1) && (pagansBadge == 0) && (fadedVisitorsBadge == 0) && (visitorsBadge == 0))
				{
					htmltext = event;
					giveItems(player, MARK_OF_KARGOS, 1);
					st.setCond(2, true);
				}
				break;
			}
			case "31329-04":
			{
				switch (getRandom(3))
				{
					case 0:
					{
						htmltext = "31329-04a.htm";
						break;
					}
					case 1:
					{
						htmltext = "31329-04b.htm";
						break;
					}
					case 2:
					{
						htmltext = "31329-04c.htm";
						break;
					}
				}
				break;
			}
			case "31329-05.htm":
			case "31329-06.htm":
			case "31329-07.htm":
			{
				htmltext = event;
				break;
			}
			case "31329-08.htm":
			{
				st.startQuest();
				htmltext = event;
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
		
		switch (npc.getId())
		{
			case ELIYAH:
			{
				switch (st.getState())
				{
					case State.COMPLETED:
					{
						htmltext = "31329-03.html";
						break;
					}
					case State.CREATED:
					{
						htmltext = "31329-01.htm";
						break;
					}
					case State.STARTED:
					{
						if (st.isCond(1))
						{
							htmltext = "31329-09.html";
						}
						break;
					}
				}
				break;
			}
			case KARGOS:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "33821-02.html";
						break;
					}
					case 2:
					{
						htmltext = "33821-06.html";
						break;
					}
				}
				break;
			}
			case KITCHEN:
			{
				switch (st.getCond())
				{
					case 2:
					{
						htmltext = "33820-01.html";
						break;
					}
					case 3:
					{
						htmltext = "33820-03.html";
						break;
					}
				}
				break;
			}
			case RAZEN:
			{
				if (st.isCond(3))
				{
					htmltext = "33803-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
