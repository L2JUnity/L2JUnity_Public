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
package org.l2junity.scripts.handlers.effecthandlers.tick;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.network.client.send.EtcStatusUpdate;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class TickGetEnergy extends AbstractEffect
{
	public TickGetEnergy(StatsSet params)
	{
	}
	
	@Override
	public void tick(Creature caster, Creature target, Skill skill)
	{
		if (target.isPlayer())
		{
			final PlayerInstance player = target.getActingPlayer();
			final int maxCharge = (int) target.getStat().getValue(DoubleStat.MAX_MOMENTUM, 0);
			final int newCharge = Math.min(player.getCharges() + 1, maxCharge);
			
			player.setCharges(maxCharge);
			
			if (newCharge == maxCharge)
			{
				player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			}
			else
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1);
				sm.addInt(newCharge);
				player.sendPacket(sm);
			}
			
			player.sendPacket(new EtcStatusUpdate(player));
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("t_get_energy", TickGetEnergy::new);
	}
}
