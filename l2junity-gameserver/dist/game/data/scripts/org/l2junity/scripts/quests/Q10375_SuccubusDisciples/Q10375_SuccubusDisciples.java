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
package org.l2junity.scripts.quests.Q10375_SuccubusDisciples;

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

import org.l2junity.scripts.quests.Q10374_ThatPlaceSuccubus.Q10374_ThatPlaceSuccubus;

/**
 * Succubus Disciples (10375)
 * @author netvirus
 */
public class Q10375_SuccubusDisciples extends Quest
{
	// NPCs
	private static final int ZENYA = 32140;
	// Monsters
	private static final int SUCCUBUS_OF_DEATH = 23191;
	private static final int SUCCUBUS_OF_DARKNESS = 23192;
	private static final int SUCCUBUS_OF_LUNACY = 23197;
	private static final int SUCCUBUS_OF_SILENCE = 23198;
	// Misc
	private static final int MIN_LVL = 80;
	
	public Q10375_SuccubusDisciples()
	{
		super(10375);
		addStartNpc(ZENYA);
		addTalkId(ZENYA);
		addKillId(SUCCUBUS_OF_DEATH, SUCCUBUS_OF_DARKNESS, SUCCUBUS_OF_LUNACY, SUCCUBUS_OF_SILENCE);
		addCondMinLevel(MIN_LVL, "32140-04.htm");
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "32140-03.htm");
		addCondCompletedQuest(Q10374_ThatPlaceSuccubus.class.getSimpleName(), "");
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
			case "32140-02.htm":
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
			case "32140-09.html":
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
		
		if (npc.getId() == ZENYA)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
				{
					htmltext = "32140-05.html";
					break;
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
							htmltext = "32140-08.html";
							break;
						}
						case 3:
						{
							htmltext = "32140-10.html";
							break;
						}
						case 4:
						{
							if (!isSimulated)
							{
								if (player.getLevel() >= MIN_LVL)
								{
									giveAdena(player, 498700, true);
									addExp(player, 24782300);
									addSp(player, 5947);
									st.exitQuest(false, true);
								}
								else
								{
									htmltext = getNoQuestLevelRewardMsg(player);
									break;
								}
							}
							htmltext = "32140-11.html";
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
		if ((st != null) && st.isStarted() && (npc.distance3d(killer) <= 1500))
		{
			int succubusOfDeath = st.getInt("killed_" + SUCCUBUS_OF_DEATH);
			int succubusOfDarkness = st.getInt("killed_" + SUCCUBUS_OF_DARKNESS);
			int succubusOfLunacy = st.getInt("killed_" + SUCCUBUS_OF_LUNACY);
			int succubusOfSilence = st.getInt("killed_" + SUCCUBUS_OF_SILENCE);
			
			switch (npc.getId())
			{
				case SUCCUBUS_OF_DEATH:
				{
					if (st.isCond(1) && (succubusOfDeath < 5))
					{
						st.set("killed_" + SUCCUBUS_OF_DEATH, ++succubusOfDeath);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_OF_DARKNESS:
				{
					if (st.isCond(1) && (succubusOfDarkness < 5))
					{
						st.set("killed_" + SUCCUBUS_OF_DARKNESS, ++succubusOfDarkness);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_OF_LUNACY:
				{
					if (st.isCond(3) && (succubusOfLunacy < 5))
					{
						st.set("killed_" + SUCCUBUS_OF_LUNACY, ++succubusOfLunacy);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_OF_SILENCE:
				{
					if (st.isCond(3) && (succubusOfSilence < 5))
					{
						st.set("killed_" + SUCCUBUS_OF_SILENCE, ++succubusOfSilence);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if (st.isCond(1) && (succubusOfDeath == 5) && (succubusOfDarkness == 5))
			{
				st.setCond(2, true);
			}
			else if (st.isCond(3) && (succubusOfLunacy == 5) && (succubusOfSilence == 5))
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
		if (qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(SUCCUBUS_OF_DEATH, false, qs.getInt("killed_" + SUCCUBUS_OF_DEATH)));
			holder.add(new NpcLogListHolder(SUCCUBUS_OF_DARKNESS, false, qs.getInt("killed_" + SUCCUBUS_OF_DARKNESS)));
			return holder;
		}
		else if (qs.isCond(3))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(SUCCUBUS_OF_LUNACY, false, qs.getInt("killed_" + SUCCUBUS_OF_LUNACY)));
			holder.add(new NpcLogListHolder(SUCCUBUS_OF_SILENCE, false, qs.getInt("killed_" + SUCCUBUS_OF_SILENCE)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
