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
package org.l2junity.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import org.l2junity.commons.util.TimeAmountInterpreter;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.data.xml.impl.AdminData;
import org.l2junity.gameserver.enums.PlayerAction;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.ConfirmDlg;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.util.GMAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public final class AdminCommandHandler implements IHandler<IAdminCommandHandler, String>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminCommandHandler.class);
	
	private final Map<String, IAdminCommandHandler> _datatable;
	
	protected AdminCommandHandler()
	{
		_datatable = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IAdminCommandHandler handler)
	{
		String[] ids = handler.getAdminCommandList();
		for (String id : ids)
		{
			_datatable.put(id, handler);
		}
	}
	
	@Override
	public synchronized void removeHandler(IAdminCommandHandler handler)
	{
		String[] ids = handler.getAdminCommandList();
		for (String id : ids)
		{
			_datatable.remove(id);
		}
	}
	
	/**
	 * WARNING: Please use {@link #useAdminCommand(PlayerInstance, String, boolean)} instead.
	 */
	@Override
	public IAdminCommandHandler getHandler(String adminCommand)
	{
		String command = adminCommand;
		if (adminCommand.contains(" "))
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		return _datatable.get(command);
	}
	
	/**
	 * Three {@link #useAdminCommand(PlayerInstance, String, boolean)} methods for the Elven-Sdw-kings under the sky,<br>
	 * Seven for the UnAfraid Dwarf-lords in their halls of stone,<br>
	 * Nine for Mortal Nik doomed to die,<br>
	 * One for the Dark Lord(Rex) on his dark throne.<br>
	 * One {@link #useAdminCommand(PlayerInstance, String, boolean)} method to rule them all. One {@link #useAdminCommand(PlayerInstance, String, boolean)} method to find them,<br>
	 * One {@link #useAdminCommand(PlayerInstance, String, boolean)} method to bring them all and in the darkness bind them.<br>
	 * @param player
	 * @param fullCommand
	 * @param useConfirm
	 */
	public void useAdminCommand(PlayerInstance player, String fullCommand, boolean useConfirm)
	{
		final String command = fullCommand.split(" ")[0];
		final String commandNoPrefix = command.substring(6);
		
		final IAdminCommandHandler handler = getHandler(command);
		if (handler == null)
		{
			if (player.isGM())
			{
				player.sendMessage("The command '" + commandNoPrefix + "' does not exist!");
			}
			LOGGER.warn("No handler registered for admin command '{}'", command);
			return;
		}
		
		if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
		{
			player.sendMessage("You don't have the access rights to use this command!");
			LOGGER.warn("Player {} tried to use admin command '{}', without proper access level!", player.getName(), command);
			return;
		}
		
		if (useConfirm && AdminData.getInstance().requireConfirm(command))
		{
			player.setAdminConfirmCmd(fullCommand);
			final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S13);
			dlg.addString("Are you sure you want execute command '" + commandNoPrefix + "' ?");
			player.addAction(PlayerAction.ADMIN_COMMAND);
			player.sendPacket(dlg);
		}
		else
		{
			// Admin Commands must run through a long running task, otherwise a command that takes too much time will freeze the server, this way you'll feel only a minor spike.
			ThreadPool.execute(() ->
			{
				final long begin = System.currentTimeMillis();
				try
				{
					if (GeneralConfig.GMAUDIT)
					{
						final WorldObject target = player.getTarget();
						GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", fullCommand, (target != null ? target.getName() : "no-target"));
					}
					
					handler.useAdminCommand(fullCommand, player);
				}
				catch (final RuntimeException e)
				{
					player.sendMessage("Exception during execution of  '" + fullCommand + "': " + e.toString());
					LOGGER.warn("Exception during execution of  {}", fullCommand, e);
				}
				finally
				{
					final long runtime = System.currentTimeMillis() - begin;
					
					if (runtime < 5000)
					{
						return;
					}
					
					player.sendMessage("The execution of '" + fullCommand + "' took " + TimeAmountInterpreter.consolidateMillis(runtime) + ".");
				}
			});
		}
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}
