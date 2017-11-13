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
package org.l2junity.scripts.quests.Q10541_TrainLikeItsRealThing;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10321_QualificationsOfTheSeeker.Q10321_QualificationsOfTheSeeker;

/**
 * Train Like It's Real Thing (10541)
 * @author Gladicek
 */
public final class Q10541_TrainLikeItsRealThing extends Quest
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int SCARECROW = 27457;
	private static final int ADVENTURE_GUIDE = 32981;
	// Buffs
	private static final SkillHolder WARRIOR_HARMONY = new SkillHolder(15649, 1); // Warrior's Harmony (Adventurer)
	private static final SkillHolder WIZARD_HARMONY = new SkillHolder(15650, 1); // Wizard's Harmony (Adventurer)
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody (Adventurer)
		new SkillHolder(15643, 1), // Drum Melody (Adventurer)
		new SkillHolder(15644, 1), // Pipe Organ Melody (Adventurer)
		new SkillHolder(15645, 1), // Guitar Melody (Adventurer)
		new SkillHolder(15646, 1), // Harp Melody (Adventurer)
		new SkillHolder(15647, 1), // Lute Melody (Adventurer)
		new SkillHolder(15651, 1), // Prevailing Sonata (Adventurer)
		new SkillHolder(15652, 1), // Daring Sonata (Adventurer)
		new SkillHolder(15653, 1), // Refreshing Sonata (Adventurer)
	};
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10541_TrainLikeItsRealThing()
	{
		super(10541);
		addStartNpc(SHANNON);
		addTalkId(SHANNON, ADVENTURE_GUIDE);
		addKillId(SCARECROW);
		addCondMaxLevel(MAX_LEVEL, "");
		addCondCompletedQuest(Q10321_QualificationsOfTheSeeker.class.getSimpleName(), "");
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
			case "32974-03.htm":
			case "32981-02.html":
			{
				htmltext = event;
				break;
			}
			case "32974-04.htm":
			{
				qs.startQuest();
				showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 4500);
				htmltext = event;
				break;
			}
			case "checkClass":
			{
				if (qs.isCond(3))
				{
					htmltext = player.isInCategory(CategoryType.MAGE_GROUP) ? "32981-03.html" : "32981-03a.html";
					player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_002_Guide_01.htm", TutorialShowHtml.LARGE_WINDOW));
				}
				break;
			}
			case "buff":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 4500);
					
					for (SkillHolder holder : BUFFS)
					{
						npc.doInstantCast(player, holder);
						
					}
					if (player.isInCategory(CategoryType.MAGE_GROUP))
					{
						npc.doInstantCast(player, WIZARD_HARMONY);
						htmltext = "32981-04a.html";
					}
					else
					{
						npc.doInstantCast(player, WARRIOR_HARMONY);
						htmltext = "32981-04.html";
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
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
				if ((npc.getId() == SHANNON) && (!isSimulated))
				{
					switch (qs.getCond())
					{
						case 1:
						{
							showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 4500);
							htmltext = "32974-05.html";
							break;
						}
						case 2:
						{
							qs.setCond(3, true);
							qs.setMemoState(0);
							showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_ADVENTURERS_GUIDE_FOR_TRAINING, ExShowScreenMessage.TOP_CENTER, 4500);
							htmltext = "32974-06.html";
							break;
						}
						case 3:
						{
							showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_ADVENTURERS_GUIDE_FOR_TRAINING, ExShowScreenMessage.TOP_CENTER, 4500);
							htmltext = "32974-07.html";
							break;
						}
						case 4:
						{
							showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 4500);
							htmltext = "32974-09.html";
							break;
						}
						case 5:
						{
							addExp(player, 2_550);
							addSp(player, 7);
							qs.exitQuest(false, true);
							htmltext = "32974-08.html";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == ADVENTURE_GUIDE)
				{
					if (qs.isCond(3))
					{
						htmltext = "32981-01.html";
					}
					else if (qs.isCond(4))
					{
						
						showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 4500);
						
						for (SkillHolder holder : BUFFS)
						{
							npc.doInstantCast(player, holder);
							
						}
						if (player.isInCategory(CategoryType.MAGE_GROUP))
						{
							npc.doInstantCast(player, WIZARD_HARMONY);
							htmltext = "32981-05a.html";
						}
						else
						{
							npc.doInstantCast(player, WARRIOR_HARMONY);
							htmltext = "32981-05.html";
						}
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
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted())
		{
			int killedDummy = qs.getMemoState();
			killedDummy++;
			
			if (qs.isCond(1))
			{
				if (killedDummy >= 4)
				{
					qs.setCond(2, true);
					showOnScreenMsg(killer, NpcStringId.SPEAK_WITH_SHANNON, ExShowScreenMessage.TOP_CENTER, 4500);
				}
				qs.setMemoState(killedDummy);
				sendNpcLogList(killer);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else if (qs.isCond(4))
			{
				if (killedDummy >= 4)
				{
					qs.setCond(5, true);
					showOnScreenMsg(killer, NpcStringId.SPEAK_WITH_SHANNON, ExShowScreenMessage.TOP_CENTER, 4500);
				}
				qs.setMemoState(killedDummy);
				sendNpcLogList(killer);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			if (qs.isCond(1))
			{
				npcLogList.add(new NpcLogListHolder(NpcStringId.DEFEATING_THE_SCARECROW, qs.getMemoState()));
			}
			else if (qs.isCond(4))
			{
				npcLogList.add(new NpcLogListHolder(SCARECROW, false, qs.getMemoState()));
			}
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}