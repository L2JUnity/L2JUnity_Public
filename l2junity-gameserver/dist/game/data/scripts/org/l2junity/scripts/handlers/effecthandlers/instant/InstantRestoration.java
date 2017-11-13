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

import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Restoration effect implementation.
 * @author Zoey76
 */
public final class InstantRestoration extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InstantRestoration.class);
	
	private final int _itemId;
	private final int _itemCount;
	
	public InstantRestoration(StatsSet params)
	{
		_itemId = params.getInt("itemId");
		_itemCount = params.getInt("itemCount");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}
		
		if ((_itemId <= 0) || (_itemCount <= 0))
		{
			casterPlayer.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
			LOGGER.warn("Effect with wrong item Id/count: {}/{}!", _itemId, _itemCount);
			return;
		}
		
		casterPlayer.addItem("Skill", _itemId, _itemCount, casterPlayer, true);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_restoration", InstantRestoration::new);
	}
}
