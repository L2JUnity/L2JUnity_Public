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
package org.l2junity.scripts.handlers.itemhandlers;

import java.util.List;

import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.enums.ItemSkillType;
import org.l2junity.gameserver.handler.IItemHandler;
import org.l2junity.gameserver.handler.ItemHandler;
import org.l2junity.gameserver.instancemanager.CastleManorManager;
import org.l2junity.gameserver.model.L2Seed;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Playable;
import org.l2junity.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.entity.Castle;
import org.l2junity.gameserver.model.holders.ItemSkillHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.network.client.send.ActionFailed;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * @author l3x
 */
public class Seed implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!GeneralConfig.ALLOW_MANOR)
		{
			return false;
		}
		else if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final WorldObject tgt = playable.getTarget();
		if (!tgt.isNpc())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		else if (!tgt.isMonster() || ((L2MonsterInstance) tgt).isRaid() || tgt.isTreasure())
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return false;
		}
		
		final L2MonsterInstance target = (L2MonsterInstance) tgt;
		if (target.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		else if (target.isSeeded())
		{
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final L2Seed seed = CastleManorManager.getInstance().getSeed(item.getId());
		if (seed == null)
		{
			return false;
		}
		
		final Castle taxCastle = target.getTaxCastle();
		if ((taxCastle == null) || (seed.getCastleId() != taxCastle.getResidenceId()))
		{
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return false;
		}
		
		final PlayerInstance activeChar = playable.getActingPlayer();
		target.setSeeded(seed, activeChar);
		
		final List<ItemSkillHolder> skills = item.getItem().getSkills(ItemSkillType.NORMAL);
		if (skills != null)
		{
			skills.forEach(holder -> activeChar.useMagic(holder.getSkill(), target, item, false, false));
		}
		return true;
	}
	
	@GameScript
	public static void main()
	{
		ItemHandler.getInstance().registerHandler(new Seed());
	}
}