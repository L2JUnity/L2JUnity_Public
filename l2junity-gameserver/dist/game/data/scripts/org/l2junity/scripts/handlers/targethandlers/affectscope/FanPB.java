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
package org.l2junity.scripts.handlers.targethandlers.affectscope;

import java.awt.Color;
import java.util.function.Consumer;

import org.l2junity.commons.lang.mutable.MutableInt;
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.handler.IAffectScopeHandler;
import org.l2junity.gameserver.handler.TargetHandler;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.interfaces.ILocational;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.ExServerPrimitive;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.gameserver.util.Util;

/**
 * Fan point blank affect scope implementation. Gathers objects in a certain angle of circular area around yourself without taking target into account.
 * @author Nik
 */
public class FanPB implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final double headingAngle = Util.convertHeadingToDegree(activeChar.getHeading());
		final int fanStartAngle = skill.getFanRange()[1];
		final int fanRadius = skill.getFanRange()[2];
		final int fanAngle = skill.getFanRange()[3];
		final double fanHalfAngle = fanAngle / 2; // Half left and half right.
		final int affectLimit = skill.getAffectLimit();
		
		// Target checks.
		final MutableInt affected = new MutableInt(0);
		
		// Check and add targets.
		World.getInstance().forEachVisibleObjectInRadius(activeChar, Creature.class, fanRadius, c ->
		{
			if ((affectLimit > 0) && (affected.intValue() >= affectLimit))
			{
				return;
			}
			if (c.isDead())
			{
				return;
			}
			if (Math.abs(activeChar.calculateAngleTo(c) - (headingAngle + fanStartAngle)) > fanHalfAngle)
			{
				return;
			}
			if (!skill.getAffectObjectHandler().checkAffectedObject(activeChar, c))
			{
				return;
			}
			if (!GeoData.getInstance().canSeeTarget(activeChar, c))
			{
				return;
			}
			
			affected.increment();
			action.accept(c);
		});
	}
	
	@Override
	public void drawEffected(Creature activeChar, WorldObject target, Skill skill)
	{
		final ExServerPrimitive packet = new ExServerPrimitive(getClass().getSimpleName() + "-" + activeChar.getObjectId(), activeChar);
		
		final double headingAngle = Util.convertHeadingToDegree(activeChar.getHeading());
		final int fanRadius = skill.getFanRange()[2];
		final int fanAngle = skill.getFanRange()[3];
		final double halfAngle = fanAngle / 2;
		
		final double x = activeChar.getX();
		final double y = activeChar.getY();
		final double z = activeChar.getZ();
		
		final int maxPoints = 10;
		final ILocational[] locs = new ILocational[maxPoints];
		final double anglePoint = fanAngle / maxPoints;
		for (int i = 0; i < locs.length; i++)
		{
			double angle = -halfAngle + (anglePoint * i);
			
			final double tx = x + (fanRadius * Math.cos(Math.toRadians(headingAngle + angle)));
			final double ty = y + (fanRadius * Math.sin(Math.toRadians(headingAngle + angle)));
			final double tz = GeoData.getInstance().getHeight(tx, ty, z);
			locs[i] = new Location(tx, ty, tz);
		}
		
		packet.addLine("X -> 1", Color.GREEN, true, activeChar, locs[0]);
		for (int i = 1; i < locs.length; i++)
		{
			packet.addLine(i + " -> " + (i + 1), Color.GREEN, true, locs[i - 1], locs[i]);
		}
		packet.addLine(locs.length + " -> X", Color.GREEN, true, locs[locs.length - 1], activeChar);
		packet.addLine("1 -> " + locs.length, Color.GREEN, true, locs[0], locs[locs.length - 1]);
		
		activeChar.sendDebugPacket(packet);
	}
	
	@SkillScript
	public static void main()
	{
		TargetHandler.getInstance().registerAffectScopeHandler("FAN_PB", new FanPB());
	}
}