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

import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.FlyToLocation;
import org.l2junity.gameserver.network.client.send.FlyToLocation.FlyType;
import org.l2junity.gameserver.network.client.send.ValidateLocation;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.gameserver.util.Util;

/**
 * Teleport To Target effect implementation.
 * @author Didldak, Adry_85
 */
public final class InstantTeleportToTarget extends AbstractEffect
{
	public InstantTeleportToTarget(StatsSet params)
	{
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		double px = target.getX();
		double py = target.getY();
		double ph = Util.convertHeadingToDegree(target.getHeading());
		
		ph += 180;
		if (ph > 360)
		{
			ph -= 360;
		}
		
		ph = (Math.PI * ph) / 180;
		double x = px + (25 * Math.cos(ph));
		double y = py + (25 * Math.sin(ph));
		double z = target.getZ();
		
		final Location loc = GeoData.getInstance().moveCheck(caster.getX(), caster.getY(), caster.getZ(), x, y, z, caster.getInstanceWorld());
		caster.broadcastPacket(new FlyToLocation(caster, loc.getX(), loc.getY(), loc.getZ(), FlyType.DUMMY));
		caster.stopActions();
		caster.setXYZ(loc);
		caster.broadcastPacket(new ValidateLocation(caster));
		caster.revalidateZone(true);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_teleport_to_target", InstantTeleportToTarget::new);
	}
}
