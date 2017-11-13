/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.quests.Q00141_ShadowFoxPart3;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q00140_ShadowFoxPart2.Q00140_ShadowFoxPart2;
import org.l2junity.scripts.quests.Q00998_FallenAngelSelect.Q00998_FallenAngelSelect;

/**
 * Shadow Fox - 3 (141)
 * @author Nono
 */
public class Q00141_ShadowFoxPart3 extends Quest
{
	// NPCs
	private static final int NATOOLS = 30894;
	// Monsters
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	
	static
	{
		MOBS.put(20135, 53); // Alligator
		MOBS.put(20791, 100); // Crokian Warrior
		MOBS.put(20792, 92); // Farhite
	}
	
	// Items
	private static final int PREDECESSORS_REPORT = 10350;
	// Misc
	private static final int MIN_LEVEL = 37;
	private static final int MAX_REWARD_LEVEL = 42;
	private static final int REPORT_COUNT = 30;
	
	public Q00141_ShadowFoxPart3()
	{
		super(141);
		addStartNpc(NATOOLS);
		addTalkId(NATOOLS);
		addKillId(MOBS.keySet());
		registerQuestItems(PREDECESSORS_REPORT);
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
			case "30894-05.html":
			case "30894-10.html":
			case "30894-11.html":
			case "30894-12.html":
			case "30894-13.html":
			case "30894-14.html":
			case "30894-16.html":
			case "30894-17.html":
			case "30894-19.html":
			case "30894-20.html":
				break;
			case "30894-03.htm":
				st.startQuest();
				break;
			case "30894-06.html":
				st.setCond(2, true);
				break;
			case "30894-15.html":
				st.set("talk", "2");
				break;
			case "30894-18.html":
				st.setCond(4, true);
				st.unset("talk");
				break;
			case "30894-21.html":
				giveAdena(player, 88888, true);
				if (player.getLevel() <= MAX_REWARD_LEVEL)
				{
					addExp(player, 278005);
					addSp(player, 17058); // TODO Incorrect SP reward.
				}
				st.exitQuest(false, true);
				
				final Quest q = QuestManager.getInstance().getQuest(Q00998_FallenAngelSelect.class.getSimpleName());
				if (q != null)
				{
					q.newQuestState(player).setState(State.STARTED);
				}
				break;
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
		if (member == null)
		{
			return super.onKill(npc, player, isSummon);
		}
		final QuestState st = getQuestState(member, false);
		if ((getRandom(100) < MOBS.get(npc.getId())))
		{
			giveItems(player, PREDECESSORS_REPORT, 1);
			if (getQuestItemsCount(player, PREDECESSORS_REPORT) >= REPORT_COUNT)
			{
				st.setCond(3, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
				htmltext = (player.getLevel() >= MIN_LEVEL) ? player.hasQuestCompleted(Q00140_ShadowFoxPart2.class.getSimpleName()) ? "30894-01.htm" : "30894-00.html" : "30894-02.htm";
				break;
			case State.STARTED:
				switch (st.getCond())
				{
					case 1:
						htmltext = "30894-04.html";
						break;
					case 2:
						htmltext = "30894-07.html";
						break;
					case 3:
						if (st.getInt("talk") == 1)
						{
							htmltext = "30894-09.html";
						}
						else if (st.getInt("talk") == 2)
						{
							htmltext = "30894-16.html";
						}
						else
						{
							htmltext = "30894-08.html";
							takeItems(player, PREDECESSORS_REPORT, -1);
							st.set("talk", "1");
						}
						break;
					case 4:
						htmltext = "30894-19.html";
						break;
				}
				break;
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
}