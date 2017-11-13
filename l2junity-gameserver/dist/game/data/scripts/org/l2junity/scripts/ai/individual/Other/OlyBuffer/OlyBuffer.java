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
package org.l2junity.scripts.ai.individual.Other.OlyBuffer;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * Olympiad Buffer AI.
 * @author St3eT
 */
public final class OlyBuffer extends AbstractNpcAI
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	// Skills
	private static final SkillHolder KNIGHT = new SkillHolder(14744, 1); // Olympiad - Knight's Harmony
	private static final SkillHolder WARRIOR = new SkillHolder(14745, 1); // Olympiad - Warrior's Harmony
	private static final SkillHolder WIZARD = new SkillHolder(14746, 1); // Olympiad - Wizard's Harmony
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(14738, 1), // Olympiad - Horn Melody
		new SkillHolder(14739, 1), // Olympiad - Drum Melody
		new SkillHolder(14740, 1), // Olympiad - Pipe Organ Melody
		new SkillHolder(14741, 1), // Olympiad - Guitar Melody
		new SkillHolder(14742, 1), // Olympiad - Harp Melody
		new SkillHolder(14743, 1), // Olympiad - Lute Melody
	};
	
	private OlyBuffer()
	{
		addStartNpc(OLYMPIAD_BUFFER);
		addFirstTalkId(OLYMPIAD_BUFFER);
		addTalkId(OLYMPIAD_BUFFER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		if (npc.isScriptValue(0))
		{
			htmltext = "olympiad_master001.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "guardian":
			{
				htmltext = applyBuffs(npc, player, KNIGHT.getSkill());
				break;
			}
			case "berserker":
			{
				htmltext = applyBuffs(npc, player, WARRIOR.getSkill());
				break;
			}
			case "magician":
			{
				htmltext = applyBuffs(npc, player, WIZARD.getSkill());
				break;
			}
		}
		npc.setScriptValue(1);
		htmltext = "olympiad_master003.htm";
		getTimers().addTimer("DELETE_ME", 5000, evnt -> npc.deleteMe());
		
		return htmltext;
	}
	
	private String applyBuffs(Npc npc, PlayerInstance player, Skill skill)
	{
		for (SkillHolder holder : BUFFS)	
		{
			npc.doInstantCast(player, holder);
		}
		addSkillCastDesire(npc, player, skill, 23);
		return null;
	}
	
	@GameScript
	public static void main()
	{
		new OlyBuffer();
	}
}