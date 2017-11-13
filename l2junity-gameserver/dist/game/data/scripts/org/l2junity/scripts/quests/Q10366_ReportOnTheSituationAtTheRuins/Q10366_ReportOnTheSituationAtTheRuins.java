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
package org.l2junity.scripts.quests.Q10366_ReportOnTheSituationAtTheRuins;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10365_ForTheSearchdogKing.Q10365_ForTheSearchdogKing;

/**
 * Report on the Situation at the Ruins (10366)
 * @author Gladicek
 */
public final class Q10366_ReportOnTheSituationAtTheRuins extends Quest
{
	// NPCs
	private static final int SEBION = 32978;
	private static final int[] MONSTERS =
	{
		22995, // Rider
		22993, // Critter
		23122, // Eyesaroch
		22994, // Avian
	};
	// Misc
	private static final int MIN_LEVEL = 17;
	private static final int MAX_LEVEL = 25;
	
	public Q10366_ReportOnTheSituationAtTheRuins()
	{
		super(10366);
		addStartNpc(SEBION);
		addTalkId(SEBION);
		addKillId(MONSTERS);
		addCondNotRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "32978-06.htm");
		addCondCompletedQuest(Q10365_ForTheSearchdogKing.class.getSimpleName(), "32978-06.htm");
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
			case "32978-02.htm":
			case "32978-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32978-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32978-08.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						addExp(player, 114_000);
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
			int killedHusks = qs.getMemoState();
			killedHusks++;
			
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			qs.setMemoState(killedHusks);
			sendNpcLogList(killer);
			
			if (killedHusks >= 40)
			{
				qs.setCond(2, true);
				
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			if (qs.isCond(1))
			{
				npcLogList.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_HUSKS, qs.getMemoState()));
			}
			return npcLogList;
		}
		return super.getNpcLogList(player);
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
				if (npc.getId() == SEBION)
				{
					htmltext = "32978-01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32978-05.html";
					break;
				}
				else if (qs.isCond(2))
				{
					htmltext = "32978-07.html";
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