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
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * An effect that gives item once a specific amount of EXP is obtained through hunting.<br>
 * On retail, accumulated exp counter resets after logout, and all exp bonuses are applied.
 * @author Nik
 */
public class PumpGetItemByExp extends AbstractEffect
{
	private final long _exp;
	private final int _itemId;
	private final long _itemCount;
	private static final Map<Integer, Long> _expHolder = new ConcurrentHashMap<>(); // On retail counter resets when player logs out.
	
	public PumpGetItemByExp(StatsSet params)
	{
		_exp = params.getLong("exp");
		_itemId = params.getInt("itemId");
		_itemCount = params.getLong("itemCount");
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return target.isPlayer();
	}
	
	public void onExperienceReceived(OnPlayableExpChanged event)
	{
		// TODO: Should only be through hunting.
		if (event.getActiveChar().isPlayer())
		{
			final PlayerInstance player = event.getActiveChar().getActingPlayer();
			long expDiff = event.getNewExp() - event.getOldExp();
			if (expDiff > 0)
			{
				long accumulatedExp = _expHolder.get(player.getObjectId()) + expDiff;
				if ((accumulatedExp >= _exp) && player.getInventory().validateCapacityByItemId(_itemId))
				{
					// Reward a single instance of the item.
					player.addItem(getClass().getName(), _itemId, _itemCount, player, true);
					
					// Reset accumulated exp.
					_expHolder.put(player.getObjectId(), 0L);
				}
				else
				{
					// Accumulate the exp so we reward in the future.
					_expHolder.put(player.getObjectId(), accumulatedExp);
				}
			}
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		_expHolder.remove(target.getObjectId());
		target.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		_expHolder.put(target.getObjectId(), 0L);
		target.addListener(new ConsumerEventListener(target, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event), this));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_get_item_by_exp", PumpGetItemByExp::new);
	}
}
