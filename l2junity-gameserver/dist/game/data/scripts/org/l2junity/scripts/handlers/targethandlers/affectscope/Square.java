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
 * Square affect scope implementation (actually more like a rectangle).
 * @author Nik
 */
public class Square implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final int squareStartAngle = skill.getFanRange()[1];
		final int squareLength = skill.getFanRange()[2];
		final int squareWidth = skill.getFanRange()[3];
		final int radius = (int) Math.sqrt((squareLength * squareLength) + (squareWidth * squareWidth));
		final int affectLimit = skill.getAffectLimit();
		
		final double rectX = activeChar.getX();
		final double rectY = activeChar.getY() - (squareWidth / 2);
		final double heading = Math.toRadians(squareStartAngle + Util.convertHeadingToDegree(activeChar.getHeading()));
		final double cos = Math.cos(-heading);
		final double sin = Math.sin(-heading);
		
		// Target checks.
		final MutableInt affected = new MutableInt(0);
		
		// Always accept main target.
		action.accept(target);
		
		// Check and add targets.
		World.getInstance().forEachVisibleObjectInRadius(activeChar, Creature.class, radius * 2, c ->
		{
			if (c == target)
			{
				return;
			}
			
			if ((affectLimit > 0) && (affected.intValue() >= affectLimit))
			{
				return;
			}
			if (c.isDead())
			{
				return;
			}
			
			final boolean checkIsInSquare = (c != target);
			
			// Check if inside square.
			double xp = c.getX() - activeChar.getX();
			double yp = c.getY() - activeChar.getY();
			double xr = (activeChar.getX() + (xp * cos)) - (yp * sin);
			double yr = activeChar.getY() + (xp * sin) + (yp * cos);
			if (!checkIsInSquare || ((xr > rectX) && (xr < (rectX + squareLength)) && (yr > rectY) && (yr < (rectY + squareWidth))))
			{
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
			}
		});
	}
	
	@Override
	public void drawEffected(Creature activeChar, WorldObject target, Skill skill)
	{
		final ExServerPrimitive packet = new ExServerPrimitive(getClass().getSimpleName() + "-" + activeChar.getObjectId(), activeChar);
		
		final int squareStartAngle = skill.getFanRange()[1]; // 0
		final int squareLength = skill.getFanRange()[2]; // 900
		// final int squareWidth = skill.getFanRange()[3]; // 50
		final int squareHalfWidth = skill.getFanRange()[3] / 2; // 50
		
		final double x = activeChar.getX();
		final double y = activeChar.getY();
		final double heading = Math.toRadians(squareStartAngle + Util.convertHeadingToDegree(activeChar.getHeading()));
		final double cos = Math.cos(heading);
		final double sin = Math.sin(heading);
		final double cos90 = Math.cos(heading + (Math.PI / 2));
		final double sin90 = Math.sin(heading + (Math.PI / 2));
		
		int x1 = (int) (x + (squareHalfWidth * cos90));
		int x2 = (int) (x - (squareHalfWidth * cos90));
		int x3 = (int) (x1 + (squareLength * cos));
		int x4 = (int) (x2 + (squareLength * cos));
		
		int y1 = (int) (y + (squareHalfWidth * sin90));
		int y2 = (int) (y - (squareHalfWidth * sin90));
		int y3 = (int) (y1 + (squareLength * sin));
		int y4 = (int) (y2 + (squareLength * sin));
		// final ILocational point1 = new Location(x, y + (squareWidth / 2), activeChar.getZ());
		// final ILocational point2 = new Location(x, y - (squareWidth / 2), activeChar.getZ());
		// final ILocational point3 = new Location(x + squareLength, y + (squareWidth / 2), activeChar.getZ());
		// final ILocational point4 = new Location(x + squareLength, y - (squareWidth / 2), activeChar.getZ());
		final ILocational point1 = new Location(x1, y1, activeChar.getZ());
		final ILocational point2 = new Location(x2, y2, activeChar.getZ());
		final ILocational point3 = new Location(x3, y3, activeChar.getZ());
		final ILocational point4 = new Location(x4, y4, activeChar.getZ());
		
		packet.addLine("X -> 1", Color.BLUE, true, activeChar, point1);
		packet.addLine("X -> 2", Color.BLUE, true, activeChar, point2);
		packet.addLine("1 -> 3", Color.BLUE, true, point1, point3);
		packet.addLine("2 -> 4", Color.BLUE, true, point2, point4);
		packet.addLine("3 -> 4", Color.BLUE, true, point3, point4);
		
		activeChar.sendDebugPacket(packet);
	}
	
	@SkillScript
	public static void main()
	{
		TargetHandler.getInstance().registerAffectScopeHandler("SQUARE", new Square());
	}
}