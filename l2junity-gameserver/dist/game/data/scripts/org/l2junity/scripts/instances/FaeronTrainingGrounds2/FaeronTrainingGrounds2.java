/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.instances.FaeronTrainingGrounds2;

import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.Id;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2junity.gameserver.model.events.returns.DamageReturn;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.TutorialShowHtml;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.instances.AbstractInstance;
import org.l2junity.scripts.quests.Q10736_ASpecialPower.Q10736_ASpecialPower;

/**
 * Fearon Training Grounds Instance Zone.
 * @author Sdw, malyelfik
 */
public final class FaeronTrainingGrounds2 extends AbstractInstance
{
	// NPCs
	private static final int KATALIN = 33943;
	private static final int KATALIN_2 = 33945;
	// Monsters
	private static final int FLOATO = 27526;
	private static final int FLOATO2 = 27531;
	private static final int RATEL = 27527;
	// Item
	private static final ItemHolder SOULSHOTS_TRAINING = new ItemHolder(1835, 150);
	// Locations
	private static final Location[] MOB_SPAWNS =
	{
		new Location(-74760, 240773, -3560),
		new Location(-74721, 240513, -3584)
	};
	// Misc
	private static final int TEMPLATE_ID = 252;
	private static final double DAMAGE_BY_SKILL = 0.5d; // Percent
	
	public FaeronTrainingGrounds2()
	{
		super(TEMPLATE_ID);
		addStartNpc(KATALIN, KATALIN_2);
		addFirstTalkId(KATALIN_2);
		addTalkId(KATALIN, KATALIN_2);
		addKillId(FLOATO, FLOATO2, RATEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10736_ASpecialPower.class.getSimpleName());
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "enter_instance":
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			case "exit_instance":
				finishInstance(player, 0);
				break;
			case "33945-03.html":
			{
				if (qs.isCond(6))
				{
					spawnMonsters(RATEL, player);
					showOnScreenMsg(player, NpcStringId.FIGHT_USING_SKILLS, ExShowScreenMessage.TOP_CENTER, 10000);
				}
				else
				{
					final int npcId = (qs.isCond(4)) ? FLOATO2 : FLOATO;
					spawnMonsters(npcId, player);
					showOnScreenMsg(player, NpcStringId.ATTACK_THE_MONSTER, ExShowScreenMessage.TOP_CENTER, 10000);
				}
				htmltext = event;
				break;
			}
			case "33945-07.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					showOnScreenMsg(player, NpcStringId.FIGHT_USING_SKILLS, ExShowScreenMessage.TOP_CENTER, 10000);
					spawnMonsters(RATEL, player);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10736_ASpecialPower.class.getSimpleName());
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		
		if ((npc.getId() == KATALIN_2) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 1:
				{
					qs.setCond(2, true);
					spawnMonsters(FLOATO, player);
					htmltext = "33945-01.html";
					break;
				}
				case 2:
				case 4:
				case 6:
				{
					htmltext = "33945-02.html";
					break;
				}
				case 3:
				{
					if (qs.getInt("ss") == 1)
					{
						spawnMonsters(FLOATO2, player);
						qs.setCond(4, true);
						htmltext = "33945-05.html";
					}
					else
					{
						qs.set("ss", 1);
						giveItems(player, SOULSHOTS_TRAINING);
						showOnScreenMsg(player, NpcStringId.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, ExShowScreenMessage.TOP_CENTER, 10000);
						htmltext = "33945-04.html";
					}
					break;
				}
				case 5:
				{
					player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_004_skill_01.htm", TutorialShowHtml.LARGE_WINDOW));
					htmltext = "33945-06.html";
					break;
				}
				case 7:
				{
					htmltext = "33945-08.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		// Check if monster is inside instance
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		// Remove monster from instance spawn holder
		world.setParameter("Mob" + npc.getScriptValue(), null);
		
		// Handle quest state
		final QuestState qs = killer.getQuestState(Q10736_ASpecialPower.class.getSimpleName());
		if (qs != null)
		{
			switch (npc.getId())
			{
				case FLOATO:
				case FLOATO2:
				{
					if ((qs.isCond(2) || qs.isCond(4)) && onKillQuestChange(killer, qs))
					{
						despawnMonsters(killer);
						if (qs.isCond(5) && (killer.getLevel() < 5))
						{
							addExp(killer, 1_716);
						}
					}
					break;
				}
				case RATEL:
				{
					if (qs.isCond(6) && onKillQuestChange(killer, qs))
					{
						despawnMonsters(killer);
						showOnScreenMsg(killer, NpcStringId.TALK_TO_KATALIN_TO_LEAVE_THE_TRAINING_GROUNDS, ExShowScreenMessage.TOP_CENTER, 10000);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DAMAGE_RECEIVED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(RATEL)
	public DamageReturn onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		final Creature target = event.getTarget();
		if (target.isNpc() && event.getAttacker().isPlayer())
		{
			final QuestState qs = event.getAttacker().getActingPlayer().getQuestState(Q10736_ASpecialPower.class.getSimpleName());
			if ((qs != null) && (qs.isCond(5) || qs.isCond(6)))
			{
				if (event.getSkill() == null)
				{
					return new DamageReturn(true, true, true, 0);
				}
				return new DamageReturn(false, true, false, target.getMaxHp() * DAMAGE_BY_SKILL);
			}
		}
		return null;
	}
	
	/**
	 * Handle death of training monster. When all monsters are killed, quest cond is increased.
	 * @param killer player who killed monster
	 * @param qs quest state of killer
	 * @return {@code true} when all monsters are killed, otherwise {@code false}
	 */
	private boolean onKillQuestChange(PlayerInstance killer, QuestState qs)
	{
		final int value = qs.getMemoStateEx(Q10736_ASpecialPower.KILL_COUNT_VAR) + 1;
		if (value >= 2)
		{
			qs.setCond(qs.getCond() + 1, true);
			qs.setMemoStateEx(Q10736_ASpecialPower.KILL_COUNT_VAR, 0);
			return true;
		}
		playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		qs.setMemoStateEx(Q10736_ASpecialPower.KILL_COUNT_VAR, value);
		qs.getQuest().sendNpcLogList(killer);
		return false;
	}
	
	/**
	 * Spawn training monsters inside instance
	 * @param npcId template id of training monster
	 * @param player player that owns instance
	 */
	private void spawnMonsters(int npcId, PlayerInstance player)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			final StatsSet params = world.getParameters();
			for (int i = 0; i < MOB_SPAWNS.length; i++)
			{
				if (params.getObject("Mob" + i, Npc.class) == null)
				{
					final Npc npc = addSpawn(npcId, MOB_SPAWNS[i], false, 0, false, world.getId());
					npc.setScriptValue(i);
					params.set("Mob" + i, npc);
				}
			}
		}
	}
	
	/**
	 * Despawn training monsters inside instance
	 * @param player player that owns instance
	 */
	private void despawnMonsters(PlayerInstance player)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			final StatsSet params = world.getParameters();
			for (int i = 0; i < MOB_SPAWNS.length; i++)
			{
				final Npc mob = params.getObject("Mob" + i, Npc.class);
				if (mob != null)
				{
					mob.deleteMe();
					params.remove("Mob" + i);
				}
			}
		}
	}
	
	@GameScript
	public static void main()
	{
		new FaeronTrainingGrounds2();
	}
}