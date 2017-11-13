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
package org.l2junity.scripts.quests.Q10371_GraspThyPower;

import java.util.HashSet;
import java.util.Set;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.NpcLogListHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.State;

import org.l2junity.scripts.quests.Q10370_MenacingTimes.Q10370_MenacingTimes;

/**
 * Grasp Thy Power (10371)
 * @author netvirus
 */
public class Q10371_GraspThyPower extends Quest
{
	// NPCs
	private static final int GERKENSHTEIN = 33648;
	// Monsters
	private static final int SUCCUBUS_SOLDIER = 23181;
	private static final int SUCCUBUS_WARRIOR = 23182;
	private static final int SUCCUBUS_ARCHER = 23183;
	private static final int SUCCUBUS_SHAMAN = 23184;
	private static final int BLOODY_SUCCUBUS = 23185;
	// Items
	private static final int SUCCUBUS_ESSENCE = 34766;
	// Misc
	private static final int MIN_LVL = 76;
	private static final int MAX_LVL = 81;
	
	public Q10371_GraspThyPower()
	{
		super(10371);
		addStartNpc(GERKENSHTEIN);
		addTalkId(GERKENSHTEIN);
		addKillId(SUCCUBUS_SOLDIER, SUCCUBUS_WARRIOR, SUCCUBUS_ARCHER, SUCCUBUS_SHAMAN, BLOODY_SUCCUBUS);
		registerQuestItems(SUCCUBUS_ESSENCE);
		addCondLevel(MIN_LVL, MAX_LVL, "33648-02.htm");
		addCondCompletedQuest(Q10370_MenacingTimes.class.getSimpleName(), "33648-02.htm");
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
			case "31292-03.html":
			case "31292-04.htm":
			case "33648-05.htm":
			case "33648-09.html":
			{
				htmltext = event;
				break;
			}
			case "33648-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33648-10.html":
			{
				if (player.getLevel() >= MIN_LVL)
				{
					htmltext = event;
					addExp(player, 22641900);
					addSp(player, 5434);
					giveAdena(player, 484990, true);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
				}
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
		
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				htmltext = "33648-03.html";
				break;
			}
			case State.CREATED:
			{
				htmltext = "33648-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == GERKENSHTEIN)
				{
					if (st.isCond(1))
					{
						htmltext = "33648-07.html";
					}
					else if (st.isCond(2))
					{
						htmltext = "33648-08.html";
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
		final Party party = killer.getParty();
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
		
		if ((st != null) && st.isCond(1) && (npc.distance3d(killer) <= 1500))
		{
			int killedSoldier = st.getInt("killed_" + SUCCUBUS_SOLDIER);
			int killedWarrior = st.getInt("killed_" + SUCCUBUS_WARRIOR);
			int killedArcher = st.getInt("killed_" + SUCCUBUS_ARCHER);
			int killedShaman = st.getInt("killed_" + SUCCUBUS_SHAMAN);
			int killedBloody = st.getInt("killed_" + BLOODY_SUCCUBUS);
			
			switch (npc.getId())
			{
				case SUCCUBUS_SOLDIER:
				{
					if (killedSoldier < 12)
					{
						st.set("killed_" + SUCCUBUS_SOLDIER, ++killedSoldier);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_WARRIOR:
				{
					if (killedWarrior < 12)
					{
						st.set("killed_" + SUCCUBUS_WARRIOR, ++killedWarrior);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_ARCHER:
				{
					if (killedArcher < 8)
					{
						st.set("killed_" + SUCCUBUS_ARCHER, ++killedArcher);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_SHAMAN:
				{
					if (killedShaman < 8)
					{
						st.set("killed_" + SUCCUBUS_SHAMAN, ++killedShaman);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case BLOODY_SUCCUBUS:
				{
					if (killedBloody < 5)
					{
						st.set("killed_" + BLOODY_SUCCUBUS, ++killedBloody);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedSoldier >= 12) && (killedWarrior >= 12) && (killedArcher >= 8) && (killedShaman >= 8) && (killedBloody >= 5))
			{
				st.setCond(2, true);
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
			holder.add(new NpcLogListHolder(SUCCUBUS_SOLDIER, false, qs.getInt("killed_" + SUCCUBUS_SOLDIER)));
			holder.add(new NpcLogListHolder(SUCCUBUS_WARRIOR, false, qs.getInt("killed_" + SUCCUBUS_WARRIOR)));
			holder.add(new NpcLogListHolder(SUCCUBUS_ARCHER, false, qs.getInt("killed_" + SUCCUBUS_ARCHER)));
			holder.add(new NpcLogListHolder(SUCCUBUS_SHAMAN, false, qs.getInt("killed_" + SUCCUBUS_SHAMAN)));
			holder.add(new NpcLogListHolder(BLOODY_SUCCUBUS, false, qs.getInt("killed_" + BLOODY_SUCCUBUS)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
