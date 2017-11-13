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
package org.l2junity.scripts.quests.Q00033_MakeAPairOfDressShoes;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Make a Pair of Dress Shoes (33)
 * @author malyelfik, Gladicek
 */
public class Q00033_MakeAPairOfDressShoes extends Quest
{
	// NPCs
	private static final int IAN = 30164;
	private static final int WOODLEY = 30838;
	private static final int LEIKAR = 31520;
	// Items
	private static final int WORN_LEATHER = 36516;
	private static final int ACCESSORY_GEM_LOW_GRADE = 36556;
	private static final int DRESS_SHOES_BOX = 7113;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int WORN_LEATHER_COUNT = 360;
	private static final int ACCESSORY_GEM_LOW_GRADE_COUNT = 90;
	private static final int ADENA_COUNT = 500000;
	private static final int ADENA_COUNT2 = 200000;
	private static final int ADENA_COUNT3 = 300000;
	
	public Q00033_MakeAPairOfDressShoes()
	{
		super(33);
		addStartNpc(WOODLEY);
		addTalkId(WOODLEY, IAN, LEIKAR);
		addCondMinLevel(MIN_LEVEL, "30838-02.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30838-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30838-06.html":
			{
				if (st.isCond(2))
				{
					st.setCond(3, true);
				}
				break;
			}
			case "30838-09.html":
			{
				if (st.isCond(3))
				{
					if ((getQuestItemsCount(player, WORN_LEATHER) >= WORN_LEATHER_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM_LOW_GRADE) >= ACCESSORY_GEM_LOW_GRADE_COUNT) && (player.getAdena() >= ADENA_COUNT2))
					{
						takeItems(player, WORN_LEATHER, WORN_LEATHER_COUNT);
						takeItems(player, ACCESSORY_GEM_LOW_GRADE, ACCESSORY_GEM_LOW_GRADE_COUNT);
						takeItems(player, Inventory.ADENA_ID, ADENA_COUNT2);
						st.setCond(4, true);
					}
					else
					{
						htmltext = "30838-10.html";
					}
					break;
				}
				break;
			}
			case "30838-13.html":
			{
				if (st.isCond(5))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveItems(player, DRESS_SHOES_BOX, 1);
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
			case "31520-02.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			}
			case "30164-02.html":
			{
				if (st.isCond(4))
				{
					if (player.getAdena() < ADENA_COUNT3)
					{
						htmltext = "30164-03.html";
					}
					takeItems(player, Inventory.ADENA_ID, ADENA_COUNT3);
					st.setCond(5, true);
				}
				break;
			}
			default:
				htmltext = null;
				break;
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
				if (npc.getId() == WOODLEY)
				{
					htmltext = "30838-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case WOODLEY:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30838-04.html";
								break;
							case 2:
								htmltext = "30838-05.html";
								break;
							case 3:
								htmltext = ((getQuestItemsCount(player, WORN_LEATHER) >= WORN_LEATHER_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM_LOW_GRADE) >= ACCESSORY_GEM_LOW_GRADE_COUNT) && (player.getAdena() >= ADENA_COUNT)) ? "30838-07.html" : "30838-08.html";
								break;
							case 4:
								htmltext = "30838-11.html";
								break;
							case 5:
								htmltext = "30838-12.html";
								break;
						}
						break;
					}
					case LEIKAR:
					{
						if (st.isCond(1))
						{
							htmltext = "31520-01.html";
						}
						else if (st.isCond(2))
						{
							htmltext = "31520-03.html";
						}
						break;
					}
					case IAN:
					{
						if (st.isCond(4))
						{
							htmltext = "30164-01.html";
						}
						else if (st.isCond(5))
						{
							htmltext = "30164-04.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}