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
package org.l2junity.scripts.events.EveTheFortuneTeller;

import org.l2junity.gameserver.enums.LuckyGameType;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.LongTimeEvent;
import org.l2junity.gameserver.network.client.send.luckygame.ExStartLuckyGame;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * @author Sdw
 */
public class EveTheFortuneTeller extends LongTimeEvent
{
	// NPCs
	private static final int EVE = 8542;
	
	private EveTheFortuneTeller()
	{
		addStartNpc(EVE);
		addFirstTalkId(EVE);
		addTalkId(EVE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "8542.htm":
			case "8542-01.htm":
			{
				htmltext = event;
				break;
			}
			case "FortuneReading":
			{
				player.sendPacket(new ExStartLuckyGame(LuckyGameType.NORMAL, player.getFortuneReadingTickets()));
				break;
			}
			case "LuxuryFortuneReading":
			{
				player.sendPacket(new ExStartLuckyGame(LuckyGameType.LUXURY, player.getLuxuryFortuneReadingTickets()));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "8542.htm";
	}
	
	@GameScript
	public static void main()
	{
		new EveTheFortuneTeller();
	}
}
