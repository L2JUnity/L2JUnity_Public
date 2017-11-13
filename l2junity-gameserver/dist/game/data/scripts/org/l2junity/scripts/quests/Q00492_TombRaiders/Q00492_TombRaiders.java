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
package org.l2junity.scripts.quests.Q00492_TombRaiders;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.QuestType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * Tomb Raiders (492)
 * @author netvirus
 */

public class Q00492_TombRaiders extends Quest
{
	// NPCs
	private static final int ZENYA = 32140;
	// Item
	private static final int RELIC_OF_THE_EMPIRE = 34769;
	// Monsters
	private static final int APPARITION_DESTROYER = 23193;
	private static final int APPARITION_ASSASSIN = 23194;
	private static final int APPARITION_SNIPER = 23195;
	private static final int APPARITION_WIZARD = 23196;
	// Misc
	private static final int MIN_LVL = 80;
	
	public Q00492_TombRaiders()
	{
		super(492);
		addStartNpc(ZENYA);
		addTalkId(ZENYA);
		addKillId(APPARITION_DESTROYER, APPARITION_ASSASSIN, APPARITION_SNIPER, APPARITION_WIZARD);
		registerQuestItems(RELIC_OF_THE_EMPIRE);
		addCondMinLevel(MIN_LVL, "32140-03.htm");
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "32140-02.htm");
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
			case "32140-05.htm":
			{
				htmltext = event;
				break;
			}
			case "32140-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
		}
		return htmltext;
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
		
		if (npc.getId() == ZENYA)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
				{
					if (!st.isNowAvailable())
					{
						htmltext = "32140-04.htm";
						break;
					}
					// If quest is available again, set state to created and execute the state for new quest again
					st.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "32140-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "32140-07.html";
							break;
						}
						case 2:
						{
							if (!isSimulated)
							{
								if (player.getLevel() >= MIN_LVL)
								{
									final int chance = getRandom(100);
									if (chance < 32)
									{
										addExp(player, 25000000);
										addSp(player, 6000);
									}
									else if (chance < 64)
									{
										addExp(player, 20000000);
										addSp(player, 5000);
									}
									else if (chance < 96)
									{
										addExp(player, 15000000);
										addSp(player, 4000);
									}
									else if (chance < 100)
									{
										addExp(player, 55000000);
										addSp(player, 12000);
									}
									st.exitQuest(QuestType.DAILY, true);
								}
								else
								{
									htmltext = getNoQuestLevelRewardMsg(player);
									break;
								}
							}
							htmltext = "32140-08.html";
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
			final PlayerInstance member = st.getPlayer();
			if (st.isCond(1) && !st.isCompleted() && (npc.distance3d(member) <= 1500))
			{
				if (giveItemRandomly(member, npc, RELIC_OF_THE_EMPIRE, 1, 50, 0.5, true))
				{
					st.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
