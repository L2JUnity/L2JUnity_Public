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
package org.l2junity.scripts.quests.Q10378_WeedingWork;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Weeding Work (10378)
 * @author netvirus
 */
public class Q10378_WeedingWork extends Quest
{
	// NPCs
	private static final int DADPHYNA = 33697;
	// Items
	private static final int MANDRAGORA_STEM = 34974;
	private static final int MANDRAGORA_ROOT = 34975;
	// Monters
	private static final int MANDRAGORA_OF_JOY_AND_SORROW = 23210;
	private static final int MANDRAGORA_OF_PRAYER = 23211;
	// Misc
	private static final int MIN_LVL = 95;
	// Reward
	private static final int SCROLL_OF_ESCAPE_GUILLOTINE_FORTRESS = 35292;
	
	public Q10378_WeedingWork()
	{
		super(10378);
		addStartNpc(DADPHYNA);
		addTalkId(DADPHYNA);
		addKillId(MANDRAGORA_OF_JOY_AND_SORROW, MANDRAGORA_OF_PRAYER);
		registerQuestItems(MANDRAGORA_STEM, MANDRAGORA_ROOT);
		addCondMinLevel(MIN_LVL, "33697-02.htm");
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
			case "33697-04.htm":
			case "33697-05.htm":
			{
				htmltext = event;
				break;
			}
			case "33697-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33697-09.html":
			{
				if (st.isCond(2) && (getQuestItemsCount(player, MANDRAGORA_STEM) >= 5) && (getQuestItemsCount(player, MANDRAGORA_ROOT) >= 5))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						giveItems(player, SCROLL_OF_ESCAPE_GUILLOTINE_FORTRESS, 2);
						addExp(player, 845059770);
						addSp(player, 202814);
						giveAdena(player, 3000000, true);
						st.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
						break;
					}
				}
				break;
			}
		}
		return htmltext;
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
		
		if (npc.getId() == DADPHYNA)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
				{
					htmltext = "33697-03.html";
					break;
				}
				case State.CREATED:
				{
					htmltext = "33697-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "33697-07.html";
							break;
						}
						case 2:
						{
							if ((getQuestItemsCount(player, MANDRAGORA_STEM) >= 5) && (getQuestItemsCount(player, MANDRAGORA_ROOT) >= 5))
							{
								htmltext = "33697-08.html";
							}
							else
							{
								st.setCond(1);
							}
							break;
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(killer, 1, 2, npc);
		if (st != null)
		{
			PlayerInstance member = st.getPlayer();
			if (st.isCond(1) && (npc.distance3d(killer) <= 1500))
			{
				long mandragoraStem = getQuestItemsCount(member, MANDRAGORA_STEM);
				long mandragoraRoot = getQuestItemsCount(member, MANDRAGORA_ROOT);
				switch (npc.getId())
				{
					case MANDRAGORA_OF_JOY_AND_SORROW:
					{
						if (mandragoraStem < 5)
						{
							++mandragoraStem;
							giveItems(member, MANDRAGORA_STEM, 1);
							playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						break;
					}
					case MANDRAGORA_OF_PRAYER:
					{
						if (mandragoraRoot < 5)
						{
							++mandragoraRoot;
							giveItems(member, MANDRAGORA_ROOT, 1);
							playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						break;
					}
				}
				if ((mandragoraStem >= 5) && (mandragoraRoot >= 5))
				{
					st.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
