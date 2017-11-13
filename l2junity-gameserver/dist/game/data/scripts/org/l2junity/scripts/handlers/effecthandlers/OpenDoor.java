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
package org.l2junity.scripts.handlers.effecthandlers;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.DoorInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Open Door effect implementation.
 * @author Adry_85
 */
public final class OpenDoor extends AbstractEffect
{
	private final int _chance;
	private final boolean _isItem;
	
	public OpenDoor(StatsSet params)
	{
		_chance = params.getInt("chance", 0);
		_isItem = params.getBoolean("isItem", false);
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final DoorInstance targetDoor = target.asDoor();
		if (targetDoor == null)
		{
			return;
		}
		
		if (caster.getInstanceWorld() != targetDoor.getInstanceWorld())
		{
			return;
		}
		
		if ((!targetDoor.isOpenableBySkill() && !_isItem) || (targetDoor.getFort() != null))
		{
			caster.sendPacket(SystemMessageId.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return;
		}
		
		if ((Rnd.get(100) < _chance) && !targetDoor.isOpen())
		{
			targetDoor.openMe();
		}
		else
		{
			caster.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
		}
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("OpenDoor", OpenDoor::new);
	}
}
