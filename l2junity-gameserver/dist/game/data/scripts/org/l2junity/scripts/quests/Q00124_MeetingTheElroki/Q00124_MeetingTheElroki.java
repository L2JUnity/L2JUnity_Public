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
package org.l2junity.scripts.quests.Q00124_MeetingTheElroki;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Meeting the Elroki (124)
 * @author Adry_85
 */
public class Q00124_MeetingTheElroki extends Quest
{
	// NPCs
	private static final int MARQUEZ = 32113;
	private static final int MUSHIKA = 32114;
	private static final int ASAMAH = 32115;
	private static final int KARAKAWEI = 32117;
	private static final int MANTARASA = 32118;
	// Item
	private static final int MANTARASA_EGG = 8778;
	// Misc
	private static final int MIN_LEVEL = 75;
	
	public Q00124_MeetingTheElroki()
	{
		super(124);
		addStartNpc(MARQUEZ);
		addTalkId(MARQUEZ, MUSHIKA, ASAMAH, KARAKAWEI, MANTARASA);
		addCondMinLevel(MIN_LEVEL, "32113-01a.htm");
		registerQuestItems(MANTARASA_EGG);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "32113-03.html":
				st.startQuest();
				break;
			case "32113-04.html":
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			case "32114-04.html":
				if (st.isCond(2))
				{
					st.setCond(3, true);
				}
				break;
			case "32115-06.html":
				if (st.isCond(3))
				{
					st.setCond(4, true);
				}
				break;
			case "32117-05.html":
				if (st.isCond(4))
				{
					st.setCond(5, true);
				}
				break;
			case "32118-04.html":
				if (st.isCond(5))
				{
					giveItems(player, MANTARASA_EGG, 1);
					st.setCond(6, true);
				}
				break;
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
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
				if (npc.getId() == MARQUEZ)
				{
					htmltext = "32113-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case MARQUEZ:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "32113-05.html";
								break;
							case 2:
								htmltext = "32113-06.html";
								break;
							case 3:
							case 4:
							case 5:
								htmltext = "32113-07.html";
								break;
						}
						break;
					}
					case MUSHIKA:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "32114-01.html";
								break;
							case 2:
								htmltext = "32114-02.html";
								break;
							default:
								htmltext = "32114-03.html";
								break;
						}
						break;
					}
					case ASAMAH:
					{
						switch (st.getCond())
						{
							case 1:
							case 2:
								htmltext = "32115-01.html";
								break;
							case 3:
								htmltext = "32115-02.html";
								break;
							case 4:
								htmltext = "32115-07.html";
								break;
							case 5:
								htmltext = "32115-08.html";
								break;
							case 6:
							{
								if (!isSimulated)
								{
									if ((player.getLevel() >= MIN_LEVEL))
									{
										if (hasQuestItems(player, MANTARASA_EGG))
										{
											addExp(player, 1_795_524);
											addSp(player, 79);
											st.exitQuest(false, true);
										}
									}
									else
									{
										htmltext = getNoQuestLevelRewardMsg(player);
									}
									break;
								}
								htmltext = "32115-09.html";
								break;
							}
						}
						break;
					}
					case KARAKAWEI:
					{
						switch (st.getCond())
						{
							case 1:
							case 2:
							case 3:
								htmltext = "32117-01.html";
								break;
							case 4:
								htmltext = "32117-02.html";
								break;
							case 5:
								htmltext = "32117-07.html";
								break;
							case 6:
								htmltext = "32117-06.html";
								break;
						}
						break;
					}
					case MANTARASA:
					{
						switch (st.getCond())
						{
							case 1:
							case 2:
							case 3:
							case 4:
								htmltext = "32118-01.html";
								break;
							case 5:
								htmltext = "32118-03.html";
								break;
							case 6:
								htmltext = "32118-02.html";
								break;
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
}
