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
package org.l2junity.scripts.quests.Q00139_ShadowFoxPart1;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q00138_TempleChampionPart2.Q00138_TempleChampionPart2;

/**
 * Shadow Fox - 1 (139)
 * @author Nono, Gladicek
 */
public class Q00139_ShadowFoxPart1 extends Quest
{
	// NPC
	private static final int MIA = 30896;
	// Monsters
	private static final int MOBS[] =
	{
		20784, // Tasaba Lizardman
		20785, // Tasaba Lizardman Shaman
		21639, // Tasaba Lizardman
		21640, // Tasaba Lizardman Shaman
	};
	// Items
	private static final int FRAGMENT = 10345;
	private static final int CHEST = 10346;
	// Misc
	private static final int MIN_LEVEL = 37;
	private static final int MAX_REWARD_LEVEL = 42;
	
	public Q00139_ShadowFoxPart1()
	{
		super(139);
		addStartNpc(MIA);
		addTalkId(MIA);
		addKillId(MOBS);
		addCondMinLevel(MIN_LEVEL, "30896-03.htm");
		addCondCompletedQuest(Q00138_TempleChampionPart2.class.getSimpleName(), "30896-00.html");
		registerQuestItems(FRAGMENT, CHEST);
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
			case "30896-02.htm":
			case "30896-06.html":
			case "30896-07.html":
			case "30896-08.html":
			case "30896-09.html":
			case "30896-10.html":
			case "30896-12.html":
			case "30896-18.html":
			{
				htmltext = event;
				break;
			}
			case "30896-04.htm":
			{
				st.startQuest();
				break;
			}
			case "30896-11.html":
			{
				if (st.isMemoState(0))
				{
					st.setMemoState(1);
				}
				break;
			}
			case "30896-13.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					st.setMemoState(0);
				}
				break;
			}
			case "30896-17.html":
			{
				if (st.isMemoState(0))
				{
					if (getRandom(20) < 3)
					{
						takeItems(player, FRAGMENT, 10);
						takeItems(player, CHEST, 1);
						htmltext = "30896-16.html";
					}
					else
					{
						takeItems(player, FRAGMENT, -1);
						takeItems(player, CHEST, -1);
						st.setMemoState(1);
					}
					break;
				}
				break;
			}
			case "30896-19.html":
			{
				if ((st.isCond(2)) && (st.isMemoState(1)))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						giveAdena(player, 14050, true);
						if (player.getLevel() <= MAX_REWARD_LEVEL)
						{
							addExp(player, 30000);
							addSp(player, 20); // TODO: Retail value
						}
						st.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(player, 1, 3, npc);
		
		if ((st != null) && (st.isCond(2)) && (st.isMemoState(0)))
		{
			giveItemRandomly(st.getPlayer(), npc, Rnd.nextBoolean() ? CHEST : FRAGMENT, 1, 0, 0.68, true);
		}
		return super.onKill(npc, player, isSummon);
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
				htmltext = "30896-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
						htmltext = (st.isMemoState(1)) ? "30896-11.html" : "30896-05.html";
						break;
					case 2:
						htmltext = (st.isMemoState(1)) ? "30896-18.html" : ((getQuestItemsCount(player, FRAGMENT) >= 10) && (getQuestItemsCount(player, CHEST) >= 1)) ? "30896-15.html" : "30896-14.html";
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