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
package org.l2junity.scripts.quests.Q10377_TheInvadedExecutionGrounds;

import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * The Invaded Execution Grounds (10377)
 * @author netvirus
 */

public class Q10377_TheInvadedExecutionGrounds extends Quest
{
	// NPCs
	private static final int SYLVAIN = 30070;
	private static final int HARLAN = 30074;
	private static final int RODERIK = 30631;
	private static final int WARDEN_OVERSEER = 33718;
	private static final int CROOK_THE_MAD = 33719;
	private static final int GUILLOTINE_OF_DEATH = 33717;
	private static final int ENDRIGO = 30632;
	// Items
	private static final int HARLANS_ORDERS = 34972;
	private static final int ENDRIGOS_REPORT = 34973;
	// Misc
	private static final int MIN_LVL = 95;
	// Reward
	private static final int SCROLL_OF_ESCAPE_GUILLOTINE_FORTRESS = 35292;
	
	public Q10377_TheInvadedExecutionGrounds()
	{
		super(10377);
		addStartNpc(SYLVAIN);
		addTalkId(SYLVAIN, HARLAN, RODERIK, ENDRIGO);
		addSeeCreatureId(WARDEN_OVERSEER, CROOK_THE_MAD, GUILLOTINE_OF_DEATH);
		registerQuestItems(HARLANS_ORDERS, ENDRIGOS_REPORT);
		addCondMinLevel(MIN_LVL, "30070-02.htm");
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
			case "30070-02a.htm":
			case "30070-04.htm":
			case "30070-05.htm":
			case "30074-02.html":
			case "30631-02.html":
			{
				htmltext = event;
				break;
			}
			case "30070-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30074-03.html":
			{
				if (st.isCond(1))
				{
					giveItems(player, HARLANS_ORDERS, 1);
					st.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "30631-03.html":
			{
				if (st.isCond(2))
				{
					giveItems(player, ENDRIGOS_REPORT, 1);
					takeItems(player, HARLANS_ORDERS, 1);
					st.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "30632-02.html":
			{
				if (st.isCond(6))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						giveItems(player, SCROLL_OF_ESCAPE_GUILLOTINE_FORTRESS, 2);
						addExp(player, 756106110);
						addSp(player, 181465);
						giveAdena(player, 2970560, true);
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
		
		switch (npc.getId())
		{
			case SYLVAIN:
			{
				switch (st.getState())
				{
					case State.COMPLETED:
					{
						htmltext = "30070-03.html";
						break;
					}
					case State.CREATED:
					{
						htmltext = "30070-01.htm";
						break;
					}
					case State.STARTED:
					{
						if (st.isCond(1))
						{
							htmltext = "30070-07.html";
						}
						break;
					}
				}
				break;
			}
			case HARLAN:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30074-01.html";
						break;
					}
					case 2:
					{
						htmltext = "30074-04.html";
						break;
					}
				}
				break;
			}
			case RODERIK:
			{
				switch (st.getCond())
				{
					case 2:
					{
						htmltext = "30631-01.html";
						break;
					}
					case 3:
					case 4:
					case 5:
					{
						htmltext = "30631-04.html";
						break;
					}
					case 6:
					{
						htmltext = "30631-05.html";
						break;
					}
				}
				break;
			}
			case ENDRIGO:
			{
				if (st.isCompleted())
				{
					htmltext = "30632-03.html";
				}
				else if (st.isCond(6))
				{
					htmltext = "30632-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState qs = getQuestState(player, false);
			
			if ((qs != null) && (qs.getCond() < 6))
			{
				switch (npc.getId())
				{
					case WARDEN_OVERSEER:
					{
						if (qs.isCond(3))
						{
							qs.setCond(4);
						}
						break;
					}
					case CROOK_THE_MAD:
					{
						if (qs.isCond(4))
						{
							qs.setCond(5);
						}
						break;
					}
					case GUILLOTINE_OF_DEATH:
					{
						if (qs.isCond(5))
						{
							qs.setCond(6);
						}
						break;
					}
				}
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}
