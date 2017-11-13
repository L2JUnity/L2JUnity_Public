/*
 * Copyright (C) 2004-2015 L2J Unity
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
package org.l2junity.scripts.ai.individual.Other.NpcBuffers;

import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class NpcBuffers extends AbstractNpcAI
{
	private NpcBuffers()
	{
		
		for (int npcId : NpcBuffersData.getInstance().getNpcBufferIds())
		{
			// TODO: Cleanup once npc rework is finished and default html is configurable.
			addFirstTalkId(npcId);
			addSpawnId(npcId);
		}
	}
	
	// TODO: Cleanup once npc rework is finished and default html is configurable.
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final NpcBufferData data = NpcBuffersData.getInstance().getNpcBuffer(npc.getId());
		for (NpcBufferSkillData skill : data.getSkills())
		{
			ThreadPool.schedule(new NpcBufferAI(npc, skill), skill.getInitialDelay(), TimeUnit.MILLISECONDS);
		}
		return super.onSpawn(npc);
	}
	
	@GameScript
	public static void main()
	{
		new NpcBuffers();
	}
}
