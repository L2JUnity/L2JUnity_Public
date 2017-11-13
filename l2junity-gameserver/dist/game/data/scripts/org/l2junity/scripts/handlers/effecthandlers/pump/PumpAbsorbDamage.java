/*
 * Copyright (C) 2004-2015 L2J Unity
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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2junity.gameserver.model.events.listeners.FunctionEventListener;
import org.l2junity.gameserver.model.events.returns.DamageReturn;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class PumpAbsorbDamage extends AbstractEffect
{
	private final double _damage;
	private static final Map<Integer, Double> _damageHolder = new ConcurrentHashMap<>();
	
	public PumpAbsorbDamage(StatsSet params)
	{
		_damage = params.getDouble("damage");
	}
	
	private DamageReturn onDamageReceivedEvent(OnCreatureDamageReceived event)
	{
		// DOT effects are not taken into account.
		if (event.isDamageOverTime())
		{
			return null;
		}
		
		final int objectId = event.getTarget().getObjectId();
		
		double damageLeft = _damageHolder.get(objectId);
		double newDamageLeft = Math.max(damageLeft - event.getDamage(), 0);
		double newDamage = Math.max(event.getDamage() - damageLeft, 0);
		
		_damageHolder.put(objectId, newDamageLeft);
		
		return new DamageReturn(false, true, false, newDamage);
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
		_damageHolder.remove(target.getObjectId());
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		_damageHolder.put(target.getObjectId(), _damage);
		target.addListener(new FunctionEventListener(target, EventType.ON_CREATURE_DAMAGE_RECEIVED, (OnCreatureDamageReceived event) -> onDamageReceivedEvent(event), this));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_absorb_damage", PumpAbsorbDamage::new);
	}
}
