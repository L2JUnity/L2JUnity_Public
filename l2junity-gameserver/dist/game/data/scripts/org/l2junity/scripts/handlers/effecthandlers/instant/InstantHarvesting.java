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
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.holders.ItemHolder;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Harvesting effect implementation.
 * @author l3x, Zoey76
 */
public final class InstantHarvesting extends AbstractEffect
{
	public InstantHarvesting(StatsSet params)
	{
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance casterPlayer = caster.asPlayer();
		if (casterPlayer == null)
		{
			return;
		}

		final L2MonsterInstance targetMonster = target.asMonster();
		if (targetMonster == null)
		{
			casterPlayer.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}

		if (!targetMonster.isDead())
		{
			return;
		}

		if (casterPlayer.getObjectId() != targetMonster.getSeederId())
		{
			casterPlayer.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
		}
		else if (targetMonster.isSeeded())
		{
			if (calcSuccess(casterPlayer, targetMonster))
			{
				final ItemHolder harvestedItem = targetMonster.takeHarvest();
				if (harvestedItem != null)
				{
					// Add item
					casterPlayer.getInventory().addItem("Harvesting", harvestedItem.getId(), harvestedItem.getCount(), casterPlayer, targetMonster);
					
					// Send system msg
					SystemMessage sm = null;
					if (item.getCount() == 1)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1);
						sm.addItemName(harvestedItem.getId());
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1);
						sm.addItemName(item.getId());
						sm.addLong(harvestedItem.getCount());
					}
					casterPlayer.sendPacket(sm);
					
					// Send msg to party
					final Party party = casterPlayer.getParty();
					if (party != null)
					{
						if (item.getCount() == 1)
						{
							sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HARVESTED_S2);
							sm.addString(casterPlayer.getName());
							sm.addItemName(harvestedItem.getId());
						}
						else
						{
							sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HARVESTED_S3_S2_S);
							sm.addString(casterPlayer.getName());
							sm.addLong(harvestedItem.getCount());
							sm.addItemName(harvestedItem.getId());
						}
						party.broadcastToPartyMembers(casterPlayer, sm);
					}
				}
			}
			else
			{
				casterPlayer.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
			}
		}
		else
		{
			casterPlayer.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
		}
	}
	
	private static boolean calcSuccess(PlayerInstance activeChar, L2MonsterInstance target)
	{
		final int levelPlayer = activeChar.getLevel();
		final int levelTarget = target.getLevel();
		
		int diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		
		// apply penalty, target <=> player levels
		// 5% penalty for each level
		int basicSuccess = 100;
		if (diff > 5)
		{
			basicSuccess -= (diff - 5) * 5;
		}
		
		// success rate can't be less than 1%
		if (basicSuccess < 1)
		{
			basicSuccess = 1;
		}
		return Rnd.nextInt(99) < basicSuccess;
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_harvesting", InstantHarvesting::new);
	}
}
