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
package org.l2junity.scripts.quests.Q10330_ToTheRuinsOfYeSagira;

import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

/**
 * To The Ruins of Ye Sagira (10330)
 * @author Gladicek
 */
public final class Q10330_ToTheRuinsOfYeSagira extends Quest
{
	// NPCs
	private static final int FRANCO = 32153;
	private static final int RIVIAN = 32147;
	private static final int DEVON = 32160;
	private static final int TOOK = 32150;
	private static final int MOKA = 32157;
	private static final int VALFAR = 32146;
	private static final int MILIA = 30006;
	private static final int LAKCIS = 32977;
	// Items
	private static final int RING_OF_KNOWLEDGE = 875;
	private static final int HEALING_POTION = 1060;
	// Misc
	private static final int MIN_LEVEL = 7;
	private static final int MAX_LEVEL = 20;
	
	public Q10330_ToTheRuinsOfYeSagira()
	{
		super(10330);
		addStartNpc(FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR);
		addTalkId(FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR, MILIA, LAKCIS);
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
			case "32153-03.htm":
			case "32147-03.htm":
			case "32150-03.htm":
			case "32146-03.htm":
			case "32160-03.htm":
			case "32157-03.htm":
			case "32153-04.htm":
			case "32147-04.htm":
			case "32150-04.htm":
			case "32146-04.htm":
			case "32160-04.htm":
			case "32157-04.htm":
			case "30006-02.html":
			{
				htmltext = event;
				break;
			}
			case "32153-05.html":
			case "32147-05.html":
			case "32150-05.html":
			case "32146-05.html":
			case "32160-05.html":
			case "32157-05.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30006-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "32977-02.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveItems(player, RING_OF_KNOWLEDGE, 2);
						giveItems(player, HEALING_POTION, 100);
						addExp(player, 20100);
						addSp(player, 11);
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
	public String onTalk(Npc npc, PlayerInstance player, boolean isSimulated)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				switch (npc.getId())
				{
					case FRANCO:
					{
						if (player.getRace() == Race.HUMAN)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32153-01.htm";
								break;
							}
							htmltext = "32153-07.htm";
							break;
						}
						htmltext = "32153-02.htm";
						break;
					}
					case RIVIAN:
					{
						if (player.getRace() == Race.ELF)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32147-01.htm";
								break;
							}
							htmltext = "32147-07.htm";
							break;
						}
						htmltext = "32147-02.htm";
						break;
					}
					case DEVON:
					{
						if (player.getRace() == Race.DARK_ELF)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32160-01.htm";
								break;
							}
							htmltext = "32160-07.htm";
							break;
						}
						htmltext = "32160-02.htm";
						break;
					}
					case TOOK:
					{
						if (player.getRace() == Race.ORC)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32150-01.htm";
								break;
							}
							htmltext = "32150-07.htm";
							break;
						}
						htmltext = "32150-02.htm";
						break;
					}
					case MOKA:
					{
						if (player.getRace() == Race.DWARF)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32157-01.htm";
								break;
							}
							htmltext = "32157-07.htm";
							break;
						}
						htmltext = "32157-02.htm";
						break;
					}
					case VALFAR:
					{
						if (player.getRace() == Race.KAMAEL)
						{
							if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
							{
								htmltext = "32146-01.htm";
								break;
							}
							htmltext = "32146-07.htm";
							break;
						}
						htmltext = "32146-02.htm";
						break;
					}
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case FRANCO:
					{
						if ((player.getRace() == Race.HUMAN) && qs.isCond(1))
						{
							htmltext = "32153-06.html";
						}
						break;
					}
					case RIVIAN:
					{
						if ((player.getRace() == Race.ELF) && qs.isCond(1))
						{
							htmltext = "32147-06.html";
						}
						break;
					}
					case DEVON:
					{
						if ((player.getRace() == Race.DARK_ELF) && qs.isCond(1))
						{
							htmltext = "32160-06.html";
						}
						break;
					}
					case TOOK:
					{
						if ((player.getRace() == Race.ORC) && qs.isCond(1))
						{
							htmltext = "32150-06.html";
						}
						break;
					}
					case MOKA:
					{
						if ((player.getRace() == Race.DWARF) && qs.isCond(1))
						{
							htmltext = "32157-06.html";
						}
						break;
					}
					case VALFAR:
					{
						if ((player.getRace() == Race.KAMAEL) && qs.isCond(1))
						{
							htmltext = "32146-06.html";
						}
						break;
					}
					case MILIA:
					{
						if (qs.isCond(1))
						{
							htmltext = "30006-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30006-03.html";
						}
						break;
					}
					case LAKCIS:
					{
						if (qs.isCond(2))
						{
							htmltext = "32977-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case FRANCO:
					case RIVIAN:
					case DEVON:
					case TOOK:
					case MOKA:
					case VALFAR:
					case MILIA:
					case LAKCIS:
					{
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}