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
package org.l2junity.scripts.events.LetterCollector;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.quest.LongTimeEvent;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Angel Cat's Letter Collector Event AI
 * @author ChaosPaladin
 */
public class LetterCollector extends LongTimeEvent
{
	// Npc
	private static final int G_EV_COLLECTOR = 4313;
	// Items
	private static final int GEMSTONE_A = 2133;
	private static final int GEMSTONE_S = 2134;
	private static final int ALPHABET_A = 3875;
	private static final int ALPHABET_C = 3876;
	private static final int ALPHABET_E = 3877;
	private static final int ALPHABET_F = 3878;
	private static final int ALPHABET_G = 3879;
	private static final int ALPHABET_H = 3880;
	private static final int ALPHABET_I = 3881;
	private static final int ALPHABET_L = 3882;
	private static final int ALPHABET_N = 3883;
	private static final int ALPHABET_O = 3884;
	private static final int ALPHABET_R = 3885;
	private static final int ALPHABET_S = 3886;
	private static final int ALPHABET_T = 3887;
	private static final int ALPHABET_II = 3888;
	private static final int RING_OF_BAIUM = 6658;
	private static final int RING_OF_QUEEN_ANT = 6660;
	private static final int ALPHABET_Y = 13417;
	private static final int QUICK_HEALING_POTION2 = 14701;
	private static final int GEMSTONE_R = 19440;
	private static final int CURSED_FREE_SCROLL_R = 19444;
	private static final int CURSED_FREE_SCROLL_R95 = 19445;
	private static final int CURSED_FREE_SCROLL_R99 = 19446;
	private static final int EV_DESTRUCTION_SCRL_OF_ENCH_WP_S_EVENT = 33843;
	private static final int EV_DESTRUCTION_SCRL_OF_ENCH_WP_R_EVENT = 33843;
	private static final int EV_DESTRUCTION_SCRL_OF_ENCH_AM_S_EVENT = 33848;
	private static final int EV_DESTRUCTION_SCRL_OF_ENCH_AM_R_EVENT = 33849;
	private static final int SCROLL_OF_BLESSING_EVENT_R1 = 34774;
	private static final int SCROLL_OF_BLESSING_EVENT_R2 = 34775;
	private static final int SCROLL_OF_BLESSING_EVENT_R95 = 34776;
	private static final int SCROLL_OF_BLESSING_EVENT_R99 = 34777;
	private static final int BLESSED_SCRL_OF_ENCH_AM_S_EVENT_LUCKY = 34786;
	private static final int BLESSED_SCRL_OF_ENCH_AM_R_EVENT_LUCKY = 34787;
	private static final int BLESSED_SCRL_OF_ENCH_WP_S_EVENT_LUCKY = 34788;
	private static final int BLESSED_SCRL_OF_ENCH_WP_R_EVENT_LUCKY = 34789;
	private static final int HIGH_ORE_OF_FIRE_EVENT_LUCKY = 34796;
	private static final int HIGH_ORE_OF_WATER_EVENT_LUCKY = 34797;
	private static final int HIGH_ORE_OF_EARTH_EVENT_LUCKY = 34798;
	private static final int HIGH_ORE_OF_WIND_EVENT_LUCKY = 34799;
	private static final int HIGH_ORE_OF_UNHOLY_EVENT_LUCKY = 34800;
	private static final int HIGH_ORE_OF_HOLY_EVENT_LUCKY = 34801;
	private static final int BOX_OF_SOULSTONE_R_EVENT_LUCKY = 34823;
	private static final int BOX_OF_SOULSTONE_R95_EVENT_LUCKY = 34824;
	private static final int BOX_OF_SOULSTONE_R99_EVENT_LUCKY = 34825;
	private static final int ENCH_SCRL_HAIRACC_EVENT = 34940;
	private static final int GENERAL_HAIRACC_EVENT = 34941;
	private static final int BOX_OF_LV2_SPECIAL_DYE_EVENT = 34942;
	private static final int BOX_OF_LV2_RARE_DYE_EVENT = 34943;
	private static final int BOX_OF_LV2_UNIQUE_DYE_EVENT = 34944;
	private static final int BOX_OF_LV3_SPECIAL_DYE_EVENT = 34945;
	private static final int BOX_OF_LV3_RARE_DYE_EVENT = 34946;
	private static final int BOX_OF_LV3_UNIQUE_DYE_EVENT = 34947;
	private static final int TOWN_RETURN_SCROLL_EVENT = 34948;
	private static final int EV_GIFT_OF_COLLECTOR_EVENT_8TH = 34949;
	private static final int BOX_OF_LV4_SPECIAL_DYE_EVENT = 34950;
	private static final int BOX_OF_LV4_RARE_DYE_EVENT = 34951;
	private static final int BOX_OF_LV4_UNIQUE_DYE_EVENT = 34952;
	private static final int BOX_OF_LV5_SPECIAL_DYE_EVENT = 34953;
	private static final int BOX_OF_LV5_RARE_DYE_EVENT = 34954;
	private static final int BOX_OF_LV5_UNIQUE_DYE_EVENT = 34955;
	private static final int ALPHABET_M = 34956;
	
	private LetterCollector()
	{
		addStartNpc(G_EV_COLLECTOR);
		addTalkId(G_EV_COLLECTOR);
		addFirstTalkId(G_EV_COLLECTOR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "g_ev_collector001.htm":
			case "g_ev_collector002.htm":
			{
				htmltext = event;
				break;
			}
			case "lineageii":
			{
				if (hasQuestItems(player, ALPHABET_E, ALPHABET_L, ALPHABET_I, ALPHABET_N, ALPHABET_A, ALPHABET_G, ALPHABET_II))
				{
					takeItems(player, ALPHABET_E, 2);
					takeItems(player, ALPHABET_L, 1);
					takeItems(player, ALPHABET_I, 1);
					takeItems(player, ALPHABET_N, 1);
					takeItems(player, ALPHABET_A, 1);
					takeItems(player, ALPHABET_II, 1);
					giveItems(player, EV_GIFT_OF_COLLECTOR_EVENT_8TH, 1);
					int i1 = Rnd.get(10000);
					if (i1 < 1)
					{
						giveItems(player, RING_OF_BAIUM, 1);
					}
					else if (i1 < 31)
					{
						int i2 = Rnd.get(30);
						if (i2 < 2)
						{
							giveItems(player, SCROLL_OF_BLESSING_EVENT_R99, 1);
						}
						else if (i2 < 5)
						{
							giveItems(player, SCROLL_OF_BLESSING_EVENT_R95, 1);
						}
						else if (i2 < 10)
						{
							giveItems(player, SCROLL_OF_BLESSING_EVENT_R2, 1);
						}
						else if (i2 < 17)
						{
							giveItems(player, SCROLL_OF_BLESSING_EVENT_R1, 1);
						}
						else if (i2 < 19)
						{
							giveItems(player, CURSED_FREE_SCROLL_R99, 1);
						}
						else if (i2 < 22)
						{
							giveItems(player, CURSED_FREE_SCROLL_R95, 1);
						}
						else if (i2 < 30)
						{
							giveItems(player, CURSED_FREE_SCROLL_R, 1);
						}
					}
					else if (i1 < 2044)
					{
						int i2 = Rnd.get(2013);
						if (i2 < 2)
						{
							giveItems(player, BOX_OF_SOULSTONE_R99_EVENT_LUCKY, 1);
						}
						else if (i2 < 5)
						{
							giveItems(player, BOX_OF_SOULSTONE_R95_EVENT_LUCKY, 1);
						}
						else if (i2 < 13)
						{
							giveItems(player, BOX_OF_SOULSTONE_R_EVENT_LUCKY, 1);
						}
						else if (i2 < 1013)
						{
							giveItems(player, ENCH_SCRL_HAIRACC_EVENT, 1);
						}
						else if (i2 < 2013)
						{
							giveItems(player, GENERAL_HAIRACC_EVENT, 1);
						}
					}
					else if (i1 < 7844)
					{
						int i2 = Rnd.get(58);
						if (i2 < 10)
						{
							giveItems(player, GEMSTONE_R, 1);
						}
						else if (i2 < 18)
						{
							giveItems(player, GEMSTONE_S, 3);
						}
						else if (i2 < 26)
						{
							giveItems(player, GEMSTONE_A, 4);
						}
						else if (i2 < 42)
						{
							giveItems(player, TOWN_RETURN_SCROLL_EVENT, 2);
						}
						else if (i2 < 58)
						{
							giveItems(player, QUICK_HEALING_POTION2, 5);
						}
					}
				}
				else
				{
					htmltext = "g_ev_collector005.htm";
				}
				break;
			}
			case "ncsoft":
			{
				if (hasQuestItems(player, ALPHABET_N, ALPHABET_C, ALPHABET_S, ALPHABET_O, ALPHABET_F, ALPHABET_T))
				{
					takeItems(player, ALPHABET_N, 1);
					takeItems(player, ALPHABET_C, 1);
					takeItems(player, ALPHABET_S, 1);
					takeItems(player, ALPHABET_O, 1);
					takeItems(player, ALPHABET_F, 1);
					takeItems(player, ALPHABET_T, 1);
					giveItems(player, EV_GIFT_OF_COLLECTOR_EVENT_8TH, 1);
					int i1 = Rnd.get(10000);
					if (i1 < 1)
					{
						giveItems(player, RING_OF_QUEEN_ANT, 1);
					}
					else if (i1 < 44)
					{
						int i2 = Rnd.get(43);
						if (i2 < 2)
						{
							giveItems(player, EV_DESTRUCTION_SCRL_OF_ENCH_WP_R_EVENT, 1);
						}
						else if (i2 < 5)
						{
							giveItems(player, EV_DESTRUCTION_SCRL_OF_ENCH_AM_R_EVENT, 1);
						}
						else if (i2 < 9)
						{
							giveItems(player, BLESSED_SCRL_OF_ENCH_WP_R_EVENT_LUCKY, 1);
						}
						else if (i2 < 14)
						{
							giveItems(player, BLESSED_SCRL_OF_ENCH_AM_R_EVENT_LUCKY, 1);
						}
						else if (i2 < 17)
						{
							giveItems(player, EV_DESTRUCTION_SCRL_OF_ENCH_WP_S_EVENT, 1);
						}
						else if (i2 < 22)
						{
							giveItems(player, EV_DESTRUCTION_SCRL_OF_ENCH_AM_S_EVENT, 1);
						}
						else if (i2 < 30)
						{
							giveItems(player, BLESSED_SCRL_OF_ENCH_WP_S_EVENT_LUCKY, 1);
							break;
						}
						else if (i2 < 43)
						{
							giveItems(player, BLESSED_SCRL_OF_ENCH_AM_S_EVENT_LUCKY, 1);
							break;
						}
					}
					else if (i1 < 2007)
					{
						int i2 = Rnd.get(1963);
						if (i2 < 2)
						{
							giveItems(player, BOX_OF_SOULSTONE_R99_EVENT_LUCKY, 1);
						}
						else if (i2 < 5)
						{
							giveItems(player, BOX_OF_SOULSTONE_R95_EVENT_LUCKY, 1);
						}
						else if (i2 < 13)
						{
							giveItems(player, BOX_OF_SOULSTONE_R_EVENT_LUCKY, 1);
						}
						else if (i2 < 338)
						{
							giveItems(player, HIGH_ORE_OF_FIRE_EVENT_LUCKY, 1);
						}
						else if (i2 < 663)
						{
							giveItems(player, HIGH_ORE_OF_WATER_EVENT_LUCKY, 1);
						}
						else if (i2 < 988)
						{
							giveItems(player, HIGH_ORE_OF_EARTH_EVENT_LUCKY, 1);
						}
						else if (i2 < 1313)
						{
							giveItems(player, HIGH_ORE_OF_WIND_EVENT_LUCKY, 1);
						}
						else if (i2 < 1638)
						{
							giveItems(player, HIGH_ORE_OF_UNHOLY_EVENT_LUCKY, 1);
						}
						else if (i2 < 1963)
						{
							giveItems(player, HIGH_ORE_OF_HOLY_EVENT_LUCKY, 1);
						}
					}
					else if (i1 < 7807)
					{
						int i2 = Rnd.get(58);
						if (i2 < 10)
						{
							giveItems(player, GEMSTONE_R, 1);
						}
						else if (i2 < 18)
						{
							giveItems(player, GEMSTONE_S, 3);
						}
						else if (i2 < 26)
						{
							giveItems(player, GEMSTONE_A, 4);
						}
						else if (i2 < 42)
						{
							giveItems(player, TOWN_RETURN_SCROLL_EVENT, 2);
						}
						else if (i2 < 58)
						{
							giveItems(player, QUICK_HEALING_POTION2, 5);
						}
					}
				}
				else
				{
					htmltext = "g_ev_collector005.htm";
				}
				break;
			}
			case "harmony":
			{
				if (hasQuestItems(player, ALPHABET_H, ALPHABET_A, ALPHABET_R, ALPHABET_M, ALPHABET_O, ALPHABET_N, ALPHABET_Y))
				{
					takeItems(player, ALPHABET_H, 1);
					takeItems(player, ALPHABET_A, 1);
					takeItems(player, ALPHABET_R, 1);
					takeItems(player, ALPHABET_M, 1);
					takeItems(player, ALPHABET_O, 1);
					takeItems(player, ALPHABET_N, 1);
					takeItems(player, ALPHABET_Y, 1);
					giveItems(player, EV_GIFT_OF_COLLECTOR_EVENT_8TH, 1);
					int i1 = Rnd.get(10000);
					if (i1 < 2)
					{
						int i2 = Rnd.get(2);
						if (i2 == 0)
						{
							giveItems(player, RING_OF_BAIUM, 1);
						}
						else if (i2 == 1)
						{
							giveItems(player, RING_OF_QUEEN_ANT, 1);
						}
					}
					else if (i1 < 26)
					{
						int i2 = Rnd.get(24);
						if (i2 < 3)
						{
							giveItems(player, BOX_OF_LV5_SPECIAL_DYE_EVENT, 1);
						}
						else if (i2 < 6)
						{
							giveItems(player, BOX_OF_LV5_UNIQUE_DYE_EVENT, 1);
						}
						else if (i2 < 9)
						{
							giveItems(player, BOX_OF_LV5_RARE_DYE_EVENT, 1);
						}
						else if (i2 < 14)
						{
							giveItems(player, BOX_OF_LV4_SPECIAL_DYE_EVENT, 1);
						}
						else if (i2 < 19)
						{
							giveItems(player, BOX_OF_LV4_UNIQUE_DYE_EVENT, 1);
						}
						else if (i2 < 24)
						{
							giveItems(player, BOX_OF_LV4_RARE_DYE_EVENT, 1);
						}
					}
					else if (i1 < 2039)
					{
						int i2 = Rnd.get(2013);
						if (i2 < 2)
						{
							giveItems(player, BOX_OF_SOULSTONE_R99_EVENT_LUCKY, 1);
						}
						else if (i2 < 5)
						{
							giveItems(player, BOX_OF_SOULSTONE_R95_EVENT_LUCKY, 1);
						}
						else if (i2 < 13)
						{
							giveItems(player, BOX_OF_SOULSTONE_R_EVENT_LUCKY, 1);
						}
						else if (i2 < 263)
						{
							giveItems(player, BOX_OF_LV3_SPECIAL_DYE_EVENT, 1);
						}
						else if (i2 < 513)
						{
							giveItems(player, BOX_OF_LV3_UNIQUE_DYE_EVENT, 1);
						}
						else if (i2 < 888)
						{
							giveItems(player, BOX_OF_LV3_RARE_DYE_EVENT, 1);
						}
						else if (i2 < 1263)
						{
							giveItems(player, BOX_OF_LV2_SPECIAL_DYE_EVENT, 1);
						}
						else if (i2 < 1638)
						{
							giveItems(player, BOX_OF_LV2_UNIQUE_DYE_EVENT, 1);
						}
						else if (i2 < 2013)
						{
							giveItems(player, BOX_OF_LV2_RARE_DYE_EVENT, 1);
						}
					}
					else if (i1 < 7839)
					{
						int i2 = Rnd.get(58);
						if (i2 < 10)
						{
							giveItems(player, GEMSTONE_R, 1);
						}
						else if (i2 < 18)
						{
							giveItems(player, GEMSTONE_S, 3);
						}
						else if (i2 < 26)
						{
							giveItems(player, GEMSTONE_A, 4);
						}
						else if (i2 < 42)
						{
							giveItems(player, TOWN_RETURN_SCROLL_EVENT, 2);
						}
						else if (i2 < 58)
						{
							giveItems(player, QUICK_HEALING_POTION2, 5);
						}
					}
				}
				else
				{
					htmltext = "g_ev_collector005.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "g_ev_collector001.htm";
	}
	
	@GameScript
	public static void main()
	{
		new LetterCollector();
	}
}
