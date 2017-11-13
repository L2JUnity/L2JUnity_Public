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
package org.l2junity.scripts.ai.spawns;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2junity.gameserver.config.TrainingCampConfig;
import org.l2junity.gameserver.model.spawns.SpawnTemplate;
import org.l2junity.gameserver.scripting.annotations.GameScript;

import org.l2junity.scripts.ai.AbstractNpcAI;

/**
 * @author Sdw
 */
public class TrainingCamp extends AbstractNpcAI
{
	private final Set<SpawnTemplate> _templates = ConcurrentHashMap.newKeySet();
	
	private TrainingCamp()
	{
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (_templates.add(template))
		{
			template.getGroups().forEach(group ->
			{
				if (!TrainingCampConfig.ENABLE)
				{
					group.despawnAll();
				}
			});
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		_templates.remove(template);
	}
	
	@GameScript
	public static void main()
	{
		new TrainingCamp();
	}
}
