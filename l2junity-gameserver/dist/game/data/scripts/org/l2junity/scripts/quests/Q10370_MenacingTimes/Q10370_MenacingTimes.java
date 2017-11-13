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
package org.l2junity.scripts.quests.Q10370_MenacingTimes;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q10341_DayOfDestinyHumansFate.Q10341_DayOfDestinyHumansFate;
import org.l2junity.scripts.quests.Q10342_DayOfDestinyElvenFate.Q10342_DayOfDestinyElvenFate;
import org.l2junity.scripts.quests.Q10343_DayOfDestinyDarkElfsFate.Q10343_DayOfDestinyDarkElfsFate;
import org.l2junity.scripts.quests.Q10344_DayOfDestinyOrcsFate.Q10344_DayOfDestinyOrcsFate;
import org.l2junity.scripts.quests.Q10345_DayOfDestinyDwarfsFate.Q10345_DayOfDestinyDwarfsFate;
import org.l2junity.scripts.quests.Q10346_DayOfDestinyKamaelsFate.Q10346_DayOfDestinyKamaelsFate;

/**
 * Menacing Times (10370)
 * @author netvirus
 */
public class Q10370_MenacingTimes extends Quest
{
	// NPCs
	private static final int ORVEN = 30857;
	private static final int WINONIN = 30856;
	private static final int OLTRAN = 30862;
	private static final int LADANZA = 30865;
	private static final int FERRIS = 30847;
	private static final int BROME = 32221;
	private static final int ANDREI = 31292;
	private static final int GERKENSHTEIN = 33648;
	// Items
	private static final int REMNANT_ASHES = 34765;
	// Monsters
	private static final Map<Integer, Double> MONSTER_DROP_CHANCES = new HashMap<>();
	static
	{
		MONSTER_DROP_CHANCES.put(21646, 0.187); // Scavenger Scarab
		MONSTER_DROP_CHANCES.put(21647, 0.222); // Grave Scarab
		MONSTER_DROP_CHANCES.put(21648, 0.205); // Grave Ant
		MONSTER_DROP_CHANCES.put(21649, 0.242); // Scavenger Ant
		MONSTER_DROP_CHANCES.put(21650, 0.222); // Shrine Knight
		MONSTER_DROP_CHANCES.put(21651, 0.212); // Shrine Royal Guard
	}
	// Misc
	private static final int MIN_LVL = 76;
	private static final int MAX_LVL = 81;
	
	public Q10370_MenacingTimes()
	{
		super(10370);
		addStartNpc(ORVEN, WINONIN, OLTRAN, LADANZA, FERRIS, BROME);
		addTalkId(ORVEN, WINONIN, OLTRAN, LADANZA, FERRIS, BROME, ANDREI, GERKENSHTEIN);
		addKillId(MONSTER_DROP_CHANCES.keySet());
		registerQuestItems(REMNANT_ASHES);
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "");
		
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30847-06.htm":
			case "30856-06.htm":
			case "30857-06.htm":
			case "30862-06.htm":
			case "30865-06.htm":
			case "31292-02.html":
			case "32221-06.htm":
			{
				htmltext = event;
				break;
			}
			case "30847-07.htm":
			case "30856-07.htm":
			case "30857-07.htm":
			case "30862-07.htm":
			case "30865-07.htm":
			case "32221-07.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "31292-03.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33648-02.html":
			{
				if (st.isCond(2))
				{
					st.setCond(3, true);
					htmltext = event;
				}
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
				switch (npcId)
				{
					case ORVEN:
					{
						if (player.isInCategory(CategoryType.HUMAN_FOURTH_CLASS_GROUP))
						{
							htmltext = "30857-05.html";
						}
						break;
					}
					case WINONIN:
					{
						if (player.isInCategory(CategoryType.ELF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30856-05.html";
						}
						break;
					}
					case OLTRAN:
					{
						if (player.isInCategory(CategoryType.DARKELF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30862-05.html";
						}
						break;
					}
					case LADANZA:
					{
						if (player.isInCategory(CategoryType.ORC_FOURTH_CLASS_GROUP))
						{
							htmltext = "30865-05.html";
						}
						break;
					}
					case FERRIS:
					{
						if (player.isInCategory(CategoryType.DWARF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30847-05.html";
						}
						break;
					}
					case BROME:
					{
						if (player.isInCategory(CategoryType.KAMAEL_FOURTH_CLASS_GROUP))
						{
							htmltext = "32221-05.html";
						}
						break;
					}
					case GERKENSHTEIN:
					{
						htmltext = "33648-03.html";
						break;
					}
				}
				break;
			}
			case State.CREATED:
			{
				switch (npcId)
				{
					case ORVEN:
					{
						if (player.isInCategory(CategoryType.HUMAN_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10341_DayOfDestinyHumansFate.class.getSimpleName()) ? "30857-01.htm" : "30857-02.htm";
							}
							else
							{
								htmltext = "30857-03.htm";
							}
						}
						else
						{
							htmltext = "30857-04.htm";
						}
						break;
					}
					case WINONIN:
					{
						if (player.isInCategory(CategoryType.ELF_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10342_DayOfDestinyElvenFate.class.getSimpleName()) ? "30856-01.htm" : "30856-02.htm";
							}
							else
							{
								htmltext = "30856-03.htm";
							}
						}
						else
						{
							htmltext = "30856-04.htm";
						}
						break;
					}
					case OLTRAN:
					{
						if (player.isInCategory(CategoryType.DARKELF_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10343_DayOfDestinyDarkElfsFate.class.getSimpleName()) ? "30862-01.htm" : "30862-02.htm";
							}
							else
							{
								htmltext = "30862-03.htm";
							}
						}
						else
						{
							htmltext = "30862-04.htm";
						}
						break;
					}
					case LADANZA:
					{
						if (player.isInCategory(CategoryType.ORC_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10344_DayOfDestinyOrcsFate.class.getSimpleName()) ? "30865-01.htm" : "30865-02.htm";
							}
							else
							{
								htmltext = "30865-03.htm";
							}
						}
						else
						{
							htmltext = "30865-04.htm";
						}
						break;
					}
					case FERRIS:
					{
						if (player.isInCategory(CategoryType.DWARF_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10345_DayOfDestinyDwarfsFate.class.getSimpleName()) ? "30847-01.htm" : "30847-02.htm";
							}
							else
							{
								htmltext = "30847-03.htm";
							}
						}
						else
						{
							htmltext = "30847-04.htm";
						}
						break;
					}
					case BROME:
					{
						if (player.isInCategory(CategoryType.KAMAEL_FOURTH_CLASS_GROUP))
						{
							if (checkLevel(player))
							{
								htmltext = player.hasQuestCompleted(Q10346_DayOfDestinyKamaelsFate.class.getSimpleName()) ? "32221-01.htm" : "32221-02.htm";
							}
							else
							{
								htmltext = "32221-03.htm";
							}
						}
						else
						{
							htmltext = "32221-04.htm";
						}
						break;
					}
					default:
					{
						htmltext = getNoQuestMsg(player);
						break;
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npcId)
				{
					case ORVEN:
					{
						if (player.isInCategory(CategoryType.HUMAN_FOURTH_CLASS_GROUP))
						{
							htmltext = "30857-08.html";
						}
						break;
					}
					case WINONIN:
					{
						if (player.isInCategory(CategoryType.ELF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30856-08.html";
						}
						break;
					}
					case OLTRAN:
					{
						if (player.isInCategory(CategoryType.DARKELF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30862-08.html";
						}
						break;
					}
					case LADANZA:
					{
						if (player.isInCategory(CategoryType.ORC_FOURTH_CLASS_GROUP))
						{
							htmltext = "30865-08.html";
						}
						break;
					}
					case FERRIS:
					{
						if (player.isInCategory(CategoryType.DWARF_FOURTH_CLASS_GROUP))
						{
							htmltext = "30847-08.html";
						}
						break;
					}
					case BROME:
					{
						if (player.isInCategory(CategoryType.KAMAEL_FOURTH_CLASS_GROUP))
						{
							htmltext = "32221-08.html";
						}
						break;
					}
					case ANDREI:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "31292-01.html";
								break;
							}
							case 2:
							{
								htmltext = "31292-04.html";
								break;
							}
						}
						break;
					}
					case GERKENSHTEIN:
					{
						switch (st.getCond())
						{
							case 2:
							{
								htmltext = "33648-01.html";
								break;
							}
							case 3:
							{
								htmltext = "33648-04.html";
								break;
							}
							case 4:
							{
								if (!isSimulated)
								{
									if (player.getLevel() >= MIN_LVL)
									{
										addExp(player, 22451400);
										addSp(player, 5388);
										giveAdena(player, 479620, true);
										st.exitQuest(false, true);
									}
									else
									{
										htmltext = getNoQuestLevelRewardMsg(player);
										break;
									}
								}
								htmltext = "33648-05.html";
								break;
							}
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
		final QuestState st = getRandomPartyMemberState(killer, 3, 2, npc);
		if ((st != null) && !st.isCompleted() && (npc.distance3d(killer) <= 1500))
		{
			final PlayerInstance player = st.getPlayer();
			giveItemRandomly(player, npc, REMNANT_ASHES, 1, 0, MONSTER_DROP_CHANCES.get(npc.getId()), true);
			if (getQuestItemsCount(player, REMNANT_ASHES) >= 30)
			{
				st.setCond(4, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public boolean checkLevel(PlayerInstance player)
	{
		final int level = player.getLevel();
		if ((level >= MIN_LVL) && (level <= MAX_LVL))
		{
			return true;
		}
		return false;
	}
}