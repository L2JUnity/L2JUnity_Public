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
package org.l2junity.scripts.ai.individual.Other.NpcBuffers;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.model.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * @author UnAfraid
 */
public class NpcBuffersData implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(NpcBuffersData.class);
	
	private final Map<Integer, NpcBufferData> _npcBuffers = new HashMap<>();
	
	protected NpcBuffersData()
	{
		load();
	}
	
	private void load()
	{
		try
		{
			parseDatapackFile("data/scripts/org/l2junity/scripts/ai/individual/Other/NpcBuffers/NpcBuffersData.xml");
			LOGGER.info("Loaded: {} buffers data.", _npcBuffers.size());
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load buffers data: ", e);
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "npc", npcNode ->
		{
			final int npcId = parseInteger(npcNode.getAttributes(), "id");
			final NpcBufferData npc = new NpcBufferData(npcId);
			forEach(npcNode, "skill", skillNode -> npc.addSkill(new NpcBufferSkillData(new StatsSet(parseAttributes(skillNode)))));
			_npcBuffers.put(npcId, npc);
		}));
	}
	
	public NpcBufferData getNpcBuffer(int npcId)
	{
		return _npcBuffers.get(npcId);
	}
	
	public Collection<NpcBufferData> getNpcBuffers()
	{
		return _npcBuffers.values();
	}
	
	public Set<Integer> getNpcBufferIds()
	{
		return _npcBuffers.keySet();
	}
	
	private static final class SingletonHolder
	{
		protected static final NpcBuffersData INSTANCE = new NpcBuffersData();
	}
	
	public static NpcBuffersData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
