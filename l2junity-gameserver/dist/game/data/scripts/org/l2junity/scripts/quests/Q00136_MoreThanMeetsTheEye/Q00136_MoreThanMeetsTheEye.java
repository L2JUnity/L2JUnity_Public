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
package org.l2junity.scripts.quests.Q00136_MoreThanMeetsTheEye;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * More Than Meets the Eye (136)
 * @author malyelfik, Gladicek
 */
public class Q00136_MoreThanMeetsTheEye extends Quest
{
	// Npcs
	private static final int HARDIN = 30832;
	private static final int ERRICKIN = 30701;
	private static final int CLAYTON = 30464;
	
	// Monsters
	private static final int GLASS_JAGUAR = 20250;
	private static final int GHOST1 = 20636;
	private static final int GHOST2 = 20637;
	private static final int GHOST3 = 20638;
	private static final int MIRROR = 20639;
	
	// Items
	private static final int ECTOPLASM = 9787;
	private static final int STABILIZED_ECTOPLASM = 9786;
	private static final int ORDER = 9788;
	private static final int GLASS_JAGUAR_CRYSTAL = 9789;
	private static final int BOOK_OF_SEAL = 9790;
	private static final int TRANSFORM_BOOK = 9648;
	
	// Misc
	private static final int MIN_LEVEL = 50;
	private static final int ECTOPLASM_COUNT = 35;
	private static final int CRYSTAL_COUNT = 5;
	
	private static final Map<Integer, Double> CHANCES_MAP = new HashMap<>();
	
	static
	{
		CHANCES_MAP.put(GHOST1, 0.0);
		CHANCES_MAP.put(GHOST2, 0.04);
		CHANCES_MAP.put(GHOST3, 0.09);
		CHANCES_MAP.put(MIRROR, 0.29);
	}
	
	public Q00136_MoreThanMeetsTheEye()
	{
		super(136);
		addStartNpc(HARDIN);
		addTalkId(HARDIN, ERRICKIN, CLAYTON);
		addKillId(GHOST1, GHOST2, GHOST3, GLASS_JAGUAR, MIRROR);
		addCondMinLevel(MIN_LEVEL, "30832-02.htm");
		registerQuestItems(ECTOPLASM, STABILIZED_ECTOPLASM, ORDER, GLASS_JAGUAR_CRYSTAL, BOOK_OF_SEAL);
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
			case "30832-05.html":
			case "30832-06.html":
			case "30832-12.html":
			case "30832-13.html":
			case "30832-18.html":
				break;
			case "30832-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30832-07.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			}
			case "30832-11.html":
			{
				st.set("talked", "2");
				break;
			}
			case "30832-14.html":
			{
				if (st.isCond(5))
				{
					st.isMemoState(0);
					giveItems(player, ORDER, 1);
					st.setCond(6, true);
					break;
				}
			}
			case "30832-17.html":
			{
				st.isMemoState(2);
				break;
			}
			case "30832-19.html":
			{
				if (st.isCond(9))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						giveItems(player, TRANSFORM_BOOK, 1);
						giveAdena(player, 67550, true);
						st.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
			case "30701-03.html":
			{
				if (st.isCond(2))
				{
					st.setCond(3, true);
				}
				break;
			}
			case "30464-03.html":
			{
				if ((st.isCond(6)) && hasQuestItems(player, ORDER))
				{
					takeItems(player, ORDER, -1);
					st.setCond(7, true);
					break;
				}
			}
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if (st != null)
		{
			if (st.isCond(3))
			{
				giveItemRandomly(st.getPlayer(), npc, ECTOPLASM, 1, ECTOPLASM_COUNT, 1.0, true);
				giveItemRandomly(st.getPlayer(), npc, ECTOPLASM, 1, ECTOPLASM_COUNT, CHANCES_MAP.get(npc.getId()), true);
				
				if ((getQuestItemsCount(killer, ECTOPLASM) >= ECTOPLASM_COUNT))
				{
					st.setCond(4, true);
				}
			}
			else if (st.isCond(7))
			{
				giveItemRandomly(st.getPlayer(), npc, GLASS_JAGUAR_CRYSTAL, 1, CRYSTAL_COUNT, 0.986, true);
				
				if ((getQuestItemsCount(killer, GLASS_JAGUAR_CRYSTAL) >= CRYSTAL_COUNT))
				{
					st.setCond(8, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == HARDIN)
				{
					htmltext = "30832-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HARDIN:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30832-04.html";
								break;
							case 2:
							case 3:
							case 4:
								htmltext = "30832-08.html";
								break;
							case 5:
							{
								if (!isSimulated)
								{
									if (st.isMemoState(1))
									{
										htmltext = "30832-10.html";
									}
									else if (st.isMemoState(2))
									{
										htmltext = "30832-12.html";
									}
									else if (hasQuestItems(player, STABILIZED_ECTOPLASM))
									{
										takeItems(player, STABILIZED_ECTOPLASM, -1);
										st.setMemoState(1);
										htmltext = "30832-09.html";
									}
									else
									{
										htmltext = "30832-08.html";
									}
									break;
								}
								break;
							}
							case 6:
							case 7:
							case 8:
								htmltext = "30832-15.html";
								break;
							case 9:
							{
								if (!isSimulated)
								{
									if (st.isMemoState(1))
									{
										st.setMemoState(2);
										htmltext = "30832-17.html";
									}
									else if (st.isMemoState(2))
									{
										htmltext = "30832-18.html";
									}
									else
									{
										takeItems(player, BOOK_OF_SEAL, -1);
										st.setMemoState(1);
										htmltext = "30832-16.html";
									}
									break;
								}
								break;
							}
						}
						break;
					}
					case ERRICKIN:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30701-01.html";
								break;
							case 2:
								htmltext = "30701-02.html";
								break;
							case 3:
								htmltext = "30701-04.html";
								break;
							case 4:
							{
								if (!isSimulated)
								{
									if (getQuestItemsCount(player, ECTOPLASM) < ECTOPLASM_COUNT)
									{
										giveItems(player, STABILIZED_ECTOPLASM, 1);
										st.setCond(5, true);
										htmltext = "30701-06.html";
									}
									else
									{
										takeItems(player, ECTOPLASM, -1);
										htmltext = "30701-05.html";
									}
									break;
								}
								break;
							}
							default:
								htmltext = "30701-07.html";
								break;
						}
						break;
					}
					case CLAYTON:
					{
						switch (st.getCond())
						{
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
								htmltext = "30464-01.html";
								break;
							case 6:
								htmltext = "30464-02.html";
								break;
							case 7:
								htmltext = "30464-04.html";
								break;
							case 8:
							{
								if (!isSimulated)
								{
									if (hasQuestItems(player, GLASS_JAGUAR_CRYSTAL))
									{
										giveItems(player, BOOK_OF_SEAL, 1);
										takeItems(player, GLASS_JAGUAR_CRYSTAL, -1);
										st.setCond(9, true);
										htmltext = "30464-05.html";
									}
									break;
								}
								break;
							}
							default:
								htmltext = "30464-06.html";
								break;
						}
						break;
					}
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