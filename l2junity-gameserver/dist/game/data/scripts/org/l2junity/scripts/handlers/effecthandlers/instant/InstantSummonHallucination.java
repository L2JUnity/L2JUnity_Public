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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.data.xml.impl.NpcData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.DoppelgangerInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sdw
 */
public class InstantSummonHallucination extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InstantSummonHallucination.class);
	
	private final int _despawnDelay;
	private final int _npcId;
	private final int _npcCount;
	
	public InstantSummonHallucination(StatsSet params)
	{
		_despawnDelay = params.getInt("despawnDelay", 20000);
		_npcId = params.getInt("npcId");
		_npcCount = params.getInt("npcCount", 1);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.isAlikeDead())
		{
			return;
		}
		
		if ((_npcId <= 0) || (_npcCount <= 0))
		{
			LOGGER.warn("Invalid NPC ID or count skill ID: {}", skill.getId());
			return;
		}
		
		if (casterPlayer.isMounted())
		{
			return;
		}
		
		final L2NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(_npcId);
		if (npcTemplate == null)
		{
			LOGGER.warn("Spawn of the nonexisting NPC ID: {}, skill ID: {}", _npcId, skill.getId());
			return;
		}
		
		double x = targetCreature.getX();
		double y = targetCreature.getY();
		final double z = targetCreature.getZ();
		for (int i = 0; i < _npcCount; i++)
		{
			x += (Rnd.nextBoolean() ? Rnd.get(0, 20) : Rnd.get(-20, 0));
			y += (Rnd.nextBoolean() ? Rnd.get(0, 20) : Rnd.get(-20, 0));
			
			final DoppelgangerInstance clone = new DoppelgangerInstance(npcTemplate, casterPlayer);
			clone.setCurrentHp(clone.getMaxHp());
			clone.setCurrentMp(clone.getMaxMp());
			clone.setSummoner(casterPlayer);
			clone.spawnMe(x, y, z);
			clone.scheduleDespawn(_despawnDelay);
			clone.doAutoAttack(targetCreature);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_summon_hallucination", InstantSummonHallucination::new);
	}
}
