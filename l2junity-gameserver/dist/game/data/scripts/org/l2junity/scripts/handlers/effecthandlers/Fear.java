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

import org.l2junity.gameserver.ai.CtrlEvent;
import org.l2junity.gameserver.ai.CtrlIntention;
import org.l2junity.gameserver.enums.Race;
import org.l2junity.gameserver.geodata.GeoData;
import org.l2junity.gameserver.handler.EffectHandler;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.L2DefenderInstance;
import org.l2junity.gameserver.model.actor.instance.L2FortCommanderInstance;
import org.l2junity.gameserver.model.actor.instance.L2SiegeFlagInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.scripting.annotations.SkillScript;
import org.l2junity.gameserver.util.Util;

/**
 * Fear effect implementation.
 * @author littlecrow
 */
public final class Fear extends AbstractEffect
{
	public static final int FEAR_RANGE = 500;
	
	public Fear(StatsSet params)
	{
		
	}
	
	@Override
	public boolean checkPumpCondition(Creature caster, Creature target, Skill skill)
	{
		return target.isPlayer() || target.isSummon() || (target.isAttackable() && //
			!((target instanceof L2DefenderInstance) || (target instanceof L2FortCommanderInstance) || //
				(target instanceof L2SiegeFlagInstance) || (target.getTemplate().getRace() == Race.SIEGE_WEAPON)));
	}
	
	@Override
	public int getTicks()
	{
		return 5;
	}
	
	@Override
	public void tick(Creature caster, Creature target, Skill skill)
	{
		fearAction(null, target);
	}
	
	@Override
	public void pumpStart(Creature caster, Creature target, Skill skill)
	{
		target.getAI().notifyEvent(CtrlEvent.EVT_AFRAID);
		fearAction(caster, target);
	}
	
	@Override
	public void pumpEnd(Creature caster, Creature target, Skill skill)
	{
		if (!target.isPlayer())
		{
			target.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	private void fearAction(Creature effector, Creature effected)
	{
		double radians = Math.toRadians((effector != null) ? effector.calculateAngleTo(effected) : Util.convertHeadingToDegree(effected.getHeading()));
		
		double posX = effected.getX() + (FEAR_RANGE * Math.cos(radians));
		double posY = effected.getY() + (FEAR_RANGE * Math.sin(radians));
		double posZ = effected.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(effected.getX(), effected.getY(), effected.getZ(), posX, posY, posZ, effected.getInstanceWorld());
		effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
	}
	
	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("Fear", Fear::new);
	}
}
