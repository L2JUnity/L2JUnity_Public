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
import java.util.Comparator;
import java.util.List;
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

/**
 * Range sorted by lowest to highest hp percent affect scope implementation.
 * @author Nik
 */
public class RangeSortByHp implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		// Target checks.
		final MutableInt affected = new MutableInt(0);
		List<Creature> result = World.getInstance().getVisibleObjects(target, Creature.class, affectRange, c ->
		{
			if ((affectLimit > 0) && (affected.intValue() >= affectLimit))
			{
				return false;
			}
			
			if (c.isDead())
			{
				return false;
			}
			
			if (!skill.getAffectObjectHandler().checkAffectedObject(activeChar, c))
			{
				return false;
			}
			
			affected.increment();
			return true;
		});
		
		// Always accept main target.
		result.add((Creature) target);
		
		// Sort from lowest hp to highest hp.
		//@formatter:off
		result.stream()
		.sorted(Comparator.comparingInt(Creature::getCurrentHpPercent))
		.limit(affectLimit > 0 ? affectLimit : Long.MAX_VALUE)
		.forEach(action);
		//@formatter:on
	}
	
	@Override
	public void drawEffected(Creature activeChar, WorldObject target, Skill skill)
	{
		final ExServerPrimitive packet = new ExServerPrimitive(getClass().getSimpleName() + "-" + activeChar.getObjectId(), activeChar);
		final int maxPoints = skill.getAffectRange() > 1000 ? 36 : skill.getAffectRange() > 100 ? 18 : 12;
		final ILocational[] locs = new ILocational[maxPoints];
		final double anglePoint = 360 / maxPoints;
		for (int i = 0; i < locs.length; i++)
		{
			double angle = (anglePoint * i);
			
			final double tx = target.getX() + (skill.getAffectRange() * Math.cos(Math.toRadians(angle)));
			final double ty = target.getY() + (skill.getAffectRange() * Math.sin(Math.toRadians(angle)));
			final double tz = GeoData.getInstance().getHeight(tx, ty, target.getZ());
			locs[i] = new Location(tx, ty, tz);
		}
		
		packet.addLine("X -> O (" + skill.getAffectRange() + " AOE)", Color.GREEN, true, activeChar, target);
		for (int i = 1; i < locs.length; i++)
		{
			packet.addLine((anglePoint * (i - 1)) + " -> " + (anglePoint * i), Color.GREEN, true, locs[i - 1], locs[i]);
		}
		packet.addLine((anglePoint * (locs.length - 1)) + " -> 360", Color.GREEN, true, locs[0], locs[locs.length - 1]);
		
		activeChar.sendDebugPacket(packet);
	}
	
	@SkillScript
	public static void main()
	{
		TargetHandler.getInstance().registerAffectScopeHandler("RANGE_SORT_BY_HP", new RangeSortByHp());
	}
}