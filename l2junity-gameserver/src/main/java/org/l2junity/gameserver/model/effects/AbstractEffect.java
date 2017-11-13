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
package org.l2junity.gameserver.model.effects;

import org.l2junity.gameserver.config.PlayerConfig;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;

/**
 * Abstract effect implementation.<br>
 * Instant effects should not override {@link #pumpEnd(Creature, Creature, Skill)}.<br>
 * Do not call super class methods {@link #pumpStart(Creature, Creature, Skill)} nor {@link #pumpEnd(Creature, Creature, Skill)}.
 * @author Zoey76
 */
public abstract class AbstractEffect
{
	private int _ticks;
	
	/**
	 * Gets the effect ticks
	 * @return the ticks
	 */
	public int getTicks()
	{
		return _ticks;
	}
	
	/**
	 * Sets the effect ticks
	 * @param ticks the ticks
	 */
	protected void setTicks(int ticks)
	{
		_ticks = ticks;
	}
	
	public double getTicksMultiplier()
	{
		return (getTicks() * PlayerConfig.EFFECT_TICK_RATIO) / 1000f;
	}
	
	/**
	 * Calculates whether this effects land or not.<br>
	 * If it lands will be scheduled and added to the character effect list.<br>
	 * Override in effect implementation to change behavior. <br>
	 * <b>Warning:</b> Must be used only for instant effects continuous effects will not call this they have their success handled by activate_rate.
	 * @param caster
	 * @param target
	 * @param skill
	 * @return {@code true} if this effect land, {@code false} otherwise
	 */
	public boolean calcSuccess(Creature caster, WorldObject target, Skill skill)
	{
		return true;
	}
	
	/**
	 * Called upon cast.<br>
	 * @param caster
	 * @param target
	 * @param skill
	 * @param item
	 */
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		
	}
	
	/**
	 * Called when effect start.<br>
	 * @param caster
	 * @param target
	 * @param skill
	 */
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		
	}
	
	/**
	 * Called when effect exit.<br>
	 * @param caster
	 * @param target
	 * @param skill
	 */
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		
	}
	
	/**
	 * Called on each tick.<br>
	 * If the abnormal time is lesser than zero it will last forever.
	 * @param caster
	 * @param target
	 * @param skill
	 */
	public void tick(Creature caster, Creature target, Skill skill)
	{
		
	}
	
	/**
	 * @param caster
	 * @param target
	 * @param skill
	 * @return {@code true} if pump can be invoked, {@code false} otherwise
	 */
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return true;
	}
	
	/**
	 * @param target
	 * @param skill
	 */
	public void pump(Creature target, Skill skill)
	{
		
	}
	
	/**
	 * @param target
	 * @param skill
	 * @return {@code false} if skill must be cancelled, {@code true} otherwise
	 */
	public boolean consume(Creature target, Skill skill)
	{
		return true;
	}
	
	/**
	 * Get this effect's type.<br>
	 * TODO: Remove.
	 * @return the effect type
	 */
	public L2EffectType getEffectType()
	{
		return L2EffectType.NONE;
	}
	
	@Override
	public String toString()
	{
		return "Effect " + getClass().getSimpleName();
	}
}