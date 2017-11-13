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
package org.l2junity.scripts.handlers.effecthandlers;

import org.l2junity.gameserver.geodata.GeoData;
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
 * Throw Up effect implementation.
 */
public final class FlyAway extends AbstractEffect
{
	private final int _radius;
	
	public FlyAway(StatsSet params)
	{
		_radius = params.getInt("radius");
	}
	
	@Override
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return target.isCreature() && Formulas.calcProbability(Double.NaN, caster, (Creature) target, skill);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		final double dx = caster.getX() - targetCreature.getX();
		final double dy = caster.getY() - targetCreature.getY();
		final double distance = Math.sqrt((dx * dx) + (dy * dy));
		final double nRadius = caster.getCollisionRadius() + targetCreature.getCollisionRadius() + _radius;
		final double x = caster.getX() - (nRadius * (dx / distance));
		final double y = caster.getY() - (nRadius * (dy / distance));
		final double z = caster.getZ();
		final Location destination = GeoData.getInstance().moveCheck(targetCreature.getX(), targetCreature.getY(), targetCreature.getZ(), x, y, z, targetCreature.getInstanceWorld());
		targetCreature.broadcastPacket(new FlyToLocation(targetCreature, destination, FlyType.THROW_UP));
		targetCreature.setXYZ(destination);
		targetCreature.broadcastPacket(new ValidateLocation(targetCreature));
		targetCreature.revalidateZone(true);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("FlyAway", FlyAway::new);
	}
}
