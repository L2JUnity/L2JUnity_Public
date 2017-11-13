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

import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.data.xml.impl.SkillData;
import org.l2junity.gameserver.enums.SubclassInfoType;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.AcquireSkillList;
import org.l2junity.gameserver.network.client.send.ExSubjobInfo;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.ability.ExAcquireAPSkillList;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * @author Sdw
 */
public class InstantChangeClass extends AbstractEffect
{
	private final int _index;
	private final static int IDENTITY_CRISIS_SKILL_ID = 1570;
	
	public InstantChangeClass(StatsSet params)
	{
		_index = params.getInt("index");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		// TODO: FIX ME - Executing 1 second later otherwise interupted exception during storeCharBase()
		ThreadPool.schedule(() ->
		{
			final int activeClass = targetPlayer.getClassId().getId();
			
			if (!targetPlayer.setActiveClass(_index))
			{
				targetPlayer.sendMessage("You cannot switch your class right now!");
				return;
			}
			
			final Skill identifyCrisis = SkillData.getInstance().getSkill(IDENTITY_CRISIS_SKILL_ID, 1);
			if (identifyCrisis != null)
			{
				identifyCrisis.applyEffects(targetPlayer, targetPlayer);
			}
			
			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_SWITCHED_S1_TO_S2);
			msg.addClassId(activeClass);
			msg.addClassId(targetPlayer.getClassId().getId());
			targetPlayer.sendPacket(msg);
			
			targetPlayer.broadcastUserInfo();
			targetPlayer.sendPacket(new AcquireSkillList(targetPlayer));
			targetPlayer.sendPacket(new ExSubjobInfo(targetPlayer, SubclassInfoType.CLASS_CHANGED));
			targetPlayer.sendPacket(new ExAcquireAPSkillList(targetPlayer));
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_class_change", InstantChangeClass::new);
	}
}
