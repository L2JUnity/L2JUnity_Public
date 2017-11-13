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
package org.l2junity.scripts.quests.Q00149_PrimalMotherIstina;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.util.Util;

/**
 * Primal Mother, Istina (149)
 * @author netvirus
 */
public class Q00149_PrimalMotherIstina extends Quest
{
	// NPCs
	private static final int RUMIESE = 33293;
	// Item
	private static final int SHILENS_MARK = 17589;
	// Monster
	private static final int ISTINA_NORMAL = 29195;
	// Misc
	private static final int MIN_LVL = 90;
	// Reward
	private static final int SCROLL_ENCHANT_ARMOR_R = 17527;
	private static final int ISTINAS_BRACELET = 19455;
	private static final int[] STONES =
	{
		22635, // Fire Stone
		22636, // Water Stone
		22637, // Earth Stones
		22638, // Wind Stones
		22639, // Dark Stone
		22640 // Holy Stone
	};
	
	public Q00149_PrimalMotherIstina()
	{
		super(149);
		addStartNpc(RUMIESE);
		addTalkId(RUMIESE);
		addKillId(ISTINA_NORMAL);
		registerQuestItems(SHILENS_MARK);
		addCondMinLevel(MIN_LVL, "33293-02.htm");
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
			case "33293-04.htm":
			case "33293-05.htm":
			{
				break;
			}
			case "33293-06.html":
			{
				st.startQuest();
				break;
			}
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			{
				if (Util.isDigit(event) && st.isCond(2))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						htmltext = "33293-09.htm";
						final int index = Integer.parseInt(event);
						if ((index >= 0) && (index < STONES.length))
						{
							giveItems(player, STONES[index], 15);
						}
						addExp(player, 833_065_000);
						addSp(player, 199_935);
						giveItems(player, SCROLL_ENCHANT_ARMOR_R, 10);
						giveItems(player, ISTINAS_BRACELET, 1);
						st.exitQuest(false, true);
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
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		if (npc.getId() == RUMIESE)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
				{
					htmltext = "33293-03.html";
					break;
				}
				case State.CREATED:
				{
					htmltext = "33293-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (st.isCond(1))
					{
						htmltext = "33293-07.html";
					}
					else if (st.isCond(2))
					{
						htmltext = "33293-08.html";
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
		npc.getInstanceWorld().getPlayers().forEach(this::giveItem);
		return super.onKill(npc, killer, isSummon);
	}
	
	private void giveItem(PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1) && !hasQuestItems(player, SHILENS_MARK))
		{
			giveItems(player, SHILENS_MARK, 1);
			st.setCond(2, true);
		}
	}
}
