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
package org.l2junity.gameserver.enums;

import java.awt.Color;

import org.l2junity.commons.lang.mutable.MutableInt;
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.interfaces.ILocational;
import org.l2junity.gameserver.network.client.send.ExServerPrimitive;
import org.l2junity.gameserver.util.Util;

/**
 * @author Sdw
 */
public enum Position
{
	FRONT,
	SIDE,
	BACK;
	
	/**
	 * Position calculation based on the retail-like formulas:<br>
	 * <ul>
	 * <li>heading: (unsigned short) abs(heading - (unsigned short)(int)floor(atan2(toY - fromY, toX - fromX) * 65535.0 / 6.283185307179586))</li>
	 * <li>side: if (heading >= 0x2000 && heading <= 0x6000 || (unsigned int)(heading - 0xA000) <= 0x4000)</li>
	 * <li>front: else if ((unsigned int)(heading - 0x2000) <= 0xC000)</li>
	 * <li>back: otherwise.</li>
	 * </ul>
	 * @param from
	 * @param to
	 * @return
	 */
	public static Position getPosition(ILocational from, ILocational to)
	{
		final int heading = Math.abs(to.getHeading() - from.calculateHeadingTo(to));
		if (((heading >= 0x2000) && (heading <= 0x6000)) || (Integer.toUnsignedLong(heading - 0xA000) <= 0x4000))
		{
			return SIDE;
		}
		else if (Integer.toUnsignedLong(heading - 0x2000) <= 0xC000)
		{
			return FRONT;
		}
		else
		{
			return BACK;
		}
	}
	
	public static void drawPosition(Creature activeChar)
	{
		final double x = activeChar.getX();
		final double y = activeChar.getY();
		final double z = activeChar.getZ();
		final int heading = activeChar.getHeading();
		final int[] headingSides =
		{
			(heading + 0x2000) % 65535,
			(heading + 0x6000) % 65535,
			(heading + 0xA000) % 65535,
			(heading + 0xE000) % 65535
		};
		
		final ExServerPrimitive packet = new ExServerPrimitive(Position.class.getSimpleName() + "-" + activeChar.getObjectId(), x, y, World.MAP_MIN_Z);
		
		final double radius = 100;
		ILocational[] locs = new ILocational[headingSides.length];
		for (int i = 0; i < locs.length; i++)
		{
			final double tx = x + (radius * Math.cos(Util.convertHeadingToRadian(headingSides[i])));
			final double ty = y + (radius * Math.sin(Util.convertHeadingToRadian(headingSides[i])));
			final double tz = GeoData.getInstance().getHeight(tx, ty, z);
			locs[i] = new Location(tx, ty, tz);
		}
		
		packet.addLine("FRONT (0x2000)", Color.GREEN, true, activeChar, locs[0]);
		packet.addLine("BACK (0x6000)", Color.RED, true, x, y, z, locs[1]);
		packet.addLine("BACK (0xA000)", Color.RED, true, x, y, z, locs[2]);
		packet.addLine("FRONT (0xE000)", Color.GREEN, true, x, y, z, locs[3]);
		
		final int maxPoints = 36;
		locs = new ILocational[maxPoints];
		final double anglePoint = (Math.PI * 2) / maxPoints;
		for (int i = 0; i < locs.length; i++)
		{
			final double tx = x + (radius * Math.cos(anglePoint * i));
			final double ty = y + (radius * Math.sin(anglePoint * i));
			final double tz = GeoData.getInstance().getHeight(tx, ty, z);
			locs[i] = new Location(tx, ty, tz);
		}
		
		for (ILocational loc : locs)
		{
			switch (getPosition(loc, activeChar))
			{
				case FRONT:
					packet.addPoint(Color.GREEN, loc);
					break;
				case SIDE:
					packet.addPoint(Color.YELLOW, loc);
					break;
				case BACK:
					packet.addPoint(Color.red, loc);
					break;
			}
		}
		
		final MutableInt pointsLeft = new MutableInt(maxPoints);
		World.getInstance().forEachVisibleObjectInRadius(activeChar, WorldObject.class, (int) radius * 2, loc ->
		{
			// Prevent packet overflow.
			if (pointsLeft.decrementAndGet() < 0)
			{
				return;
			}
			
			final Position pos = getPosition(loc, activeChar);
			switch (pos)
			{
				case FRONT:
					packet.addPoint(pos.toString(), Color.GREEN, true, loc);
					break;
				case SIDE:
					packet.addPoint(pos.toString(), Color.YELLOW, true, loc);
					break;
				case BACK:
					packet.addPoint(pos.toString(), Color.red, true, loc);
					break;
			}
		});
		
		activeChar.sendDebugPacket(packet, DebugType.POSITION);
	}
}
