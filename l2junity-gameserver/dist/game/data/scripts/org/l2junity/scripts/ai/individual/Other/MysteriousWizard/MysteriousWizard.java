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
package org.l2junity.scripts.ai.individual.Other.MysteriousWizard;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.instancezone.Instance;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;
import org.l2junity.scripts.quests.Q10751_WindsOfFateEncounters.Q10751_WindsOfFateEncounters;

/**
 * Mysterious Wizard AI.
 * @author Gladicek
 */
public final class MysteriousWizard extends AbstractNpcAI
{
	// Npc
	private static final int MYSTERIOUS_WIZARD = 33980;
	// Items
	private static final int WIND_SPIRIT_REALMS_RELIC = 39535;
	// Misc
	private static final int FORTRESS_OF_THE_DEAD = 254;
	
	private MysteriousWizard()
	{
		addFirstTalkId(MYSTERIOUS_WIZARD);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		if (npc != null)
		{
			if (event.equals("33980-01.html") && isFotDInstance(npc.getInstanceWorld()))
			{
				htmltext = event;
			}
			else if (event.equals("33980-03.html"))
			{
				final QuestState qs = player.getQuestState(Q10751_WindsOfFateEncounters.class.getSimpleName());
				if ((qs != null) && qs.isCond(6))
				{
					giveItems(player, WIND_SPIRIT_REALMS_RELIC, 1);
					qs.setCond(7, true);
					showOnScreenMsg(player, NpcStringId.RETURN_TO_RAYMOND_OF_THE_TOWN_OF_GLUDIO, ExShowScreenMessage.TOP_CENTER, 8000);
					htmltext = event;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = player.getQuestState(Q10751_WindsOfFateEncounters.class.getSimpleName());
		final Instance world = npc.getInstanceWorld();
		
		if (isFotDInstance(world))
		{
			htmltext = "33980.html";
		}
		else if (qs != null)
		{
			if (qs.isCond(6))
			{
				htmltext = "33980-02.html";
			}
			else if (qs.isCond(7))
			{
				htmltext = "33980-04.html";
			}
		}
		return htmltext;
	}
	
	private boolean isFotDInstance(Instance instance)
	{
		return (instance != null) && (instance.getTemplateId() == FORTRESS_OF_THE_DEAD);
	}
	
	@GameScript
	public static void main()
	{
		new MysteriousWizard();
	}
}