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
package org.l2junity.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import org.l2junity.gameserver.data.xml.impl.TeleportersData;
import org.l2junity.gameserver.enums.InstanceType;
import org.l2junity.gameserver.enums.TeleportType;
import org.l2junity.gameserver.instancemanager.CastleManager;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.Npc;
import org.l2junity.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2junity.gameserver.model.skills.CommonSkill;
import org.l2junity.gameserver.model.teleporter.TeleportHolder;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NightMarez
 */
public final class L2TeleporterInstance extends Npc
{
	private static final Logger LOGGER = LoggerFactory.getLogger(L2TeleporterInstance.class);

	private static final CommonSkill[] FORBIDDEN_TRANSFORM =
	{
		CommonSkill.FROG_TRANSFORM,
		CommonSkill.CHILD_TRANSFORM,
		CommonSkill.NATIVE_TRANSFORM
	};
	
	public L2TeleporterInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2TeleporterInstance);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster() || super.isAutoAttackable(attacker);
	}
	
	@Override
	public void onBypassFeedback(PlayerInstance player, String command)
	{
		// Check if transformed
		for (CommonSkill skill : FORBIDDEN_TRANSFORM)
		{
			if (player.isAffectedBySkill(skill.getId()))
			{
				sendHtmlMessage(player, "data/html/teleporter/epictransformed.htm");
				return;
			}
		}
		
		// Process bypass
		final StringTokenizer st = new StringTokenizer(command, " ");
		switch (st.nextToken())
		{
			case "showNoblesSelect":
			{
				sendHtmlMessage(player, "data/html/teleporter/" + (player.isNoble() ? "nobles_select" : "not_nobles") + ".htm");
				break;
			}
			case "showTeleports":
			{
				final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.NORMAL.name();
				final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warn("Player {} requested show teleports for list with name {} at NPC {}!", player.getObjectId(), listName, getId());
					return;
				}
				holder.showTeleportList(player, this);
				break;
			}
			case "teleport":
			{
				// Check for required count of params.
				if (st.countTokens() != 2)
				{
					LOGGER.warn("Player {} send unhandled teleport command: {}", player.getObjectId(), command);
					return;
				}
				
				final String listName = st.nextToken();
				final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warn("Player {} requested unknown teleport list: {} for npc: {}!", player.getObjectId(), listName, getId());
					return;
				}
				holder.doTeleport(player, this, parseNextInt(st, -1));
				break;
			}
			case "chat":
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException | NumberFormatException ignored)
				{
				}
				showChatWindow(player, val);
				break;
			}
			default:
			{
				super.onBypassFeedback(player, command);
			}
		}
	}
	
	private int parseNextInt(StringTokenizer st, int defaultVal)
	{
		if (st.hasMoreTokens())
		{
			final String token = st.nextToken();
			if (Util.isDigit(token))
			{
				return Integer.valueOf(token);
			}
		}
		return defaultVal;
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		final String pom = (val == 0) ? String.valueOf(npcId) : (npcId + "-" + val);
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	@Override
	public void showChatWindow(PlayerInstance player)
	{
		// Teleporter isn't on castle ground
		if (CastleManager.getInstance().getCastle(this) == null)
		{
			super.showChatWindow(player);
			return;
		}
		
		// Teleporter is on castle ground
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		if ((player.getClan() != null) && (getCastle().getOwnerId() == player.getClanId())) // Clan owns castle
		{
			filename = getHtmlPath(getId(), 0); // Owner message window
		}
		else if (getCastle().getSiege().isInProgress()) // Teleporter is busy due siege
		{
			filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
		}
		sendHtmlMessage(player, filename);
	}
	
	private void sendHtmlMessage(PlayerInstance player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getHtmlPrefix(), filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
}
