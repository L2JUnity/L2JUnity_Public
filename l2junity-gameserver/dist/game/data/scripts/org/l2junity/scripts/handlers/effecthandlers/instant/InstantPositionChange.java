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
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.Formulas;
import org.l2junity.gameserver.network.client.send.FlyToLocation;
import org.l2junity.gameserver.network.client.send.FlyToLocation.FlyType;
import org.l2junity.gameserver.network.client.send.ValidateLocation;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * This Blink effect switches the location of the caster and the target.<br>
 * This effect is totally done based on client description. <br>
 * Assume that geodata checks are done on the skill cast and not needed to repeat here.
 * @author Nik
 */
public final class InstantPositionChange extends AbstractEffect
{
	public InstantPositionChange(StatsSet params)
	{
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && Formulas.calcProbability(Double.NaN, caster, target.asCreature(), skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		final Location casterLocation = caster.getLocation();
		final Location targetLocation = targetCreature.getLocation();
		caster.broadcastPacket(new FlyToLocation(caster, targetLocation, FlyType.DUMMY));
		caster.stopActions();
		caster.setXYZ(targetLocation);
		caster.broadcastPacket(new ValidateLocation(caster));
		
		targetCreature.broadcastPacket(new FlyToLocation(targetCreature, casterLocation, FlyType.DUMMY));
		targetCreature.stopActions();
		targetCreature.setXYZ(casterLocation);
		targetCreature.broadcastPacket(new ValidateLocation(targetCreature));
		targetCreature.revalidateZone(true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_position_change", InstantPositionChange::new);
	}
}
