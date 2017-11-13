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
package org.l2junity.scripts.quests.Q10544_CommandoSupplies;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q10543_SheddingWeight.Q10543_SheddingWeight;

/**
 * Commando Supplies (10544)
 * @author Gladicek
 */
public final class Q10544_CommandoSupplies extends Quest
{
	// NPCs
	private static final int WILFORD = 30005;
	private static final int KATERINA = 30004;
	private static final int LECTOR = 30001;
	private static final int JACKSON = 30002;
	private static final int TREVOR = 32166;
	private static final int FRANCO = 32153;
	private static final int RIVIAN = 32147;
	private static final int DEVON = 32160;
	private static final int TOOK = 32150;
	private static final int MOKA = 32157;
	private static final int VALFAR = 32146;
	// Items
	private static final int GROCERY_SUPPLY_BOX = 47604;
	private static final int WEAPON_SUPPLY_BOX = 47602;
	private static final int ARMOR_SUPPLY_BOX = 47603;
	private static final int ACCESSORY_SUPPLY_BOX = 47605;
	private static final int APPRENTICE_EARRING = 112;
	private static final int NECKLACE_OF_KNOWLEDGE = 906;
	private static final int LEATHER_SHIRT = 22;
	private static final int LEATHER_PANTS = 29;
	private static final int LEATHER_TUNIC = 429;
	private static final int LEATHER_STOCKINGS = 464;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10544_CommandoSupplies()
	{
		super(10544);
		addStartNpc(WILFORD);
		addTalkId(WILFORD, KATERINA, LECTOR, JACKSON, TREVOR, FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR);
		addCondMaxLevel(MAX_LEVEL, "");
		addCondCompletedQuest(Q10543_SheddingWeight.class.getSimpleName(), "");
		registerQuestItems(GROCERY_SUPPLY_BOX, WEAPON_SUPPLY_BOX, ARMOR_SUPPLY_BOX, ACCESSORY_SUPPLY_BOX);
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
			case "30005-02.htm":
			case "30005-03.htm":
			case "32153-02.html":
			case "32147-02.html":
			case "32150-02.html":
			case "32146-02.html":
			case "32160-02.html":
			case "32157-02.html":
			{
				htmltext = event;
				break;
			}
			case "30005-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30004-02.html":
			{
				if (qs.isCond(1))
				{
					giveItems(player, GROCERY_SUPPLY_BOX, 1);
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "30001-02.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, WEAPON_SUPPLY_BOX, 1);
					qs.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "30002-02.html":
			{
				if (qs.isCond(3))
				{
					giveItems(player, ARMOR_SUPPLY_BOX, 1);
					qs.setCond(4);
					htmltext = event;
				}
				break;
			}
			case "check":
			{
				if (qs.isCond(4))
				{
					giveItems(player, ACCESSORY_SUPPLY_BOX, 1);
					
					switch (player.getRace())
					{
						case HUMAN:
						{
							qs.setCond(5);
							htmltext = "32166-human.html";
							break;
						}
						case ELF:
						{
							qs.setCond(6);
							htmltext = "32166-elf.html";
							break;
						}
						case DARK_ELF:
						{
							qs.setCond(7);
							htmltext = "32166-darkelf.html";
							break;
						}
						case ORC:
						{
							qs.setCond(8);
							htmltext = "32166-orc.html";
							break;
						}
						case DWARF:
						{
							qs.setCond(9);
							htmltext = "32166-dwarf.html";
							break;
						}
						case KAMAEL:
						{
							qs.setCond(10);
							htmltext = "32166-kamael.html";
							break;
						}
					}
					break;
				}
			}
			case "32153-03.html":
			case "32147-03.html":
			case "32150-03.html":
			case "32146-03.html":
			case "32160-03.html":
			case "32157-03.html":
			{
				if ((qs.getCond() >= 5) && (qs.getCond() <= 10))
				{
					giveItems(player, APPRENTICE_EARRING, 2);
					if (player.isInCategory(CategoryType.MAGE_GROUP))
					{
						giveItems(player, LEATHER_TUNIC, 1);
						giveItems(player, LEATHER_STOCKINGS, 1);
					}
					else
					{
						giveItems(player, LEATHER_SHIRT, 1);
						giveItems(player, LEATHER_PANTS, 1);
					}
					giveItems(player, NECKLACE_OF_KNOWLEDGE, 1);
					addExp(player, 2630);
					addSp(player, 10);
					qs.exitQuest(false, true);
					htmltext = event;
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
				if (npc.getId() == WILFORD)
				{
					htmltext = "30005-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case WILFORD:
					{
						if ((qs.isCond(1)))
						{
							htmltext = "30005-05.html";
						}
						break;
					}
					case KATERINA:
					{
						if (qs.isCond(1))
						{
							htmltext = "30004-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30004-03.html";
						}
						break;
					}
					case LECTOR:
					{
						if (qs.isCond(2))
						{
							htmltext = "30001-01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "30001-03.html";
						}
						break;
					}
					case JACKSON:
					{
						if (qs.isCond(3))
						{
							htmltext = "30002-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "30002-03.html";
						}
						break;
					}
					case TREVOR:
					{
						if (qs.isCond(4))
						{
							htmltext = "32166-01.html";
						}
						else if ((qs.getCond() >= 5) && (qs.getCond() <= 10))
						{
							htmltext = "32166-02.html";
						}
						break;
					}
					case FRANCO:
					{
						if ((qs.isCond(5)))
						{
							htmltext = "32153-01.html";
						}
						break;
					}
					case RIVIAN:
					{
						if ((qs.isCond(6)))
						{
							htmltext = "32147-01.html";
						}
						break;
					}
					case DEVON:
					{
						if ((qs.isCond(7)))
						{
							htmltext = "32160-01.html";
						}
						break;
					}
					case TOOK:
					{
						if ((qs.isCond(8)))
						{
							htmltext = "32150-01.html";
						}
						break;
					}
					case MOKA:
					{
						if ((qs.isCond(9)))
						{
							htmltext = "32157-01.html";
						}
						break;
					}
					case VALFAR:
					{
						if ((qs.isCond(10)))
						{
							htmltext = "32146-01.html";
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
					case WILFORD:
					case FRANCO:
					case RIVIAN:
					case DEVON:
					case TOOK:
					case MOKA:
					case VALFAR:
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