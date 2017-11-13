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
package org.l2junity.scripts.quests.Q00177_SplitDestiny;

import org.l2junity.commons.util.ArrayUtil;
import org.l2junity.gameserver.data.xml.impl.CategoryData;
import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.SubclassInfoType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExSubjobInfo;
import org.l2junity.gameserver.network.client.send.SocialAction;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;

/**
 * Split Destiny (177)
 * @author Sdw
 */
public final class Q00177_SplitDestiny extends Quest
{
	// NPCs
	private static final int HADEL = 33344;
	private static final int ISHUMA = 32615;
	private static final int VAMPIRICE_BERISE = 27530;
	private static final int[] GIANTS_FOOT_MONSTERS =
	{
		22257, // Island Guardian
		22258, // White Sand Mirage
		22259, // Muddy Coral
		22260, // Kleopora
	};
	// Items
	private static final ItemHolder PETRIFIED_GIANTS_HAND = new ItemHolder(17718, 2);
	private static final ItemHolder PETRIFIED_GIANTS_FOOT = new ItemHolder(17719, 2);
	private static final int PETRIFIED_GIANTS_HAND_PIECE = 17720;
	private static final int PETRIFIED_GIANTS_FOOT_PIECE = 17721;
	// Rewards
	private static final ItemHolder RECIPE_TWILIGHT_NECKLACE = new ItemHolder(36791, 1);
	private static final ItemHolder CRYSTAL_R = new ItemHolder(17371, 5);
	// Variable
	private static final String VAR_SUB_INDEX = "SPLIT_DESTINY_SUB_ID";
	// Misc
	private static final int MIN_LEVEL = 80;
	
	public Q00177_SplitDestiny()
	{
		super(177);
		addStartNpc(HADEL);
		addTalkId(HADEL, ISHUMA);
		addKillId(VAMPIRICE_BERISE);
		addKillId(GIANTS_FOOT_MONSTERS);
		registerQuestItems(PETRIFIED_GIANTS_HAND.getId(), PETRIFIED_GIANTS_FOOT.getId(), PETRIFIED_GIANTS_HAND_PIECE, PETRIFIED_GIANTS_FOOT_PIECE);
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
			case "33344-14.htm":
			{
				qs.startQuest();
				qs.set(VAR_SUB_INDEX, player.getClassIndex());
				htmltext = event;
				break;
			}
			case "33344-19.htm":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "33344-22.htm":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "32615-03.htm":
			{
				if (qs.isCond(7) && (getQuestItemsCount(player, PETRIFIED_GIANTS_HAND_PIECE) >= 10) && (getQuestItemsCount(player, PETRIFIED_GIANTS_FOOT_PIECE) >= 10))
				{
					takeItems(player, PETRIFIED_GIANTS_HAND_PIECE, -1);
					takeItems(player, PETRIFIED_GIANTS_FOOT_PIECE, -1);
					qs.setCond(8, true);
					htmltext = event;
				}
				break;
			}
			case "33344-25.htm":
			{
				if (qs.isCond(9) && (qs.getMemoState() == 0) && hasItem(player, PETRIFIED_GIANTS_HAND) && hasItem(player, PETRIFIED_GIANTS_FOOT))
				{
					takeItem(player, PETRIFIED_GIANTS_HAND);
					takeItem(player, PETRIFIED_GIANTS_FOOT);
					qs.setMemoState(1);
					htmltext = event;
				}
				else if (qs.isCond(9) && (qs.getMemoState() == 1))
				{
					htmltext = event;
				}
				break;
			}
			case "33344-27.htm":
			{
				if (qs.isCond(9))
				{
					if (player.getClassIndex() != qs.getInt(VAR_SUB_INDEX))
					{
						return "33344-16.htm";
					}
					
					player.getSubClasses().get(player.getClassIndex()).setIsDualClass(true);
					
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS);
					msg.addClassId(player.getClassId().getId());
					msg.addClassId(player.getClassId().getId());
					player.sendPacket(msg);
					
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.broadcastSocialAction(SocialAction.LEVEL_UP);
					
					giveItems(player, RECIPE_TWILIGHT_NECKLACE);
					giveItems(player, CRYSTAL_R);
					addExp(player, 175_739_575);
					addSp(player, 42_177);
					qs.exitQuest(false, true);
				}
				break;
			}
			case "33344-13.htm":
			case "33344-18.htm":
			case "32615-02.htm":
			case "33344-26.htm":
			{
				htmltext = event;
				break;
			}
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
				if (npc.getId() == HADEL)
				{
					if (!player.isSubClassActive() || !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) || (player.getLevel() < MIN_LEVEL))
					{
						htmltext = "33344-02.htm";
					}
					else if (!CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, player.getBaseClass()))
					{
						htmltext = "33344-03.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.TANKER_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.TANKER_CATEGORY))
					{
						htmltext = "33344-sigel.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.WARRIOR_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.WARRIOR_CATEGORY))
					{
						htmltext = "33344-tyrr.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.ROGUE_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.ROGUE_CATEGORY))
					{
						htmltext = "33344-othell.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.ARCHER_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.ARCHER_CATEGORY))
					{
						htmltext = "33344-yul.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.WIZARD_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.WIZARD_CATEGORY))
					{
						htmltext = "33344-feoh.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.ENCHANTER_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.ENCHANTER_CATEGORY))
					{
						htmltext = "33344-iss.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.SUMMONER_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.SUMMONER_CATEGORY))
					{
						htmltext = "33344-wynn.htm";
					}
					else if (CategoryData.getInstance().isInCategory(CategoryType.HEALER_CATEGORY, player.getBaseClass()) && player.isInCategory(CategoryType.HEALER_CATEGORY))
					{
						htmltext = "33344-aeore.htm";
					}
					else if (player.hasDualClass())
					{
						htmltext = "33344-12.htm";
					}
					else
					{
						htmltext = "33344-01.htm";
					}
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == HADEL)
				{
					if (player.getClassIndex() == qs.getInt(VAR_SUB_INDEX))
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							{
								htmltext = "33344-15.htm";
								break;
							}
							case 3:
							{
								htmltext = "33344-17.htm";
								break;
							}
							case 4:
							case 5:
							{
								htmltext = "33344-20.htm";
								break;
							}
							case 6:
							{
								htmltext = "33344-21.htm";
								break;
							}
							case 7:
							case 8:
							{
								htmltext = "33344-23.htm";
								break;
							}
							case 9:
							{
								htmltext = "33344-24.htm";
								break;
							}
						}
					}
					else
					{
						htmltext = "33344-16.htm";
					}
					break;
				}
				else if (npc.getId() == ISHUMA)
				{
					switch (qs.getCond())
					{
						case 7:
						{
							htmltext = "32615-01.htm";
							break;
						}
						case 8:
						{
							qs.setCond(9, true);
							giveItems(player, PETRIFIED_GIANTS_HAND);
							giveItems(player, PETRIFIED_GIANTS_FOOT);
							htmltext = "32615-04.htm";
							break;
						}
						default:
						{
							htmltext = "32615-05.htm";
							break;
						}
					}
				}
			}
			case State.COMPLETED:
			{
				if (npc.getId() == HADEL)
				{
					htmltext = "33344-12.htm";
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
		
		if ((qs != null) && (killer.getClassIndex() == qs.getInt(VAR_SUB_INDEX)))
		{
			switch (qs.getCond())
			{
				case 1:
				{
					if (npc.getId() == VAMPIRICE_BERISE)
					{
						giveItems(killer, PETRIFIED_GIANTS_HAND_PIECE, 1);
						qs.setCond(2, true);
					}
					break;
				}
				case 2:
				{
					if (npc.getId() == VAMPIRICE_BERISE)
					{
						if (giveItemRandomly(killer, npc, PETRIFIED_GIANTS_HAND_PIECE, 1, 10, 1.0, true))
						{
							qs.setCond(3, true);
						}
					}
					break;
				}
				case 4:
				{
					if (ArrayUtil.contains(GIANTS_FOOT_MONSTERS, npc.getId()))
					{
						giveItems(killer, PETRIFIED_GIANTS_FOOT_PIECE, 1);
						qs.setCond(5, true);
					}
					break;
				}
				case 5:
				{
					if (ArrayUtil.contains(GIANTS_FOOT_MONSTERS, npc.getId()))
					{
						if (giveItemRandomly(killer, npc, PETRIFIED_GIANTS_FOOT_PIECE, 1, 10, 1.0, true))
						{
							qs.setCond(6, true);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
