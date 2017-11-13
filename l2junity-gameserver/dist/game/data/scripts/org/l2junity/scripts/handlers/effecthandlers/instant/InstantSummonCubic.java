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

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.data.xml.impl.CubicData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.templates.L2CubicTemplate;
import org.l2junity.gameserver.model.cubic.CubicInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.network.client.send.ExUserInfoCubic;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Summon Cubic effect implementation.
 * @author Zoey76
 */
public final class InstantSummonCubic extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InstantSummonCubic.class);
	
	private final int _cubicId;
	private final int _cubicLvl;
	
	public InstantSummonCubic(StatsSet params)
	{
		_cubicId = params.getInt("cubicId");
		_cubicLvl = params.getInt("cubicLvl");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		if (targetPlayer.isAlikeDead() || targetPlayer.inObserverMode() || targetPlayer.isMounted())
		{
			return;
		}
		
		if (_cubicId < 0)
		{
			LOGGER.warn("Invalid Cubic ID: {} in skill ID: {}", _cubicId, skill.getId());
			return;
		}
		
		// If cubic is already present, it's replaced.
		final CubicInstance cubic = targetPlayer.getCubicById(_cubicId);
		if (cubic != null)
		{
			if (cubic.getTemplate().getLevel() > _cubicLvl)
			{
				// What do we do in such case?
				return;
			}
			
			cubic.deactivate();
		}
		else
		{
			// If maximum amount is reached, random cubic is removed.
			// Players with no mastery can have only one cubic.
			final int allowedCubicCount = (int) targetPlayer.getStat().getValue(DoubleStat.MAX_CUBIC, 1);
			final int currentCubicCount = targetPlayer.getCubics().size();
			// Extra cubics are removed, one by one, randomly.
			for (int i = 0; i <= (currentCubicCount - allowedCubicCount); i++)
			{
				final int removedCubicId = (int) targetPlayer.getCubics().keySet().toArray()[Rnd.get(currentCubicCount)];
				final CubicInstance removedCubic = targetPlayer.getCubicById(removedCubicId);
				removedCubic.deactivate();
			}
		}
		
		final L2CubicTemplate template = CubicData.getInstance().getCubicTemplate(_cubicId, _cubicLvl);
		if (template == null)
		{
			LOGGER.warn("Attempting to summon cubic without existing template id: {} level: {}", _cubicId, _cubicLvl);
			return;
		}
		
		// Adding a new cubic.
		targetPlayer.addCubic(new CubicInstance(targetPlayer, casterPlayer, template));
		targetPlayer.sendPacket(new ExUserInfoCubic(targetPlayer));
		targetPlayer.broadcastCharInfo();
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_summon_cubic", InstantSummonCubic::new);
	}
}
