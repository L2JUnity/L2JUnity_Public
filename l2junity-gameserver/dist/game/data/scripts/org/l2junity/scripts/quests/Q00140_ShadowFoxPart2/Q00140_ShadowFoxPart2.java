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
package org.l2junity.scripts.quests.Q00140_ShadowFoxPart2;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q00139_ShadowFoxPart1.Q00139_ShadowFoxPart1;

/**
 * Shadow Fox - 2 (140)
 * @author Nono, Gladicek
 */
public class Q00140_ShadowFoxPart2 extends Quest
{
	// NPCs
	private static final int KLUCK = 30895;
	private static final int XENOVIA = 30912;
	// Items
	private static final int DARK_CRYSTAL = 10347;
	private static final int DARK_OXYDE = 10348;
	private static final int CRYPTOGRAM_OF_THE_GODDESS_SWORD = 10349;
	// Monsters
	private static final Map<Integer, Double> MOBS = new HashMap<>();
	
	static
	{
		MOBS.put(20789, 0.45); // Crokian
		MOBS.put(20790, 0.58); // Dailaon
		MOBS.put(20791, 1.0);// Crokian Warrior
		MOBS.put(20792, 0.92); // Farhite
	}
	
	// Misc
	private static final int MIN_LEVEL = 37;
	private static final int MAX_REWARD_LEVEL = 42;
	private static final int CHANCE = 8;
	private static final int CRYSTAL_COUNT = 5;
	
	public Q00140_ShadowFoxPart2()
	{
		super(140);
		addStartNpc(KLUCK);
		addTalkId(KLUCK, XENOVIA);
		addKillId(MOBS.keySet());
		addCondMinLevel(MIN_LEVEL, "30896-03.htm");
		addCondCompletedQuest(Q00139_ShadowFoxPart1.class.getSimpleName(), "30896-00.html");
		registerQuestItems(DARK_CRYSTAL, DARK_OXYDE, CRYPTOGRAM_OF_THE_GODDESS_SWORD);
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
			case "30895-05.html":
			case "30895-06.html":
			case "30912-03.html":
			case "30912-04.html":
			case "30912-05.html":
			case "30912-08.html":
			case "30895-10.html":
			{
				break;
			}
			case "30895-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30895-07.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			}
			case "30912-06.html":
			{
				if (st.isMemoState(0))
				{
					st.setMemoState(1);
				}
				break;
			}
			case "30912-09.html":
			{
				if (st.isCond(2))
				{
					st.setMemoState(0);
					st.setCond(3, true);
				}
				break;
			}
			case "30912-14.html":
			{
				if ((st.isCond(3)) && (getQuestItemsCount(player, DARK_CRYSTAL) >= 5))
				{
					takeItems(player, DARK_CRYSTAL, 5);
					
					if (getRandom(10) < CHANCE)
					{
						if (hasQuestItems(player, DARK_OXYDE))
						{
							st.setCond(4, true);
							giveItems(player, CRYPTOGRAM_OF_THE_GODDESS_SWORD, 1);
							takeItems(player, DARK_CRYSTAL, -1);
							takeItems(player, DARK_OXYDE, -1);
							htmltext = "30912-13.html";
						}
						else
						{
							giveItems(player, DARK_OXYDE, 1);
							htmltext = "30912-12.html";
						}
						break;
					}
				}
				break;
			}
			case "30895-11.html":
			{
				if ((st.isCond(4)) && (st.isMemoState(1)))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						giveAdena(player, 18775, true);
						
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
					break;
				}
			}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(player, 1, 3, npc);
		
		if ((st != null) && (st.isCond(3)))
		{
			giveItemRandomly(st.getPlayer(), npc, DARK_CRYSTAL, 1, 0, MOBS.get(npc.getId()), true);
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
				if (npc.getId() == KLUCK)
				{
					htmltext = "30895-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KLUCK:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30895-04.html";
								break;
							case 2:
							case 3:
								htmltext = "30895-08.html";
								break;
							case 4:
							{
								if (st.isMemoState(0))
								{
									takeItems(player, CRYPTOGRAM_OF_THE_GODDESS_SWORD, -1);
									st.setMemoState(1);
									htmltext = "30895-09.html";
								}
								else
								{
									htmltext = "30895-10.html";
								}
								break;
							}
						}
						break;
					}
					case XENOVIA:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30912-01.html";
								break;
							case 2:
								htmltext = (st.isMemoState(1)) ? "30912-07.html" : "30912-02.html";
								break;
							case 3:
								htmltext = (getQuestItemsCount(player, DARK_CRYSTAL) >= CRYSTAL_COUNT) ? "30912-11.html" : "30912-10.html";
								break;
							case 4:
								htmltext = "30912-15.html";
								break;
						}
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