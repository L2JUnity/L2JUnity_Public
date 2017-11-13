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
import org.l2junity.gameserver.enums.QuestSound;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.L2Seed;
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.SystemMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Sow effect implementation.
 * @author Adry_85, l3x
 */
public final class InstantSow extends AbstractEffect
{
	public InstantSow(StatsSet params)
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
			return;
		}
		
		if (targetMonster.isDead() || !targetMonster.getTemplate().canBeSown() || targetMonster.isSeeded() || (targetMonster.getSeederId() != casterPlayer.getObjectId()))
		{
			return;
		}
		
		// Consuming used seed
		final L2Seed seed = targetMonster.getSeed();
		if (!casterPlayer.destroyItemByItemId("Consume", seed.getSeedId(), 1, targetMonster, false))
		{
			return;
		}
		
		final SystemMessage sm;
		if (calcSuccess(casterPlayer, targetMonster, seed))
		{
			casterPlayer.sendPacket(QuestSound.ITEMSOUND_QUEST_ITEMGET.getPacket());
			targetMonster.setSeeded(casterPlayer.asPlayer());
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SEED_WAS_SUCCESSFULLY_SOWN);
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_SEED_WAS_NOT_SOWN);
		}
		
		Party party = casterPlayer.getParty();
		if (party != null)
		{
			party.broadcastPacket(sm);
		}
		else
		{
			casterPlayer.sendPacket(sm);
		}
	}
	
	private static boolean calcSuccess(Creature activeChar, Creature target, L2Seed seed)
	{
		// TODO: check all the chances
		final int minlevelSeed = seed.getLevel() - 5;
		final int maxlevelSeed = seed.getLevel() + 5;
		final int levelPlayer = activeChar.getLevel(); // Attacker Level
		final int levelTarget = target.getLevel(); // target Level
		int basicSuccess = seed.isAlternative() ? 20 : 90;
		
		// seed level
		if (levelTarget < minlevelSeed)
		{
			basicSuccess -= 5 * (minlevelSeed - levelTarget);
		}
		if (levelTarget > maxlevelSeed)
		{
			basicSuccess -= 5 * (levelTarget - maxlevelSeed);
		}
		
		// 5% decrease in chance if player level
		// is more than +/- 5 levels to _target's_ level
		int diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		if (diff > 5)
		{
			basicSuccess -= 5 * (diff - 5);
		}
		
		// chance can't be less than 1%
		Math.max(basicSuccess, 1);
		return Rnd.nextInt(99) < basicSuccess;
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_sow", InstantSow::new);
	}
}
