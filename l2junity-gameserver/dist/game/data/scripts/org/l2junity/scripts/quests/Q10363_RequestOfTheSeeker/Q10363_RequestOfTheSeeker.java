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
package org.l2junity.scripts.quests.Q10363_RequestOfTheSeeker;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10362_CertificationOfTheSeeker.Q10362_CertificationOfTheSeeker;

/**
 * Request of the Seeker (10363)
 * @author Gladicek
 */
public final class Q10363_RequestOfTheSeeker extends Quest
{
	// NPCs
	private static final int NAGEL = 33450;
	private static final int CELIN = 33451;
	private static final int KRAPHER = 22996;
	private static final int CRAWLER = 22991;
	// Items
	private static final int WOODEN_HELMET = 43;
	private static final int HUSK_DISTRIBUTION_REPORT = 47606;
	// Misc
	private static final int MIN_LEVEL = 11;
	private static final int MAX_LEVEL = 20;
	
	public Q10363_RequestOfTheSeeker()
	{
		super(10363);
		addStartNpc(NAGEL);
		addTalkId(NAGEL, CELIN);
		addKillId(KRAPHER, CRAWLER);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33450-02.htm");
		addCondNotRace(Race.ERTHEIA, "33450-03.htm");
		addCondCompletedQuest(Q10362_CertificationOfTheSeeker.class.getSimpleName(), "33450-02.htm");
		registerQuestItems(HUSK_DISTRIBUTION_REPORT);
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
			case "33450-02.htm":
			case "33450-04.htm":
			case "33450-05.htm":
			{
				htmltext = event;
				break;
			}
			case "33450-06.html":
			{
				qs.startQuest();
				showOnScreenMsg(player, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_2, ExShowScreenMessage.TOP_CENTER, 4500);
				htmltext = event;
				break;
			}
			case "33451-02.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveItems(player, WOODEN_HELMET, 1);
						addExp(player, 70000);
						addSp(player, 13);
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
					break;
				}
				break;
			}
		}
		return htmltext;
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
				if (npc.getId() == NAGEL)
				{
					htmltext = "33450-01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NAGEL)
				{
					if (qs.isCond(1))
					{
						htmltext = "33450-06.html";
						break;
					}
				}
				else if (npc.getId() == CELIN)
				{
					if (qs.isCond(1))
					{
						htmltext = "33451-04.html";
						break;
					}
					else if (qs.isCond(2))
					{
						htmltext = "33451-01.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == CELIN)
				{
					htmltext = "33451-03.html";
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
			giveItems(killer, HUSK_DISTRIBUTION_REPORT, 1);
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			
			if (getQuestItemsCount(killer, HUSK_DISTRIBUTION_REPORT) >= 15)
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}