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
package org.l2junity.scripts.quests.Q00035_FindGlitteringJewelry;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Find Glittering Jewelry (35)
 * @author malyelfik, Gladicek
 */
public class Q00035_FindGlitteringJewelry extends Quest
{
	// NPCs
	private static final int ELLIE = 30091;
	private static final int FELTON = 30879;
	// Monster
	private static final int ALLIGATOR = 20135;
	// Items
	private static final int IRON_ORE = 36521;
	private static final int ARMOR_FRAGMENT_LOW_GRADE = 36551;
	private static final int ACCESORY_GEM_LOW_GRADE = 36556;
	private static final int JEWEL_BOX = 7077;
	private static final int ROUGH_JEWEL = 7162;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int JEWEL_COUNT = 10;
	private static final int IRON_ORE_COUNT = 95;
	private static final int ARMOR_FRAGMENT_COUNT = 405;
	private static final int ACCESORY_GEM_COUNT = 385;
	
	public Q00035_FindGlitteringJewelry()
	{
		super(35);
		addStartNpc(ELLIE);
		addTalkId(ELLIE, FELTON);
		addKillId(ALLIGATOR);
		addCondMinLevel(MIN_LEVEL, "30091-02.html");
		registerQuestItems(ROUGH_JEWEL);
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
			case "30091-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30879-02.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			}
			case "30091-07.html":
			{
				if (st.isCond(3))
				{
					if (getQuestItemsCount(player, ROUGH_JEWEL) < JEWEL_COUNT)
					{
						htmltext = "30091-08.html";
						break;
					}
					takeItems(player, ROUGH_JEWEL, -1);
					st.setCond(4, true);
				}
				break;
			}
			case "30091-11.html":
			{
				if (st.isCond(4))
				{
					if ((getQuestItemsCount(player, IRON_ORE) >= IRON_ORE_COUNT) && (getQuestItemsCount(player, ARMOR_FRAGMENT_LOW_GRADE) >= ARMOR_FRAGMENT_COUNT) && (getQuestItemsCount(player, ACCESORY_GEM_LOW_GRADE) >= ACCESORY_GEM_COUNT))
					{
						if ((player.getLevel() >= MIN_LEVEL))
						{
							takeItems(player, IRON_ORE, IRON_ORE_COUNT);
							takeItems(player, ARMOR_FRAGMENT_LOW_GRADE, ARMOR_FRAGMENT_COUNT);
							takeItems(player, ACCESORY_GEM_LOW_GRADE, ACCESORY_GEM_COUNT);
							giveItems(player, JEWEL_BOX, 1);
							st.exitQuest(false, true);
						}
						else
						{
							htmltext = getNoQuestLevelRewardMsg(player);
						}
					}
					else
					{
						htmltext = "30091-12.html";
					}
					break;
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
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final PlayerInstance member = getRandomPartyMember(player, 2);
		if (member != null)
		{
			final QuestState st = getQuestState(member, false);
			if ((st.isCond(2) && getRandomBoolean()))
			{
				giveItems(member, ROUGH_JEWEL, 1);
				if (getQuestItemsCount(member, ROUGH_JEWEL) >= JEWEL_COUNT)
				{
					st.setCond(3, true);
				}
				else
				{
					playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
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
				if (npc.getId() == ELLIE)
				{
					htmltext = "30091-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ELLIE:
					{
						switch (st.getCond())
						{
							case 1:
								htmltext = "30091-04.html";
								break;
							case 3:
								htmltext = (getQuestItemsCount(player, ROUGH_JEWEL) >= JEWEL_COUNT) ? "30091-06.html" : "30091-05.html";
								break;
							case 4:
								htmltext = ((getQuestItemsCount(player, IRON_ORE) >= IRON_ORE_COUNT) && (getQuestItemsCount(player, ARMOR_FRAGMENT_LOW_GRADE) >= ARMOR_FRAGMENT_COUNT) && (getQuestItemsCount(player, ACCESORY_GEM_LOW_GRADE) >= ACCESORY_GEM_COUNT)) ? "30091-09.html" : "30091-10.html";
								break;
						}
						break;
					}
					case FELTON:
					{
						if (st.isCond(1))
						{
							htmltext = "30879-01.html";
						}
						else if (st.isCond(2))
						{
							htmltext = "30879-03.html";
						}
						break;
					}
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
				}
			}
		}
		return htmltext;
	}
}