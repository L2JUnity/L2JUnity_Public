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
package org.l2junity.scripts.quests.Q10389_TheVoiceOfAuthority;

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
import org.l2junity.gameserver.network.client.send.string.NpcStringId;

import org.l2junity.scripts.quests.Q10388_ConspiracyBehindDoors.Q10388_ConspiracyBehindDoors;

/**
 * The Voice Of Authority (10389)
 * @author netvirus
 */
public class Q10389_TheVoiceOfAuthority extends Quest
{
	// NPC
	private static final int RAZEN = 33803;
	// Monters
	private static final int OLD_ARISTOCRATS_SOLFIER = 22139;
	private static final int RESURRECTED_WORKER = 22140;
	private static final int FORGOTTEN_VICTIM = 22141;
	private static final int RITUAL_OFFRERING = 22149;
	private static final int RITUAL_SACRIFICE = 22145;
	private static final int RESURRECTED_TEMPLE_KNIGHT = 22144;
	private static final int TRIOLS_BELIEVER = 22143;
	private static final int TRIOLS_HIGH_PRIEST = 22146;
	private static final int TRIOLS_LAYPERSON = 22142;
	// Items
	private static final int PAGANS_BADGE = 8067;
	private static final int PAGANS_BADGE1 = 36229;
	// Misc
	private static final int MIN_LVL = 97;
	
	public Q10389_TheVoiceOfAuthority()
	{
		super(10389);
		addStartNpc(RAZEN);
		addTalkId(RAZEN);
		addKillId(OLD_ARISTOCRATS_SOLFIER, RESURRECTED_WORKER, FORGOTTEN_VICTIM, RITUAL_OFFRERING, RITUAL_SACRIFICE, RESURRECTED_TEMPLE_KNIGHT, TRIOLS_BELIEVER, TRIOLS_HIGH_PRIEST, TRIOLS_LAYPERSON);
		addCondMinLevel(MIN_LVL, "33803-02.htm");
		addCondCompletedQuest(Q10388_ConspiracyBehindDoors.class.getSimpleName(), "33803-02.htm");
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
			case "33803-04.htm":
			{
				htmltext = event;
				break;
			}
			case "33803-05":
			{
				if (!hasQuestItems(player, PAGANS_BADGE))
				{
					htmltext = "33803-05.htm";
				}
				else
				{
					htmltext = "33803-05a.htm";
				}
				break;
			}
			case "33803-05b.htm":
			{
				if (hasQuestItems(player, PAGANS_BADGE))
				{
					htmltext = event;
				}
				break;
			}
			case "33803-05c.html":
			{
				if (hasQuestItems(player, PAGANS_BADGE))
				{
					giveItems(player, PAGANS_BADGE1, 1);
					takeItems(player, PAGANS_BADGE, 1);
					addExp(player, 592767000);
					addSp(player, 142264);
					giveAdena(player, 1302720, true);
					st.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "33803-06.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33803-09.html":
			{
				if (st.isCond(2))
				{
					if (player.getLevel() >= MIN_LVL)
					{
						giveItems(player, PAGANS_BADGE1, 1);
						addExp(player, 592767000);
						addSp(player, 142264);
						giveAdena(player, 1302720, true);
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
		
		if (npc.getId() == RAZEN)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
				{
					htmltext = "33803-03.html";
					break;
				}
				case State.CREATED:
				{
					htmltext = "33803-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "33803-07.html";
							break;
						}
						case 2:
						{
							htmltext = "33803-08.html";
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
		if ((st != null) && st.isCond(1) && (npc.distance3d(killer) <= 1500))
		{
			int mobs = st.getInt("killed");
			switch (npc.getId())
			{
				case OLD_ARISTOCRATS_SOLFIER:
				case RESURRECTED_WORKER:
				case FORGOTTEN_VICTIM:
				case RITUAL_OFFRERING:
				case RITUAL_SACRIFICE:
				case RESURRECTED_TEMPLE_KNIGHT:
				case TRIOLS_BELIEVER:
				case TRIOLS_HIGH_PRIEST:
				case TRIOLS_LAYPERSON:
				{
					if (mobs < 30)
					{
						st.set("killed", ++mobs);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			if (st.isCond(1) && (mobs >= 30))
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
			holder.add(new NpcLogListHolder(NpcStringId.ELIMINATE_THE_PAGANS_IN_THE_ANTEROOM, qs.getInt("killed")));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
