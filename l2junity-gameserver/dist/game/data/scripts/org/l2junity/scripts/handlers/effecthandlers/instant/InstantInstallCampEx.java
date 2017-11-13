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

import org.l2junity.gameserver.data.xml.impl.NpcData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2SiegeFlagInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.model.stats.DoubleStat;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Headquarter Create effect implementation.
 * @author Adry_85
 */
public final class InstantInstallCampEx extends AbstractEffect
{
	private static final int HQ_NPC_ID = 35062;
	
	public InstantInstallCampEx(StatsSet params)
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
		
		if ((casterPlayer.getClan() == null) || (casterPlayer.getClan().getLeaderId() != casterPlayer.getObjectId()))
		{
			return;
		}
		
		final L2SiegeFlagInstance flag = new L2SiegeFlagInstance(casterPlayer, NpcData.getInstance().getTemplate(HQ_NPC_ID), true);
		flag.setTitle(casterPlayer.getClan().getName());
		flag.getStat().mulAdditionalStat(DoubleStat.MAX_HP, 2);
		flag.getStat().recalculateStats(false);
		flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
		flag.setHeading(casterPlayer.getHeading());
		flag.spawnMe(casterPlayer.getX(), casterPlayer.getY(), casterPlayer.getZ() + 50);
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_install_camp_ex", InstantInstallCampEx::new);
	}
}
