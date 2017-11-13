/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.playeractions;

import java.util.Optional;

import org.l2junity.gameserver.handler.IPlayerActionHandler;
import org.l2junity.gameserver.handler.PlayerActionHandler;
import org.l2junity.gameserver.model.ActionDataHolder;
import org.l2junity.gameserver.model.actor.Summon;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.holders.SkillHolder;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * Summon skill use player action handler.
 * @author Nik
 */
public final class ServitorSkillUse implements IPlayerActionHandler
{
	@Override
	public void useAction(PlayerInstance activeChar, ActionDataHolder data, boolean ctrlPressed, boolean shiftPressed)
	{
		if (activeChar.getTarget() == null)
		{
			return;
		}
		
		final Summon summon = activeChar.getAnyServitor();
		if ((summon == null) || !summon.isServitor())
		{
			activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
			return;
		}
		
		activeChar.getServitors().values().forEach(servitor ->
		{
			if (servitor.isBetrayed())
			{
				activeChar.sendPacket(SystemMessageId.YOUR_PET_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}
			final Optional<SkillHolder> holder = servitor.getTemplate().getParameters().getSkillHolder(data.getOptionId());
			if (holder.isPresent())
			{
				servitor.setTarget(activeChar.getTarget());
				servitor.useMagic(holder.get().getSkill(), servitor.getTarget(), null, ctrlPressed, shiftPressed);
			}
		});
	}
	
	@GameScript
	public static void main()
	{
		PlayerActionHandler.getInstance().registerHandler(new ServitorSkillUse());
	}
}
