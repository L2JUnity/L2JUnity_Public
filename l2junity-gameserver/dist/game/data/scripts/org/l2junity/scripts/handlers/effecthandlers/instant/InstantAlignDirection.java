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
package org.l2junity.scripts.handlers.effecthandlers.instant;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.StartRotation;
import org.l2junity.gameserver.network.client.send.StopRotation;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Bluff effect implementation.
 * @author decad
 */
public final class InstantAlignDirection extends AbstractEffect
{
	private final int _chance;
	
	public InstantAlignDirection(StatsSet params)
	{
		_chance = params.getInt("chance");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && Formulas.calcProbability(_chance, caster, target.asCreature(), skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		// Headquarters NPC should not rotate
		if ((targetCreature.getId() == 35062) || targetCreature.isRaid() || targetCreature.isRaidMinion())
		{
			return;
		}
		
		targetCreature.broadcastPacket(new StartRotation(targetCreature.getObjectId(), targetCreature.getHeading(), 1, 65535));
		targetCreature.broadcastPacket(new StopRotation(targetCreature.getObjectId(), caster.getHeading(), 65535));
		targetCreature.setHeading(caster.getHeading());
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_align_direction", InstantAlignDirection::new);
	}
}
