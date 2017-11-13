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
package org.l2junity.scripts.ai.individual.Parnassus.EntrancePortalToCrystalCaverns;

import java.util.Calendar;

import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.network.client.send.OnEventTrigger;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;
import org.l2junity.scripts.instances.CrystalCaverns.CrystalCavernsCoralGarden;
import org.l2junity.scripts.instances.CrystalCaverns.CrystalCavernsEmeraldSquare;
import org.l2junity.scripts.instances.CrystalCaverns.CrystalCavernsSteamCorridor;

/**
 * Entrance Portal to Crystal Caverns AI.
 * @author St3eT
 */
public final class EntrancePortalToCrystalCaverns extends AbstractNpcAI
{
	// NPCs
	private static final int ENTRANCE_PORTAL = 33522;
	// Misc
	private static final int EMERALD_SQUARE_TEMPLATE_ID = 163;
	private static final int STEAM_CORRIDOR_TEMPLATE_ID = 164;
	private static final int CORAL_GARDEN_TEMPLATE_ID = 165;
	private static final int BAYLOR_TRIGGER = 24230010;
	private static final int BALOK_TRIGGER = 24230012;
	private static final int EMERALD_SQUARE_TRIGGER = 24230014;
	private static final int STEAM_CORRIDOR_TRIGGER = 24230016;
	private static final int CORAL_GARDEN_TRIGGER = 24230018;
	
	private EntrancePortalToCrystalCaverns()
	{
		addStartNpc(ENTRANCE_PORTAL);
		addTalkId(ENTRANCE_PORTAL);
		addFirstTalkId(ENTRANCE_PORTAL);
		addSpawnId(ENTRANCE_PORTAL);
		addSeeCreatureId(ENTRANCE_PORTAL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			Quest instanceScript = null;
			
			switch (getCurrentInstanceTemplateId())
			{
				case EMERALD_SQUARE_TEMPLATE_ID:
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsEmeraldSquare.class.getSimpleName());
					break;
				case STEAM_CORRIDOR_TEMPLATE_ID:
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsSteamCorridor.class.getSimpleName());
					break;
				case CORAL_GARDEN_TEMPLATE_ID:
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsCoralGarden.class.getSimpleName());
					break;
			}
			
			if (instanceScript != null)
			{
				instanceScript.notifyEvent(event, npc, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "EntrancePortal_" + getCurrentInstanceTemplateId() + ".html";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addRepeatingTimer("LOOP_TIMER", 10000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("LOOP_TIMER"))
		{
			final int currentTemplateId = getCurrentInstanceTemplateId();
			
			World.getInstance().forEachVisibleObjectInRadius(npc, PlayerInstance.class, 5000, pl ->
			{
				updateTriggersForPlayer(player, currentTemplateId);
			});
		}
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			creature.getActingPlayer().sendPacket(new OnEventTrigger(BAYLOR_TRIGGER, true));
			creature.getActingPlayer().sendPacket(new OnEventTrigger(BALOK_TRIGGER, true));
			updateTriggersForPlayer(creature.getActingPlayer(), getCurrentInstanceTemplateId());
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public void updateTriggersForPlayer(PlayerInstance player, int currentTemplateId)
	{
		if (player != null)
		{
			player.sendPacket(new OnEventTrigger(EMERALD_SQUARE_TRIGGER, false));
			player.sendPacket(new OnEventTrigger(STEAM_CORRIDOR_TRIGGER, false));
			player.sendPacket(new OnEventTrigger(CORAL_GARDEN_TRIGGER, false));
			
			switch (currentTemplateId)
			{
				case EMERALD_SQUARE_TEMPLATE_ID:
					player.sendPacket(new OnEventTrigger(EMERALD_SQUARE_TRIGGER, true));
					break;
				case STEAM_CORRIDOR_TEMPLATE_ID:
					player.sendPacket(new OnEventTrigger(STEAM_CORRIDOR_TRIGGER, true));
					break;
				case CORAL_GARDEN_TEMPLATE_ID:
					player.sendPacket(new OnEventTrigger(CORAL_GARDEN_TRIGGER, true));
					break;
			}
		}
	}
	
	public int getCurrentInstanceTemplateId()
	{
		final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int templateId = -1;
		
		switch (day)
		{
			case Calendar.MONDAY:
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
			case Calendar.TUESDAY:
				templateId = (hour < 18) ? CORAL_GARDEN_TEMPLATE_ID : EMERALD_SQUARE_TEMPLATE_ID;
				break;
			case Calendar.WEDNESDAY:
				templateId = (hour < 18) ? STEAM_CORRIDOR_TEMPLATE_ID : CORAL_GARDEN_TEMPLATE_ID;
				break;
			case Calendar.THURSDAY:
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
			case Calendar.FRIDAY:
				templateId = (hour < 18) ? CORAL_GARDEN_TEMPLATE_ID : EMERALD_SQUARE_TEMPLATE_ID;
				break;
			case Calendar.SATURDAY:
				templateId = (hour < 18) ? STEAM_CORRIDOR_TEMPLATE_ID : CORAL_GARDEN_TEMPLATE_ID;
				break;
			case Calendar.SUNDAY:
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
		}
		return templateId;
	}
	
	@GameScript
	public static void main()
	{
		new EntrancePortalToCrystalCaverns();
	}
}