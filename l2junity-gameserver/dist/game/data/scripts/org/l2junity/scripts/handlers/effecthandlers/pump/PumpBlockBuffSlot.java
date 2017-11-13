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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.AbnormalType;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Block Buff Slot effect implementation.
 * @author Zoey76
 */
public final class PumpBlockBuffSlot extends AbstractEffect
{
	private final Set<AbnormalType> _blockAbnormalSlots;
	
	public PumpBlockBuffSlot(StatsSet params)
	{
		_blockAbnormalSlots = Arrays.stream(params.getString("slot").split(";")).map(slot -> Enum.valueOf(AbnormalType.class, slot)).collect(Collectors.toSet());
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.getEffectList().addBlockedAbnormalTypes(_blockAbnormalSlots);
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		target.getEffectList().removeBlockedAbnormalTypes(_blockAbnormalSlots);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("p_block_buff_slot", PumpBlockBuffSlot::new);
	}
}
