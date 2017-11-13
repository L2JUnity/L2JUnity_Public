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

import org.l2junity.gameserver.ai.CtrlIntention;
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
 * Blink effect implementation.<br>
 * This class handles warp effects, disappear and quickly turn up in a near location.<br>
 * If geodata enabled and an object is between initial and final point, flight is stopped just before colliding with object.<br>
 * Flight course and radius are set as skill properties (flyCourse and flyRadius):
 * <ul>
 * <li>Fly Radius means the distance between starting point and final point, it must be an integer.</li>
 * <li>Fly Course means the movement direction: imagine a compass above player's head, making north player's heading. So if fly course is 180, player will go backwards (good for blink, e.g.).</li>
 * </ul>
 * By the way, if flyCourse = 360 or 0, player will be moved in in front of him. <br>
 * If target is effector, put in XML self="1", this will make _actor = getEffector(). This, combined with target type, allows more complex actions like flying target's backwards or player's backwards.
 * @author DrHouse
 */
public final class InstantBlink extends AbstractEffect
{
	private final int _flyCourse;
	private final int _flyRadius;
	
	private final FlyType _flyType;
	private final int _flySpeed;
	private final int _flyDelay;
	private final int _animationSpeed;
	
	public InstantBlink(StatsSet params)
	{
		_flyCourse = params.getInt("angle", 0);
		_flyRadius = params.getInt("range", 0);
		_flyType = params.getEnum("flyType", FlyType.class, FlyType.DUMMY);
		_flySpeed = params.getInt("speed", 0);
		_flyDelay = params.getInt("delay", 0);
		_animationSpeed = params.getInt("animationSpeed", 0);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final Creature targetCreature = target.asCreature();
		if (targetCreature == null)
		{
			return;
		}
		
		if (targetCreature.cannotEscape())
		{
			return;
		}
		
		final double angle = Util.convertHeadingToDegree(targetCreature.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(_flyCourse);
		final double x1 = Math.cos(Math.PI + radian + course) * _flyRadius;
		final double y1 = Math.sin(Math.PI + radian + course) * _flyRadius;
		
		double x = targetCreature.getX() + x1;
		double y = targetCreature.getY() + y1;
		double z = targetCreature.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(targetCreature.getX(), targetCreature.getY(), targetCreature.getZ(), x, y, z, targetCreature.getInstanceWorld());
		
		targetCreature.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		targetCreature.broadcastPacket(new FlyToLocation(targetCreature, destination, _flyType, _flySpeed, _flyDelay, _animationSpeed));
		targetCreature.setXYZ(destination);
		targetCreature.broadcastPacket(new ValidateLocation(targetCreature));
		targetCreature.revalidateZone(true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_blink", InstantBlink::new);
	}
}
