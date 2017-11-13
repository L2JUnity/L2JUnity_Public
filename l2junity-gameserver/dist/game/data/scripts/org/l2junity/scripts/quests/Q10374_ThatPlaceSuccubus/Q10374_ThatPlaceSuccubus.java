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
package org.l2junity.scripts.quests.Q10374_ThatPlaceSuccubus;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.CategoryType;
import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q10372_PurgatoryVolvere.Q10372_PurgatoryVolvere;

/**
 * That Place Succubus (10374)
 * @author netvirus
 */
public class Q10374_ThatPlaceSuccubus extends Quest
{
	// NPCs
	private static final int ANDREI = 31292;
	private static final int AGNES = 31588;
	private static final int ZENYA = 32140;
	// Monsters
	private static final int PHANTOM_SOLDIER = 23186;
	private static final int PHANTOM_WARRIOR = 23187;
	private static final int PHANTOM_ARCHER = 23188;
	private static final int PHANTOM_SHAMAN = 23189;
	private static final int PHANTOM_MARTYR = 23190;
	// Misc
	private static final int MIN_LVL = 80;
	
	public Q10374_ThatPlaceSuccubus()
	{
		super(10374);
		addStartNpc(ANDREI);
		addTalkId(ANDREI, AGNES, ZENYA);
		addKillId(PHANTOM_SOLDIER, PHANTOM_WARRIOR, PHANTOM_ARCHER, PHANTOM_SHAMAN, PHANTOM_MARTYR);
		addCondMinLevel(MIN_LVL, "31292-05.htm");
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "31292-04.htm");
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
			case "31292-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31292-07.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "31588-02.html":
			{
				if (st.isCond(1))
				{
					htmltext = event;
					st.setCond(2, true);
				}
				break;
			}
			case "32140-02.html":
			{
				if (st.isCond(2))
				{
					htmltext = event;
					st.setCond(3, true);
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
		QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		final int npcId = npc.getId();
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				if (npcId == ANDREI)
				{
					htmltext = "31292-06.html";
				}
				break;
			}
			case State.CREATED:
			{
				if (npcId == ANDREI)
				{
					htmltext = player.hasQuestCompleted(Q10372_PurgatoryVolvere.class.getSimpleName()) ? "31292-01.htm" : "31292-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npcId)
				{
					case ANDREI:
					{
						if (st.isCond(1))
						{
							htmltext = "31292-08.html";
						}
						break;
					}
					case AGNES:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "31588-01.html";
								break;
							}
							case 2:
							{
								htmltext = "31588-03.html";
								break;
							}
						}
						break;
					}
					case ZENYA:
					{
						switch (st.getCond())
						{
							case 2:
							{
								htmltext = "32140-01.html";
								break;
							}
							case 3:
							{
								htmltext = "32140-03.html";
								break;
							}
							case 4:
							{
								if (!isSimulated)
								{
									if (player.getLevel() >= MIN_LVL)
									{
										addExp(player, 23747100);
										addSp(player, 5699);
										giveAdena(player, 500560, true);
										st.exitQuest(false, true);
									}
									else
									{
										htmltext = getNoQuestLevelRewardMsg(player);
										break;
									}
								}
								htmltext = "32140-04.html";
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		Party party = killer.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public void onKill(Npc npc, PlayerInstance killer)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(3) && (npc.distance3d(killer) <= 1500))
		{
			int killedArcher = st.getInt("killed_" + PHANTOM_ARCHER);
			int killedShaman = st.getInt("killed_" + PHANTOM_SHAMAN);
			int killedMartyr = st.getInt("killed_" + PHANTOM_MARTYR);
			int killedSoldier = st.getInt("killed_" + PHANTOM_SOLDIER);
			int killedWarrior = st.getInt("killed_" + PHANTOM_WARRIOR);
			
			switch (npc.getId())
			{
				case PHANTOM_ARCHER:
				{
					if (killedArcher < 5)
					{
						st.set("killed_" + PHANTOM_ARCHER, ++killedArcher);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case PHANTOM_SHAMAN:
				{
					if (killedShaman < 5)
					{
						st.set("killed_" + PHANTOM_SHAMAN, ++killedShaman);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case PHANTOM_MARTYR:
				{
					if (killedMartyr < 5)
					{
						st.set("killed_" + PHANTOM_MARTYR, ++killedMartyr);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case PHANTOM_SOLDIER:
				{
					if (killedSoldier < 15)
					{
						st.set("killed_" + PHANTOM_SOLDIER, ++killedSoldier);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case PHANTOM_WARRIOR:
				{
					if (killedWarrior < 10)
					{
						st.set("killed_" + PHANTOM_WARRIOR, ++killedWarrior);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedArcher == 5) && (killedShaman == 5) && (killedMartyr == 5) && (killedSoldier == 15) && (killedWarrior == 10))
			{
				st.setCond(4, true);
			}
			sendNpcLogList(killer);
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs.isCond(3))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(PHANTOM_ARCHER, false, qs.getInt("killed_" + PHANTOM_ARCHER)));
			holder.add(new NpcLogListHolder(PHANTOM_SHAMAN, false, qs.getInt("killed_" + PHANTOM_SHAMAN)));
			holder.add(new NpcLogListHolder(PHANTOM_MARTYR, false, qs.getInt("killed_" + PHANTOM_MARTYR)));
			holder.add(new NpcLogListHolder(PHANTOM_SOLDIER, false, qs.getInt("killed_" + PHANTOM_SOLDIER)));
			holder.add(new NpcLogListHolder(PHANTOM_WARRIOR, false, qs.getInt("killed_" + PHANTOM_WARRIOR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
