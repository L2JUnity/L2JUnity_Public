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
package org.l2junity.scripts.events.HuntForSanta;

import java.util.EnumSet;
import java.util.Set;

import org.l2junity.gameserver.data.xml.impl.MultisellData;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.quest.LongTimeEvent;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Hunt For Santa Event AI
 * @author ChaosPaladin
 */
public class HuntForSanta extends LongTimeEvent
{
	// Npc
	private static final int NOEL = 34008;
	// Skills
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(16419, 1),
		new SkillHolder(16420, 1),
		new SkillHolder(16421, 1),
	};
	// Misc
	private static final int MIN_PARTY = 7;
	private static final int MIN_RACES = 3;
	private static final Set<AbnormalType> ABNORMALS = EnumSet.of(AbnormalType.EVENT_BUF1, AbnormalType.EVENT_BUF2, AbnormalType.EVENT_BUF3);
	
	private HuntForSanta()
	{
		addStartNpc(NOEL);
		addTalkId(NOEL);
		addFirstTalkId(NOEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "ev_noel001.htm":
			case "ev_noel006.htm":
			{
				htmltext = event;
				break;
			}
			case "rewards":
			{
				MultisellData.getInstance().separateAndSend(2082, player, npc, false);
				break;
			}
			case "buff1":
			{
				player.getEffectList().stopEffects(ABNORMALS);
				npc.setTarget(player);
				npc.doCast(BUFFS[0].getSkill());
				htmltext = "ev_noel002.htm";
			}
			case "buff2":
			{
				player.getEffectList().stopEffects(ABNORMALS);
				npc.setTarget(player);
				npc.doCast(BUFFS[1].getSkill());
				htmltext = "ev_noel002.htm";
			}
			case "buff3":
			{
				player.getEffectList().stopEffects(ABNORMALS);
				npc.setTarget(player);
				npc.doCast(BUFFS[2].getSkill());
				htmltext = "ev_noel002.htm";
			}
			case "givePartyBuff":
			{
				htmltext = applyPartyBuff(npc, player);
				break;
			}
			case "giveAnotherBuff":
			{
				player.getEffectList().stopEffects(ABNORMALS);
				htmltext = "ev_noel006.htm";
				break;
			}
		}
		return htmltext;
	}
	
	private String applyPartyBuff(Npc npc, PlayerInstance player)
	{
		final Party playerParty = player.getParty();
		
		if (playerParty == null)
		{
			return "ev_noel003.htm";
		}
		else if ((playerParty.getLeader() == player) && ((playerParty.getMemberCount() >= MIN_PARTY) || (playerParty.getMembers().stream().map(PlayerInstance::getRace).distinct().count() >= MIN_RACES)))
		{
			for (PlayerInstance member : playerParty.getMembers())
			{
				if (npc.isInRadius2d(member, 300))
				{
					player.getEffectList().stopEffects(ABNORMALS);
					for (SkillHolder holder : BUFFS)
					{
						npc.doInstantCast(player, holder);
					}
				}
			}
			return "ev_noel002.htm";
		}
		return "ev_noel004.htm";
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "ev_noel001.htm";
	}
	
	@GameScript
	public static void main()
	{
		new HuntForSanta();
	}
}
