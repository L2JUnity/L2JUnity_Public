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
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerDeathExpPenalty;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerDeathPenalty;
import org.l2junity.gameserver.model.events.impl.character.player.OnPlayerVitalityConsume;
import org.l2junity.gameserver.model.events.listeners.FunctionEventListener;
import org.l2junity.gameserver.model.events.returns.IntegerReturn;
import org.l2junity.gameserver.model.events.returns.LongReturn;
import org.l2junity.gameserver.model.events.returns.TerminateReturn;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Lucky effect implementation.
 * @author Zoey76
 */
public final class PumpLuck extends AbstractEffect
{
	public PumpLuck(StatsSet params)
	{
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return target.isPlayer();
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.removeListenerIf(EventType.ON_PLAYER_DEATH_PENALTY, listener -> listener.getOwner() == this);
		target.removeListenerIf(EventType.ON_PLAYER_DEATH_EXP_PENALTY, listener -> listener.getOwner() == this);
		target.removeListenerIf(EventType.ON_PLAYER_VITALITY_CONSUME, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.addListener(new FunctionEventListener(target, EventType.ON_PLAYER_DEATH_PENALTY, (OnPlayerDeathPenalty event) -> target.getLevel() <= 9 ? new TerminateReturn(true, false, false) : null, this));
		target.addListener(new FunctionEventListener(target, EventType.ON_PLAYER_DEATH_EXP_PENALTY, (OnPlayerDeathExpPenalty event) -> target.getLevel() <= 9 ? new LongReturn(true, false, false, 0) : null, this));
		target.addListener(new FunctionEventListener(target, EventType.ON_PLAYER_VITALITY_CONSUME, (OnPlayerVitalityConsume event) -> target.getLevel() <= 9 ? new IntegerReturn(true, false, false, 0) : null, this));
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_luck", PumpLuck::new);
	}
}
