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
import org.l2junity.gameserver.model.Party;
import org.l2junity.gameserver.model.StatsSet;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.effects.AbstractEffect;
import org.l2junity.gameserver.model.interfaces.ILocational;
import org.l2junity.gameserver.model.items.instance.ItemInstance;
import org.l2junity.gameserver.model.skills.Skill;
import org.l2junity.gameserver.network.client.send.FlyToLocation;
import org.l2junity.gameserver.network.client.send.FlyToLocation.FlyType;
import org.l2junity.gameserver.network.client.send.ValidateLocation;
import org.l2junity.gameserver.scripting.annotations.SkillScript;

/**
 * Teleport party to summoned npc effect implementation.
 * @author Nik
 */
public final class InstantPartyTeleportToNpc extends AbstractEffect
{
	private final int _npcId;
	
	public InstantPartyTeleportToNpc(StatsSet params)
	{
		_npcId = params.getInt("npcId");
	}
	
	@Override
	public void instant(Creature caster, WorldObject target, Skill skill, ItemInstance item)
	{
		final PlayerInstance targetPlayer = target.asPlayer();
		if (targetPlayer == null)
		{
			return;
		}
		
		final ILocational teleLocation = caster.getSummonedNpcs().stream().filter(npc -> npc.getId() == _npcId).findAny().orElse(null);
		if (teleLocation != null)
		{
			final Party party = targetPlayer.getParty();
			if (party != null)
			{
				party.getMembers().forEach(p -> teleport(p, teleLocation));
			}
		}
	}
	
	private void teleport(Creature effected, ILocational location)
	{
		if (effected.isInRadius2d(location, 900))
		{
			effected.broadcastPacket(new FlyToLocation(effected, location, FlyType.DUMMY));
			effected.stopActions();
			effected.setXYZ(location);
			effected.broadcastPacket(new ValidateLocation(effected));
			effected.revalidateZone(true);
		}
		else
		{
			effected.teleToLocation(location);
		}
	}

	@SkillScript
	public static void main()
	{
		EffectHandler.getInstance().registerHandler("i_party_teleport_to_npc", InstantPartyTeleportToNpc::new);
	}
}
