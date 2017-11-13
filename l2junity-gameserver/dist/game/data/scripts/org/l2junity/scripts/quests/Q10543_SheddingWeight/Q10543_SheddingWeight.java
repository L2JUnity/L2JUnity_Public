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
package org.l2junity.scripts.quests.Q10543_SheddingWeight;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10542_SearchingForNewPower.Q10542_SearchingForNewPower;

/**
 * Shedding Weight (10543)
 * @author Gladicek
 */
public final class Q10543_SheddingWeight extends Quest
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int WILFORD = 30005;
	// Items
	private static final int NOVICE_TRAINING_LOG = 47601;
	private static final int APPRENTICE_ADVENTURERS_KNIFE = 7818;
	private static final int APPRENTICE_ADVENTURERS_LONG_SWORD = 7821;
	private static final int APPRENTICE_ADVENTURERS_STAFF = 7816;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10543_SheddingWeight()
	{
		super(10543);
		addStartNpc(SHANNON);
		addTalkId(SHANNON, WILFORD);
		addCondMaxLevel(MAX_LEVEL, "");
		addCondCompletedQuest(Q10542_SearchingForNewPower.class.getSimpleName(), "");
		registerQuestItems(NOVICE_TRAINING_LOG);
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
			case "32974-02.htm":
			case "30005-02.html":
			{
				htmltext = event;
				break;
			}
			case "32974-03.htm":
			{
				qs.startQuest();
				giveItems(player, NOVICE_TRAINING_LOG, 1);
				htmltext = event;
				break;
			}
			case "30005-03.html":
			{
				if (qs.isCond(1))
				{
					showOnScreenMsg(player, NpcStringId.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 4500);
					player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_007_post_01.htm", TutorialShowHtml.LARGE_WINDOW));
					if (player.isInCategory(CategoryType.MAGE_GROUP))
					{
						giveItems(player, APPRENTICE_ADVENTURERS_STAFF, 1);
					}
					else
					{
						giveItems(player, APPRENTICE_ADVENTURERS_KNIFE, 1);
						giveItems(player, APPRENTICE_ADVENTURERS_LONG_SWORD, 1);
					}
					addExp(player, 2630);
					addSp(player, 9);
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
				if (npc.getId() == SHANNON)
				{
					htmltext = "32974-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == SHANNON)
				{
					if (qs.isCond(1))
					{
						htmltext = "32974-04.html";
					}
					break;
				}
				else if (npc.getId() == WILFORD)
				{
					if (qs.isCond(1))
					{
						htmltext = "30005-01.html";
					}
					break;
				}
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