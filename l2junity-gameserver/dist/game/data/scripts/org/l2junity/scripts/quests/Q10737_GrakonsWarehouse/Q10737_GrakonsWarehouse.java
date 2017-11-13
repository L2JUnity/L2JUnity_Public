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
package org.l2junity.scripts.quests.Q10737_GrakonsWarehouse;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10735_ASpecialPower.Q10735_ASpecialPower;
import org.l2junity.scripts.quests.Q10736_ASpecialPower.Q10736_ASpecialPower;

/**
 * Grakons Warehouse (10737)
 * @author Sdw
 */
public final class Q10737_GrakonsWarehouse extends Quest
{
	// NPC's
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int GRAKON = 33947;
	// Items
	private static final ItemHolder APPRENTICE_SUPPORT_BOX = new ItemHolder(39520, 1);
	private static final ItemHolder APPRENTICE_ADVENTURER_STAFF = new ItemHolder(7816, 1);
	private static final ItemHolder APPRENTICE_ADVENTURER_FISTS = new ItemHolder(7819, 1);
	// Misc
	private static final int MIN_LEVEL = 5;
	private static final int MAX_LEVEL = 20;
	
	public Q10737_GrakonsWarehouse()
	{
		super(10737);
		addStartNpc(KATALIN, AYANTHE);
		addTalkId(KATALIN, AYANTHE, GRAKON);
		addCondRace(Race.ERTHEIA, "");
		registerQuestItems(APPRENTICE_SUPPORT_BOX.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33942-02.htm":
			case "33943-02.htm":
			case "33947-03.html":
			case "33947-04.html":
				break;
			case "33942-03.htm":
			case "33943-03.htm":
			{
				qs.startQuest();
				giveItems(player, APPRENTICE_SUPPORT_BOX);
				break;
			}
			case "33947-05.html":
			{
				if (qs.isStarted())
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2text\\QT_007_post_01.htm", TutorialShowHtml.LARGE_WINDOW));
						showOnScreenMsg(player, NpcStringId.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
						if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							giveItems(player, APPRENTICE_ADVENTURER_STAFF);
						}
						else
						{
							giveItems(player, APPRENTICE_ADVENTURER_FISTS);
						}
						addExp(player, 2625);
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
			default:
				htmltext = null;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				switch (npc.getId())
				{
					case KATALIN:
					{
						if (!player.isInCategory(CategoryType.MAGE_GROUP))
						{
							htmltext = (meetStartRestrictions(player)) ? "33943-01.htm" : "33943-00.htm";
						}
						break;
					}
					case AYANTHE:
					{
						if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							htmltext = (meetStartRestrictions(player)) ? "33942-01.htm" : "33942-00.htm";
						}
						break;
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KATALIN:
					{
						if (!player.isInCategory(CategoryType.MAGE_GROUP))
						{
							htmltext = "33943-04.html";
						}
						break;
					}
					case AYANTHE:
					{
						if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							htmltext = "33942-04.html";
						}
						break;
					}
					case GRAKON:
					{
						htmltext = (player.isInCategory(CategoryType.MAGE_GROUP)) ? "33947-02.html" : "33947-01.html";
					}
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
	
	private boolean meetStartRestrictions(PlayerInstance player)
	{
		if (player.isInCategory(CategoryType.MAGE_GROUP))
		{
			return (player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL) && player.hasQuestCompleted(Q10735_ASpecialPower.class.getSimpleName());
		}
		return (player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL) && player.hasQuestCompleted(Q10736_ASpecialPower.class.getSimpleName());
	}
}
