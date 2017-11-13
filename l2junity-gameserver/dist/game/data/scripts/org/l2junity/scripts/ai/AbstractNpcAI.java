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
package org.l2junity.scripts.ai;

import java.awt.Color;

import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.debugger.DebugType;
import org.l2junity.gameserver.model.holders.MinionHolder;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.network.client.send.ExServerPrimitive;
import org.l2junity.gameserver.network.client.send.SocialAction;
import org.l2junity.gameserver.util.Broadcast;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract NPC AI class for datapack based AIs.
 * @author UnAfraid, Zoey76
 */
public abstract class AbstractNpcAI extends Quest
{
	protected final Logger _logger = LoggerFactory.getLogger(getClass());
	
	public AbstractNpcAI()
	{
		super(-1);
	}
	
	/**
	 * Simple on first talk event handler.
	 */
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".html";
	}
	
	/**
	 * Registers the following events to the current script:<br>
	 * <ul>
	 * <li>ON_ATTACK</li>
	 * <li>ON_KILL</li>
	 * <li>ON_SPAWN</li>
	 * <li>ON_SPELL_FINISHED</li>
	 * <li>ON_SKILL_SEE</li>
	 * <li>ON_FACTION_CALL</li>
	 * <li>ON_AGGR_RANGE_ENTER</li>
	 * </ul>
	 * @param mobs
	 */
	public void registerMobs(int... mobs)
	{
		addAttackId(mobs);
		addKillId(mobs);
		addSpawnId(mobs);
		addSpellFinishedId(mobs);
		addSkillSeeId(mobs);
		addAggroRangeEnterId(mobs);
		addFactionCallId(mobs);
	}
	
	/**
	 * Broadcasts SocialAction packet to self and known players.
	 * @param character
	 * @param actionId
	 */
	protected void broadcastSocialAction(Creature character, int actionId)
	{
		Broadcast.toSelfAndKnownPlayers(character, new SocialAction(character.getObjectId(), actionId));
	}
	
	/**
	 * Broadcasts SocialAction packet to self and known players in specific radius.
	 * @param character
	 * @param actionId
	 * @param radius
	 */
	protected void broadcastSocialAction(Creature character, int actionId, int radius)
	{
		Broadcast.toSelfAndKnownPlayersInRadius(character, new SocialAction(character.getObjectId(), actionId), radius);
	}
	
	public void spawnMinions(final Npc npc, final String spawnName)
	{
		for (MinionHolder is : npc.getParameters().getMinionList(spawnName))
		{
			addMinion((L2MonsterInstance) npc, is.getId());
		}
	}
	
	protected void followNpc(final Npc npc, int followedNpcId, int followingAngle, int minDistance, int maxDistance)
	{
		// TODO: Handle if someone buffs Rinne for speed the same speed has to be synchronized with Allada (Verified on NCWest)
		World.getInstance().forEachVisibleObject(npc, Npc.class, npcAround ->
		{
			if (npcAround.getId() != followedNpcId)
			{
				return;
			}
			
			// Whenever Allada gets behind Rinne he gets temporal speed boost until he catches up with her.
			final double distance = npc.distance3d(npcAround);
			if ((distance >= maxDistance) && npc.isScriptValue(0))
			{
				npc.setRunning();
				npc.setScriptValue(1);
				npc.sendDebugMessage("Running temporary to catch up with " + npcAround.getName(), DebugType.WALKER);
			}
			else if ((distance <= (minDistance * 1.5)) && npc.isScriptValue(1))
			{
				npc.setWalking();
				npc.setScriptValue(0);
				npc.sendDebugMessage("Walking back since i reached her/him", DebugType.WALKER);
			}
			
			final Location rinneLocation = npcAround.getLocation();
			final double course = Math.toRadians(followingAngle);
			final double radian = Math.toRadians(Util.convertHeadingToDegree(npcAround.getHeading()));
			final double nRadius = npc.getCollisionRadius() + npcAround.getCollisionRadius() + minDistance;
			
			final double x = rinneLocation.getX() + (Math.cos(Math.PI + radian + course) * nRadius);
			final double y = rinneLocation.getY() + (Math.sin(Math.PI + radian + course) * nRadius);
			final double z = rinneLocation.getZ();
			
			final Location newLocation = new Location(x, y, z);
			if (npc.isDebug())
			{
				final ExServerPrimitive primitive = new ExServerPrimitive(npc.getName(), npc);
				primitive.addLine("(Distance with " + npcAround.getName() + ": " + Math.round(distance) + ") Moving to next point " + npc.getScriptValue() + " " + npc.isRunning(), Color.RED, true, npc, newLocation);
				npc.sendDebugPacket(primitive);
			}
			npc.getAI().moveTo(newLocation);
		});
	}
}