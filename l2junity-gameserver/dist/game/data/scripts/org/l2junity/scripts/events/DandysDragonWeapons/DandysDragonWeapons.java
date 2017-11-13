/*
 * Copyright (C) 2004-2017 L2J DataPack
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
package org.l2junity.scripts.events.DandysDragonWeapons;

import org.l2junity.gameserver.data.xml.impl.MultisellData;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.annotations.Id;
import org.l2junity.gameserver.model.events.annotations.RegisterEvent;
import org.l2junity.gameserver.model.events.annotations.RegisterType;
import org.l2junity.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2junity.gameserver.model.quest.LongTimeEvent;
import org.l2junity.gameserver.model.variables.PlayerVariables;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Dandy's Dragon Weapons.
 * http://www.lineage2.com/en/news/events/dandys-dragon-weapons.php
 * @author ChaosPaladin
 */
public final class DandysDragonWeapons extends LongTimeEvent
{
	// NPCs
	private static final int DANDI = 33930;
	// Items
	private static final int DANDYS_SCROLL_RELEASE_SEAL = 38847;
	private static final int DRAGON_SCROLL_BOOST_WEAPON = 38848;
	
	private DandysDragonWeapons()
	{
		addStartNpc(DANDI);
		addFirstTalkId(DANDI);
		addTalkId(DANDI);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "ev_10th_dandi001.htm";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "ev_10th_dandi001.htm":
			case "ev_10th_dandi002.htm":
			case "ev_10th_dandi003.htm":
			case "ev_10th_dandi004.htm":
			case "ev_10th_dandi005.htm":
			case "ev_10th_dandi006.htm":
			case "ev_10th_dandi007.htm":
			case "ev_10th_dandi008.htm":
			case "ev_10th_dandi009.htm":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(DANDI)
	public final void OnNpcMenuSelect(OnNpcMenuSelect event)
	{
		final PlayerInstance player = event.getTalker();
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == -10001000)
		{
			if (reply == 1)
			{
				MultisellData.getInstance().separateAndSend(2500, player, npc, false); // Custom ID
			}
			else if (reply == 2)
			{
				MultisellData.getInstance().separateAndSend(2501, player, npc, false); // Custom ID
			}
		}
		else if (ask == -10001001)
		{
			if (reply == 1)
			{
				if (!player.getVariables().getBoolean(PlayerVariables.GIVE_DANDI_SCROLL, false))
				{
					final int i = getRandom(10);
					if (i < 9)
					{
						giveItems(player, DANDYS_SCROLL_RELEASE_SEAL, 1);
						showHtmlFile(player, "ev_10th_dandi004.htm");
					}
					else
					{
						giveItems(player, DRAGON_SCROLL_BOOST_WEAPON, 1);
						showHtmlFile(player, "ev_10th_dandi005.htm");
					}
					player.getVariables().set(PlayerVariables.GIVE_DANDI_SCROLL, true);
				}
				else
				{
					showHtmlFile(player, "ev_10th_dandi006.htm");
				}
			}
			else if (reply == 2)
			{
				if (player.getPcCafePoints() < 120)
				{
					showHtmlFile(player, "ev_10th_dandi009.htm");
				}
				else if (!player.getVariables().getBoolean(PlayerVariables.GIVE_DANDI_SCROLL_PCCAFE, false))
				{
					player.getVariables().set(PlayerVariables.GIVE_DANDI_SCROLL_PCCAFE, true);
					player.decreasePcCafePoints(120);
					giveItems(player, DANDYS_SCROLL_RELEASE_SEAL, 1);
					showHtmlFile(player, "ev_10th_dandi007.htm");
				}
				else
				{
					showHtmlFile(player, "ev_10th_dandi008.htm");
				}
			}
		}
	}
	
	@GameScript
	public static void main()
	{
		new DandysDragonWeapons();
	}
}
