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
package org.l2junity.scripts.quests.Q10365_ForTheSearchdogKing;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10364_ObligationsOfTheSeeker.Q10364_ObligationsOfTheSeeker;

/**
 * For the Searchdog King (10365)
 * @author Gladicek
 */
public final class Q10365_ForTheSearchdogKing extends Quest
{
	// NPCs
	private static final int DEP = 33453;
	private static final int SEBION = 32978;
	// Item
	private static final int KING_TONIC = 47607;
	private static final int[] MONSTERS =
	{
		22995, // Rider
		22993, // Critter
		23122, // Eyesaroch
		22994, // Avian
	};
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int MAX_LEVEL = 25;
	
	public Q10365_ForTheSearchdogKing()
	{
		super(10365);
		addStartNpc(DEP);
		addTalkId(DEP, SEBION);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33453-02.htm");
		addCondNotRace(Race.ERTHEIA, "");
		addCondCompletedQuest(Q10364_ObligationsOfTheSeeker.class.getSimpleName(), "33453-02.htm");
		registerQuestItems(KING_TONIC);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33453-04.htm":
			case "33453-05.htm":
			{
				htmltext = event;
				break;
			}
			case "33453-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32978-03.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						addExp(player, 172_000);
						addSp(player, 15);
						qs.exitQuest(false, true);
						htmltext = event;
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
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1))
		{
			giveItems(killer, KING_TONIC, 1);
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			
			if (getQuestItemsCount(killer, KING_TONIC) >= 20)
			{
				qs.setCond(2, true);
				showOnScreenMsg(killer, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_5, ExShowScreenMessage.TOP_CENTER, 4500);
				
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == DEP)
				{
					htmltext = "33453-01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == DEP)
				{
					if (qs.isCond(1))
					{
						htmltext = "33453-07.html";
						break;
					}
					else if (qs.isCond(2))
					{
						showOnScreenMsg(player, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_5, ExShowScreenMessage.TOP_CENTER, 4500);
						htmltext = "33453-08.html";
						break;
					}
					break;
				}
				else if (npc.getId() == SEBION)
				{
					if (qs.isCond(1))
					{
						htmltext = "32978-01.html";
						break;
					}
					else if (qs.isCond(2))
					{
						htmltext = "32978-02.html";
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