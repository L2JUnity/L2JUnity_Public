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
package org.l2junity.scripts.handlers.effecthandlers.pump;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.Playable;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2junity.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.network.client.send.ExSpawnEmitter;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Soul Eating effect implementation.
 * @author UnAfraid
 */
public final class PumpSoulEating extends AbstractEffect
{
	private final int _expNeeded;
	private final int _maxSouls;
	
	public PumpSoulEating(StatsSet params)
	{
		_expNeeded = params.getInt("expNeeded");
		_maxSouls = params.getInt("maxSouls");
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			target.addListener(new ConsumerEventListener(target, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getActiveChar(), (event.getNewExp() - event.getOldExp())), this));
		}
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			target.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
		}
	}
	
	@Override
	public void pump(Creature target, Skill skill)
	{
		target.getStat().mergeAdd(DoubleStat.MAX_SOULS, _maxSouls);
	}
	
	public void onExperienceReceived(Playable playable, long exp)
	{
		// TODO: Verify logic.
		if (playable.isPlayer() && (exp >= _expNeeded))
		{
			final PlayerInstance player = playable.getActingPlayer();
			final int maxSouls = (int) player.getStat().getValue(DoubleStat.MAX_SOULS, 0);
			if (player.getChargedSouls() >= maxSouls)
			{
				playable.sendPacket(SystemMessageId.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
				return;
			}
			
			player.increaseSouls(1);
			
			if ((player.getTarget() != null) && player.getTarget().isNpc())
			{
				final Npc npc = (Npc) playable.getTarget();
				player.broadcastPacket(new ExSpawnEmitter(player, npc), 500);
			}
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_soul_eating", PumpSoulEating::new);
	}
}
