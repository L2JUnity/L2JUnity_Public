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
package org.l2junity.scripts.ai.individual.FantasyIsle.FantasyIslePaddy;

import org.l2junity.gameserver.data.xml.impl.MultisellData;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.itemcontainer.Inventory;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Fantasy Isle Paddy AI.
 * @author ChaosPaladin
 */
public final class FantasyIslePaddy extends AbstractNpcAI
{
	// NPC
	private static final int PADDIES = 32378;
	// Items
	private static final int G_SCRL_OF_ENCH_WOLF_SHIRT = 21581;
	private static final int PI_TSHIRT_ENCHANT_SCROLL_2011 = 34627;
	private static final int PI_TSHIRT_OF_PA1 = 34623;
	private static final int PI_TSHIRT_OF_SA1 = 34624;
	private static final int PI_TSHIRT_OF_EV1 = 34625;
	private static final int PI_TSHIRT_OF_MA1 = 34626;
	private static final int PI_TSHIRT_OF_EL = 37718;
	private static final int PI_TSHIRT_OF_EL_EX_STONE = 37723;
	private static final int REFLECT_ATTACK_TSHIRT = 46193;
	private static final int REFLECT_MAGIC_TSHIRT = 46194;
	// Locations
	private static final Location[] UNDERGROUND_COLISEUM =
	{
		new Location(-81896, -49589, -10352),
		new Location(-82271, -49196, -10352),
		new Location(-81886, -48784, -10352),
		new Location(-81490, -49167, -10352),
	};
	private static final Location[] KRATEIS_CUBE =
	{
		new Location(-70411, -70958, -1416),
		new Location(-70522, -71026, -1416),
		new Location(-70293, -71029, -1416),
	};
	private static final Location HANDYS_BLOCK_CHECKER_ARENA = new Location(-57328, -60566, -2320);
	
	private FantasyIslePaddy()
	{
		addStartNpc(PADDIES);
		addTalkId(PADDIES);
		addFirstTalkId(PADDIES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "tp_undergroundcoliseum":
			{
				player.teleToLocation(UNDERGROUND_COLISEUM[getRandom(UNDERGROUND_COLISEUM.length)]);
				break;
			}
			case "tp_krateis_cube":
			{
				player.teleToLocation(KRATEIS_CUBE[getRandom(KRATEIS_CUBE.length)]);
				break;
			}
			case "tp_handys_arena":
			{
				player.teleToLocation(HANDYS_BLOCK_CHECKER_ARENA);
				break;
			}
			case "exc_elem_shirt":
			{
				htmltext = "32378-01.htm";
				break;
			}
			case "exc_wolf_shirt":
			{
				htmltext = "32378-02.htm";
				break;
			}
			case "buy_elem_shirt":
			{
				if (player.getAdena() < 8888)
				{
					htmltext = "32378-noAdena1.htm";
				}
				else
				{
					takeItems(player, Inventory.ADENA_ID, 8888);
					giveItems(player, PI_TSHIRT_ENCHANT_SCROLL_2011, 1);
					htmltext = "32378-01.htm";
					break;
				}
				break;
			}
			case "buy_wolf_shirt":
			{
				if (player.getAdena() < 7777)
				{
					htmltext = "32378-noAdena2.htm";
				}
				else
				{
					takeItems(player, Inventory.ADENA_ID, 7777);
					giveItems(player, G_SCRL_OF_ENCH_WOLF_SHIRT, 1);
					htmltext = "32378-02.htm";
					break;
				}
				break;
			}
			case "exc_coins":
			{
				MultisellData.getInstance().separateAndSend(643, player, npc, false);
				break;
			}
			case "32378-03.htm":
			case "32378-04.htm":
			{
				htmltext = event;
				break;
			}
			case "exc_shirt1":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_PA1) && (getEnchantLevel(player, PI_TSHIRT_OF_PA1) >= 7))
				{
					takeItems(player, PI_TSHIRT_OF_PA1, 1);
					giveItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
				}
				else
				{
					htmltext = "32378-noItem3.htm";
				}
				break;
			}
			case "exc_shirt2":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_SA1) && (getEnchantLevel(player, PI_TSHIRT_OF_SA1) >= 7))
				{
					takeItems(player, PI_TSHIRT_OF_SA1, 1);
					giveItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
				}
				else
				{
					htmltext = "32378-noItem3.htm";
				}
				break;
			}
			case "exc_shirt3":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_EV1) && (getEnchantLevel(player, PI_TSHIRT_OF_EV1) >= 7))
				{
					takeItems(player, PI_TSHIRT_OF_EV1, 1);
					giveItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
				}
				else
				{
					htmltext = "32378-noItem3.htm";
				}
				break;
			}
			case "exc_shirt4":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_MA1) && (getEnchantLevel(player, PI_TSHIRT_OF_MA1) >= 7))
				{
					takeItems(player, PI_TSHIRT_OF_MA1, 1);
					giveItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
				}
				else
				{
					htmltext = "32378-noItem3.htm";
					break;
				}
			}
			case "enchant_shirt1":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_PA1, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, PI_TSHIRT_OF_PA1) == 8))
				{
					takeItems(player, PI_TSHIRT_OF_PA1, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, PI_TSHIRT_OF_PA1, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt2":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_SA1, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, PI_TSHIRT_OF_SA1) == 8))
				{
					takeItems(player, PI_TSHIRT_OF_SA1, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, PI_TSHIRT_OF_SA1, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt3":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_EV1, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, PI_TSHIRT_OF_EV1) == 8))
				{
					takeItems(player, PI_TSHIRT_OF_EV1, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, PI_TSHIRT_OF_EV1, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt4":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_MA1, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, PI_TSHIRT_OF_MA1) == 8))
				{
					takeItems(player, PI_TSHIRT_OF_MA1, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, PI_TSHIRT_OF_MA1, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt5":
			{
				if (hasQuestItems(player, REFLECT_ATTACK_TSHIRT, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, REFLECT_ATTACK_TSHIRT) == 8))
				{
					takeItems(player, REFLECT_ATTACK_TSHIRT, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, REFLECT_ATTACK_TSHIRT, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt6":
			{
				if (hasQuestItems(player, REFLECT_MAGIC_TSHIRT, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, REFLECT_MAGIC_TSHIRT) == 8))
				{
					takeItems(player, REFLECT_MAGIC_TSHIRT, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, REFLECT_MAGIC_TSHIRT, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
			case "enchant_shirt7":
			{
				if (hasQuestItems(player, PI_TSHIRT_OF_EL, PI_TSHIRT_OF_EL_EX_STONE) && (getEnchantLevel(player, PI_TSHIRT_OF_EL) == 8))
				{
					takeItems(player, PI_TSHIRT_OF_EL, 1);
					takeItems(player, PI_TSHIRT_OF_EL_EX_STONE, 1);
					giveItems(player, PI_TSHIRT_OF_EL, 1, 10, false);
				}
				else
				{
					htmltext = "32378-noItem4.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "32378.htm";
	}
	
	@GameScript
	public static void main()
	{
		new FantasyIslePaddy();
	}
}
