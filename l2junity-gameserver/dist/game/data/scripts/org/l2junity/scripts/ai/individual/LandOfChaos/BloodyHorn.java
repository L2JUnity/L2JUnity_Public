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
package org.l2junity.scripts.ai.individual.LandOfChaos;

import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.network.client.send.ExShowScreenMessage;
import org.l2junity.gameserver.network.client.send.string.NpcStringId;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * @author ChaosPaladin
 */
public final class BloodyHorn extends AbstractNpcAI
{
	// NPC
	private static final int BLOODY_HORN = 19463;
	// Skills
	private static final SkillHolder[] HALF_RESTORE =
	{
		new SkillHolder(15537, 1), // Blood Sacrifice: Decrease Speed
		new SkillHolder(15538, 1), // Blood Sacrifice: Decrease Accuracy
		new SkillHolder(15539, 1), // Blood Sacrifice: Decrease P. Def.
		new SkillHolder(15540, 1), // Blood Sacrifice: Decrease M. Def.
	};
	private static final SkillHolder[] FULL_RESTORE =
	{
		new SkillHolder(15537, 2), // Blood Sacrifice: Decrease Speed
		new SkillHolder(15538, 2), // Blood Sacrifice: Decrease Accuracy
		new SkillHolder(15539, 2), // Blood Sacrifice: Decrease P. Def.
		new SkillHolder(15540, 2), // Blood Sacrifice: Decrease M. Def.
	};
	
	private BloodyHorn()
	{
		addFirstTalkId(BLOODY_HORN);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final int i = getRandom(10);
		if (i < 7)
		{
			npc.setTarget(player);
			npc.doInstantCast(player, HALF_RESTORE[getRandom(4)]);
			showOnScreenMsg(player, NpcStringId.HP_IS_HALFWAY_RESTORED, ExShowScreenMessage.TOP_CENTER, 5000, true);
		}
		else
		{
			npc.setTarget(player);
			npc.doInstantCast(player, FULL_RESTORE[getRandom(4)]);
			showOnScreenMsg(player, NpcStringId.HP_IS_FULLY_RESTORED, ExShowScreenMessage.TOP_CENTER, 5000, true);
		}
		npc.deleteMe();
		return null;
	}
	
	@GameScript
	public static void main()
	{
		new BloodyHorn();
	}
}
