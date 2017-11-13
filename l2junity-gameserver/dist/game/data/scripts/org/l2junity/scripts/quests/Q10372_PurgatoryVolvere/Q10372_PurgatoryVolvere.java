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
package org.l2junity.scripts.quests.Q10372_PurgatoryVolvere;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q10371_GraspThyPower.Q10371_GraspThyPower;

/**
 * Purgatory Volvere (10372)
 * @author netvirus
 */
public class Q10372_PurgatoryVolvere extends Quest
{
	// NPCs
	private static final int GERKENSHTEIN = 33648;
	private static final int ANDREI = 31292;
	// Monsters
	private static final int BLOODY_SUCCUBUS = 23185;
	// Items
	private static final int SUCCUBUS_ESSENCE = 34766;
	private static final int GERKENSHTEINS_REPORT = 34767;
	// Reward
	private static final int[] CRYSTALS =
	{
		22641, // Fire Crystal
		22642, // Water Crystal
		22643, // Earth Crystal
		22644, // Wind Crystal
		22645, // Dark Crystal
		22646 // Holy Crystal
	};
	// Misc
	private static final int MIN_LVL = 76;
	private static final int MAX_LVL = 81;
	
	public Q10372_PurgatoryVolvere()
	{
		super(10372);
		addStartNpc(GERKENSHTEIN);
		addTalkId(GERKENSHTEIN, ANDREI);
		addKillId(BLOODY_SUCCUBUS);
		registerQuestItems(SUCCUBUS_ESSENCE, GERKENSHTEINS_REPORT);
		addCondLevel(MIN_LVL, MAX_LVL, "33648-02.htm");
		addCondCompletedQuest(Q10371_GraspThyPower.class.getSimpleName(), "");
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
		
		switch (event)
		{
			case "31292-02.html":
			case "31292-03.html":
			{
				if (st.isCond(3))
				{
					htmltext = event;
				}
				break;
			}
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			{
				if (st.isCond(3))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						htmltext = "31292-04.html";
						final int index = Integer.parseInt(event);
						if ((index >= 0) && (index < CRYSTALS.length))
						{
							giveItems(player, CRYSTALS[index], 1);
						}
						addExp(player, 23009000);
						addSp(player, 5522);
						st.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
			case "33648-04.htm":
			case "33648-05.htm":
			{
				htmltext = event;
				break;
			}
			case "33648-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		final int npcId = npc.getId();
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				if (npcId == ANDREI)
				{
					htmltext = "31292-05.html";
				}
				break;
			}
			case State.CREATED:
			{
				if (npcId == GERKENSHTEIN)
				{
					htmltext = "33648-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npcId)
				{
					case GERKENSHTEIN:
					{
						switch (st.getCond())
						{
							case 1:
							{
								if (getQuestItemsCount(player, SUCCUBUS_ESSENCE) < 10)
								{
									htmltext = "33648-07.html";
								}
								break;
							}
							case 2:
							{
								if (!isSimulated)
								{
									st.setCond(3, true);
									takeItems(player, SUCCUBUS_ESSENCE, 10);
									giveItems(player, GERKENSHTEINS_REPORT, 1);
								}
								htmltext = "33648-08.html";
								break;
							}
							case 3:
							{
								htmltext = "33648-09.html";
								break;
							}
						}
						break;
					}
					case ANDREI:
					{
						if (st.isCond(3))
						{
							htmltext = "31292-01.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
		
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((st != null) && !st.isCompleted() && st.isCond(1))
		{
			final PlayerInstance player = st.getPlayer();
			if (giveItemRandomly(player, npc, SUCCUBUS_ESSENCE, 1, 10, 1, true))
			{
				st.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}