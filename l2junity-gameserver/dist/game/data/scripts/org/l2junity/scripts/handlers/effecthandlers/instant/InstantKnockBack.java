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

import org.l2junity.gameserver.ai.CtrlEvent;
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
 * Check if this effect is not counted as being stunned.
 * @author UnAfraid
 */
public final class InstantKnockBack extends AbstractEffect
{
	private final int _distance;
	private final int _speed;
	private final int _delay;
	private final int _animationSpeed;
	private final boolean _knockDown;
	private final FlyType _type;
	
	public InstantKnockBack(StatsSet params)
	{
		_distance = params.getInt("distance");
		_speed = params.getInt("speed", 0);
		_delay = params.getInt("delay", 0);
		_animationSpeed = params.getInt("animationSpeed", 0);
		_knockDown = params.getBoolean("knockDown", false);
		_type = params.getEnum("type", FlyType.class, _knockDown ? FlyType.PUSH_DOWN_HORIZONTAL : FlyType.PUSH_HORIZONTAL);
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
		
		if (!_knockDown)
		{
			knockBack(caster, targetCreature);
		}
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.stopActions();
		if (_knockDown)
		{
			knockBack(caster, target);
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (!target.isPlayer())
		{
			target.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	public void knockBack(Creature effector, Creature effected)
	{
		final double radians = Math.toRadians(effector.calculateAngleTo(effected));
		final double x = effected.getX() + (_distance * Math.cos(radians));
		final double y = effected.getY() + (_distance * Math.sin(radians));
		final double z = effected.getZ();
		final Location loc = GeoData.getInstance().moveCheck(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceWorld());
		effected.broadcastPacket(new FlyToLocation(effected, loc, _type, _speed, _delay, _animationSpeed));
		if (_knockDown)
		{
			effected.setHeadingTo(effector);
		}
		effected.setXYZ(loc);
		effected.broadcastPacket(new ValidateLocation(effected));
		effected.revalidateZone(true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_knockback", InstantKnockBack::new);
	}
}
