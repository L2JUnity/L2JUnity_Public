/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.handlers.admincommandhandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.instancemanager.GlobalVariablesManager;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.variables.AbstractVariables;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.annotations.GameScript;

/**
 * @author UnAfraid
 */
public class AdminVariables implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_vars"
	};
	
	private final Map<String, DefaultVariablesEditor> _editors = new HashMap<>();
	
	public AdminVariables()
	{
		for (VariablesType type : VariablesType.values())
		{
			_editors.put(type.getType(), new DefaultVariablesEditor(type));
		}
	}
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		if (!st.hasMoreTokens())
		{
			return false;
		}
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_vars":
			{
				if (!st.hasMoreTokens())
				{
					String possible = "";
					for (VariablesType type : VariablesType.values())
					{
						possible += type.getType() + "/";
					}
					if (!possible.isEmpty())
					{
						possible = possible.substring(0, possible.length() - 1);
					}
					activeChar.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
					activeChar.sendMessage("Syntax: //vars <" + possible + ">");
					break;
				}
				
				final String type = st.nextToken();
				final DefaultVariablesEditor editor = _editors.get(type);
				if (editor == null)
				{
					activeChar.sendMessage("Unexisting variable type specified: " + type);
					break;
				}
				
				String remainingCmd = st.hasMoreTokens() ? st.nextToken() : "list";
				while (st.hasMoreTokens())
				{
					remainingCmd += " " + st.nextToken();
				}
				
				switch (editor.getType())
				{
					case GLOBAL_VARIABLES:
					{
						return editor.useCommand(remainingCmd, activeChar, GlobalVariablesManager.getInstance());
					}
					case PLAYER_VARIABLES:
					{
						if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
						{
							activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
							break;
						}
						return editor.useCommand(remainingCmd, activeChar, activeChar.getTarget().getActingPlayer().getVariables());
					}
					case ACCOUNT_VARIABLES:
					{
						if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
						{
							activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
							break;
						}
						return editor.useCommand(remainingCmd, activeChar, activeChar.getTarget().getActingPlayer().getAccountVariables());
					}
					case CLAN_VARIABLES:
					{
						if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer() || (activeChar.getClan() == null))
						{
							activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
							break;
						}
						return editor.useCommand(remainingCmd, activeChar, activeChar.getTarget().getActingPlayer().getClan().getVariables());
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	protected static enum VariablesType
	{
		GLOBAL_VARIABLES("Global Variables", "global", "bypass admin_vars global", "data/html/admin/globalVars.htm"),
		PLAYER_VARIABLES("Player Variables", "player", "bypass admin_vars player", "data/html/admin/playerVars.htm"),
		ACCOUNT_VARIABLES("Account Variables", "account", "bypass admin_vars account", "data/html/admin/accVars.htm"),
		CLAN_VARIABLES("Clan Variables", "clan", "bypass admin_vars clan", "data/html/admin/clanVars.htm");
		
		private final String _name;
		private final String _type;
		private final String _bypassPrefix;
		private final String _varsHtmlPath;
		
		private VariablesType(String name, String type, String bypassPrefix, String htmlPath)
		{
			_name = name;
			_type = type;
			_bypassPrefix = bypassPrefix;
			_varsHtmlPath = htmlPath;
		}
		
		public String getName()
		{
			return _name;
		}
		
		public String getType()
		{
			return _type;
		}
		
		public String getBypassPrefix()
		{
			return _bypassPrefix;
		}
		
		public String getHtmlPath()
		{
			return _varsHtmlPath;
		}
	}
	
	protected static class DefaultVariablesEditor
	{
		private final VariablesType _type;
		
		protected DefaultVariablesEditor(VariablesType type)
		{
			_type = type;
		}
		
		protected VariablesType getType()
		{
			return _type;
		}
		
		protected boolean useCommand(String command, PlayerInstance activeChar, AbstractVariables vars)
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (!st.hasMoreTokens())
			{
				return false;
			}
			final String cmd = st.nextToken();
			switch (cmd)
			{
				case "list":
				{
					final NpcHtmlMessage msg = new NpcHtmlMessage();
					msg.setFile(activeChar.getHtmlPrefix(), _type.getHtmlPath());
					final StringBuilder sb = new StringBuilder();
					for (Entry<String, Object> entry : vars.getSet().entrySet())
					{
						sb.append("<tr>");
						sb.append("<td fixwidth=5></td>");
						sb.append("<td fixwidth=110>" + entry.getKey() + "</td>");
						sb.append("<td fixwidth=110>" + entry.getValue() + "</td>");
						sb.append("<td fixwidth=50><button action=\"" + _type.getBypassPrefix() + " set " + entry.getKey() + " $value\" value=\"Set\" width=\"50\" height=\"19\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
						sb.append("</tr>");
					}
					msg.replace("%vars%", sb.toString());
					activeChar.sendPacket(msg);
					break;
				}
				case "set":
				{
					if (!st.hasMoreTokens())
					{
						break;
					}
					final String key = st.nextToken();
					if (!st.hasMoreTokens())
					{
						break;
					}
					String value = st.nextToken();
					for (int i = 0; i < 9; i++)
					{
						if (value.equals("0" + i))
						{
							value = "" + i;
							break;
						}
					}
					vars.set(key, value);
					activeChar.sendMessage(_type.getName().substring(0, _type.getName().length() - 1) + ": " + key + " has been set to: " + value);
					return useCommand("list", activeChar, vars);
				}
				case "unset":
				{
					if (!st.hasMoreTokens())
					{
						break;
					}
					final String key = st.nextToken();
					final Object value = vars.getSet().remove(key);
					if (value == null)
					{
						activeChar.sendMessage("Unexisting variable name requested!");
					}
					else
					{
						activeChar.sendMessage(_type.getName().substring(0, _type.getName().length() - 1) + ": " + key + " has been unset");
					}
					return useCommand("list", activeChar, vars);
				}
			}
			return false;
		}
	}
	
	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminVariables());
	}
}
