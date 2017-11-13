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
package org.l2junity.scripts.handlers.admincommandhandlers;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.tools.JavaFileObject.Kind;

import org.l2junity.gameserver.enums.QuestType;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.events.EventType;
import org.l2junity.gameserver.model.events.ListenerRegisterType;
import org.l2junity.gameserver.model.events.listeners.AbstractEventListener;
import org.l2junity.gameserver.model.quest.Quest;
import org.l2junity.gameserver.model.quest.QuestState;
import org.l2junity.gameserver.model.quest.QuestTimer;
import org.l2junity.gameserver.model.quest.State;
import org.l2junity.gameserver.network.client.send.ExShowQuestMark;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.network.client.send.QuestList;
import org.l2junity.gameserver.network.client.send.string.SystemMessageId;
import org.l2junity.gameserver.scripting.ScriptsManager;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminQuest implements IAdminCommandHandler
{
	public static final Logger LOGGER = LoggerFactory.getLogger(AdminQuest.class);

	private static final String[] ADMIN_COMMANDS =
			{
					"admin_quest_reload",
					"admin_script_load",
					"admin_script_unload",
					"admin_script_dir",
					"admin_show_quests",
					"admin_quest_info",
					"admin_charquestmenu",
					"admin_setcharquest",
					"admin_setquest",
			};

	private static Quest findScript(String script)
	{
		if (Util.isDigit(script))
		{
			return QuestManager.getInstance().getQuest(Integer.parseInt(script));
		}
		return QuestManager.getInstance().getQuest(script);
	}

	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.startsWith("admin_quest_reload"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token

			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Usage: //quest_reload <questName> or <questId>");
				return false;
			}

			String script = st.nextToken();
			Quest quest = findScript(script);
			if (quest == null)
			{
				activeChar.sendMessage("The script " + script + " couldn't be found!");
				return false;
			}

			if (!quest.reload())
			{
				activeChar.sendMessage("Failed to reload " + script + "!");
				return false;
			}

			activeChar.sendMessage("Script successful reloaded.");
		}
		else if (command.startsWith("admin_script_load"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token

			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Usage: //script_load className");
				return false;
			}

			String script = st.nextToken();
			try
			{
				ScriptsManager.getInstance().runScript(script, GameScript.class);
				activeChar.sendMessage("Script loaded seccessful!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Failed to load script!");
				LOGGER.warn("Failed to load script " + script + "!", e);
			}
		}
		else if (command.startsWith("admin_script_unload"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token

			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Usage: //script_load path/to/script.java");
				return false;
			}

			String script = st.nextToken();
			Quest quest = findScript(script);
			if (quest == null)
			{
				activeChar.sendMessage("The script " + script + " couldn't be found!");
				return false;
			}

			quest.unload();
			activeChar.sendMessage("Script successful unloaded!");
		}
		else if (command.startsWith("admin_show_quests"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendMessage("Get a target first.");
			}
			else if (!activeChar.getTarget().isCreature())
			{
				activeChar.sendMessage("Invalid Target.");
			}
			else
			{
				final Creature character = (Creature) activeChar.getTarget();
				final StringBuilder sb = new StringBuilder();
				final Set<String> questNames = new TreeSet<>();
				for (EventType type : EventType.values())
				{
					for (AbstractEventListener listener : character.getListeners(type))
					{
						if (listener.getOwner() instanceof Quest)
						{
							final Quest quest = (Quest) listener.getOwner();
							if (!questNames.add(quest.getName()))
							{
								continue;
							}
							sb.append("<tr><td colspan=\"4\"><font color=\"LEVEL\"><a action=\"bypass -h admin_quest_info " + quest.getName() + "\">" + quest.getName() + "</a></font></td></tr>");
						}
					}
				}

				final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
				msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npc-quests.htm");
				msg.replace("%quests%", sb.toString());
				msg.replace("%objid%", character.getObjectId());
				msg.replace("%questName%", "");
				activeChar.sendPacket(msg);
			}
		}
		else if (command.startsWith("admin_quest_info "))
		{
			final String questName = command.substring("admin_quest_info ".length());
			final Quest quest = QuestManager.getInstance().getQuest(questName);
			String events = "", npcs = "", items = "", timers = "";
			int counter = 0;
			if (quest == null)
			{
				activeChar.sendMessage("Couldn't find quest or script with name " + questName + " !");
				return false;
			}

			final Set<EventType> listenerTypes = new TreeSet<>();
			for (AbstractEventListener listener : quest.getListeners())
			{
				if (listenerTypes.add(listener.getType()))
				{
					events += ", " + listener.getType().name();
					counter++;
				}
				if (counter > 10)
				{
					counter = 0;
					break;
				}
			}

			final Set<Integer> npcIds = new TreeSet<>(quest.getRegisteredIds(ListenerRegisterType.NPC));
			for (int npcId : npcIds)
			{
				npcs += ", " + npcId;
				counter++;
				if (counter > 50)
				{
					counter = 0;
					break;
				}
			}

			if (!events.isEmpty())
			{
				events = listenerTypes.size() + ": " + events.substring(2);
			}

			if (!npcs.isEmpty())
			{
				npcs = npcIds.size() + ": " + npcs.substring(2);
			}

			if (quest.getRegisteredItemIds() != null)
			{
				for (int itemId : quest.getRegisteredItemIds())
				{
					items += ", " + itemId;
					counter++;
					if (counter > 20)
					{
						counter = 0;
						break;
					}
				}
				items = quest.getRegisteredItemIds().length + ":" + items.substring(2);
			}

			for (List<QuestTimer> list : quest.getQuestTimers().values())
			{
				for (QuestTimer timer : list)
				{
					timers += "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">" + timer.getName() + ":</font> <font color=00FF00>Active: " + timer.getIsActive() + " Repeatable: " + timer.getIsRepeating() + " Player: " + timer.getPlayer() + " Npc: " + timer.getNpc() + "</font></td></tr></table></td></tr>";
					counter++;
					if (counter > 10)
					{
						break;
					}
				}
			}

			final StringBuilder sb = new StringBuilder();
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">ID:</font> <font color=00FF00>" + quest.getId() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Name:</font> <font color=00FF00>" + quest.getName() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Path:</font> <font color=00FF00>" + quest.getPath() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Events:</font> <font color=00FF00>" + events + "</font></td></tr></table></td></tr>");
			if (!npcs.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">NPCs:</font> <font color=00FF00>" + npcs + "</font></td></tr></table></td></tr>");
			}
			if (!items.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Items:</font> <font color=00FF00>" + items + "</font></td></tr></table></td></tr>");
			}
			if (!timers.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Timers:</font> <font color=00FF00></font></td></tr></table></td></tr>");
				sb.append(timers);
			}

			final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
			msg.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npc-quests.htm");
			msg.replace("%quests%", sb.toString());
			final String fileToReload = quest.getPath() + "/" + quest.getName() + Kind.SOURCE.extension;
			msg.replace("%questName%", "<table><tr><td width=\"50\" align=\"left\"><a action=\"bypass -h admin_script_load " + fileToReload + "\">Reload</a></td> <td width=\"150\"  align=\"center\"><a action=\"bypass -h admin_quest_info " + quest.getName() + "\">" + quest.getName() + "</a></td> <td width=\"50\" align=\"right\"><a action=\"bypass -h admin_script_unload " + quest.getName() + "\">Unload</a></td></tr></table>");
			activeChar.sendPacket(msg);
		}
		else if (command.startsWith("admin_setquest")) // Retail command.
		{
			final WorldObject targetObject = activeChar.getTarget();
			final PlayerInstance target = targetObject != null ? targetObject.asPlayer() : null;
			if (target == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}

			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // setquest
			final int questId = Integer.parseInt(st.nextToken());
			final int cond = Integer.parseInt(st.nextToken());
			final Quest quest = QuestManager.getInstance().getQuest(questId);
			if (quest == null)
			{
				activeChar.sendMessage("Quest with id: " + questId + " not found");
				return false;
			}

			final QuestState qs = quest.getQuestState(target, (cond == 0) || (cond == 1));
			if (qs == null)
			{
				activeChar.sendMessage("Cannot initialize new quest state with cond " + cond + " for player " + target.getName() + ". To initialize new quest state, use cond 0.");
				return false;
			}

			if (cond > 0)
			{
				qs.setState(State.STARTED);
				qs.setCond(cond);
				activeChar.sendMessage(target.getName() + "'s " + quest.getName() + " quest condition set to " + cond);
			}
			else
			{
				activeChar.sendMessage(target.getName() + "'s " + quest.getName() + " quest has been created. To start it, use //setquest " + questId + " 1");
				showQuestMenu(target, activeChar, new String[]
						{
								"name",
								quest.getName()
						});
			}

		}
		else if (command.startsWith("admin_charquestmenu") || command.startsWith("admin_setcharquest"))
		{
			String[] cmdParams = command.split(" ");
			PlayerInstance target = null;
			WorldObject targetObject = null;
			String[] val = new String[4];
			val[0] = null;

			if (cmdParams.length > 1)
			{
				target = World.getInstance().getPlayer(cmdParams[1]);
				if (cmdParams.length > 2)
				{
					if (cmdParams[2].equals("0") || cmdParams[2].equals("1") || cmdParams[2].equals("2"))
					{
						val[0] = "var";
						val[1] = cmdParams[2];
					}
					if (cmdParams[2].equals("3"))
					{
						val[0] = "full";
					}
					if (cmdParams[2].indexOf("_") != -1)
					{
						val[0] = "name";
						val[1] = cmdParams[2];
					}
					if (cmdParams.length > 3)
					{
						if (cmdParams[3].equals("custom"))
						{
							val[0] = "custom";
							val[1] = cmdParams[2];
						}
					}
				}
			}
			else
			{
				targetObject = activeChar.getTarget();

				if ((targetObject != null) && targetObject.isPlayer())
				{
					target = targetObject.getActingPlayer();
				}
			}

			if (target == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}

			if (command.startsWith("admin_charquestmenu"))
			{
				if (val[0] != null)
				{
					showQuestMenu(target, activeChar, val);
				}
				else
				{
					showFirstQuestMenu(target, activeChar);
				}
			}
			else if (command.startsWith("admin_setcharquest"))
			{
				if (cmdParams.length >= 5)
				{
					val[0] = cmdParams[2];
					val[1] = cmdParams[3];
					val[2] = cmdParams[4];
					if (cmdParams.length == 6)
					{
						val[3] = cmdParams[5];
					}
					setQuestVar(target, activeChar, val);
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	private static void showFirstQuestMenu(PlayerInstance target, PlayerInstance actor)
	{
		StringBuilder replyMSG = new StringBuilder("<html><body><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center>Player: " + target.getName() + "</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin6\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		int ID = target.getObjectId();

		replyMSG.append("Quest Menu for <font color=\"LEVEL\">" + target.getName() + "</font> (ID:" + ID + ")<br><center>");
		replyMSG.append("<table width=250><tr><td><button value=\"CREATED\" action=\"bypass -h admin_charquestmenu " + target.getName() + " 0\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"STARTED\" action=\"bypass -h admin_charquestmenu " + target.getName() + " 1\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"COMPLETED\" action=\"bypass -h admin_charquestmenu " + target.getName() + " 2\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><br><button value=\"All\" action=\"bypass -h admin_charquestmenu " + target.getName() + " 3\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><br><br>Manual Edit by Quest number:<br></td></tr>");
		replyMSG.append("<tr><td><edit var=\"qn\" width=50 height=15><br><button value=\"Edit\" action=\"bypass -h admin_charquestmenu " + target.getName() + " $qn custom\" width=50 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("</table></center></body></html>");
		adminReply.setHtml(replyMSG.toString());
		actor.sendPacket(adminReply);
	}

	private static void showQuestMenu(PlayerInstance target, PlayerInstance actor, String[] val)
	{
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);

		switch (val[0])
		{
			case "full":
			{
				replyMSG.append("<table width=250><tr><td>Full Quest List for <font color=\"LEVEL\">" + target.getName() + "</font> (ID:" + target.getObjectId() + ")</td></tr>");
				target.getAllQuestStates().forEach(qs -> replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu " + target.getName() + " " + qs.getQuestName() + "\">" + qs.getQuestName() + "</a></td></tr>"));
				replyMSG.append("</table></body></html>");
				break;
			}
			case "name":
			{
				final String questName = val[1];
				QuestState qs = target.getQuestState(questName);
				String state = (qs != null) ? State.getStateName(qs.getState()) : "N/A";
				replyMSG.append("<center><table><tr><td><button value=\"Back\" action=\"bypass -h admin_charquestmenu " + target.getName() + " 3\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Refresh\" action=\"bypass -h admin_charquestmenu " + target.getName() + " " + questName + "\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></center>");
				replyMSG.append("Character: <font color=\"LEVEL\">" + target.getName() + "</font><br>Quest: <font color=\"LEVEL\">" + questName + "</font><br>State: <font color=\"LEVEL\">" + state + "</font><br><br>");
				replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");
				if (qs != null)
				{
					for (Entry<String, String> entry : qs.getVars().entrySet())
					{
						final String varName = entry.getKey();
						String value = entry.getValue();
						switch (varName)
						{
							case "<state>":
							{
								continue;
							}
							case "cond":
							{
								final int cond = Integer.parseInt(value);
								value = "0b" + Integer.toBinaryString(cond) + "(" + ((32 - Integer.numberOfLeadingZeros(cond))) + ")";
								break;
							}
						}
						replyMSG.append("<tr><td>" + varName + "</td><td>" + value + "</td><td><edit var=\"var" + varName + "\" width=80 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questName + " " + varName + " $var" + varName + "\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Del\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questName + " " + varName + " delete\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
				}
				replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
				replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questName + " state COMPLETED 1\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questName + " state COMPLETED 0\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
				replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questName + " state DELETE\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				replyMSG.append("</center></body></html>");
				break;
			}
			case "var":
			{
				final byte state = Byte.parseByte(val[1]);
				replyMSG.append("Character: <font color=\"LEVEL\">" + target.getName() + "</font><br>Quests with state: <font color=\"LEVEL\">" + State.getStateName(state) + "</font><br>");
				replyMSG.append("<table width=250>");
				target.getAllQuestStates().stream().filter(qs -> qs.getState() == state).forEach(qs -> replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu " + target.getName() + " " + qs.getQuestName() + "\">" + qs.getQuestName() + "</a></td></tr>"));
				replyMSG.append("</table></body></html>");
				break;
			}
			case "custom":
			{
				final int questId = Integer.parseInt(val[1]);
				final Quest quest = QuestManager.getInstance().getQuest(questId);
				if (quest == null)
				{
					replyMSG.append("<center><font color=\"ee0000\">Quest with number </font><font color=\"LEVEL\">" + questId + "</font><font color=\"ee0000\"> doesn't exist!</font></center></body></html>");
					break;
				}

				final QuestState qs = target.getQuestState(quest.getName());

				replyMSG.append("<center><button value=\"Refresh\" action=\"bypass -h admin_charquestmenu " + target.getName() + " " + val[1] + "\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center>");
				if (qs != null)
				{
					replyMSG.append("Character: <font color=\"LEVEL\">" + target.getName() + "</font><br>Quest: <font color=\"LEVEL\">" + quest.getName() + "</font><br>State: <font color=\"LEVEL\">" + State.getStateName(qs.getState()) + "</font><br><br>");
					replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");
					for (Entry<String, String> entry : qs.getVars().entrySet())
					{
						final String varName = entry.getKey();
						String value = entry.getValue();
						switch (varName)
						{
							case "<state>":
							{
								continue;
							}
							case "cond":
							{
								final int cond = Integer.parseInt(value);
								value = "0b" + Integer.toBinaryString(cond) + "(" + ((32 - Integer.numberOfLeadingZeros(cond))) + ")";
								break;
							}
						}
						replyMSG.append("<tr><td>" + varName + "</td><td>" + value + "</td><td><edit var=\"var" + varName + "\" width=80 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + quest.getName() + " " + varName + " $var" + varName + "\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Del\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + quest.getName() + " " + varName + " delete\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
					replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
					replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + quest.getName() + " state COMPLETED 1\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
					replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + quest.getName() + " state COMPLETED 0\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + quest.getName() + " state DELETE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
					replyMSG.append("</center></body></html>");
				}
				else
				{
					replyMSG.append("Character: <font color=\"LEVEL\">" + target.getName() + "</font><br>Quest: <font color=\"LEVEL\">" + quest.getName() + "</font><br>State: <font color=\"LEVEL\">N/A</font><br><br>");
					replyMSG.append("<center>Start this Quest for player:<br>");
					replyMSG.append("<button value=\"Create Quest\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questId + " state CREATE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
					replyMSG.append("<font color=\"ee0000\">Only for Unrepeateble quests:</font><br>");
					replyMSG.append("<button value=\"Create & Complete\" action=\"bypass -h admin_setcharquest " + target.getName() + " " + questId + " state CC\" width=130 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
					replyMSG.append("</center></body></html>");
				}
				break;
			}
		}
		adminReply.setHtml(replyMSG.toString());
		actor.sendPacket(adminReply);
	}

	private static void setQuestVar(PlayerInstance target, PlayerInstance actor, String[] val)
	{
		QuestState qs = target.getQuestState(val[0]);
		String[] outval = new String[3];

		if (val[1].equals("state"))
		{
			switch (val[2])
			{
				case "COMPLETED":
				{
					qs.exitQuest((val[3].equals("1")) ? QuestType.REPEATABLE : QuestType.ONE_TIME);
					break;
				}
				case "DELETE":
				{
					qs.exitQuest(QuestType.REPEATABLE);
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
					break;
				}
				case "CREATE":
				{
					qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
					qs.startQuest();
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
					val[0] = qs.getQuest().getName();
					break;
				}
				case "CC":
				{
					qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
					qs.exitQuest(QuestType.ONE_TIME);
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
					val[0] = qs.getQuest().getName();
					break;
				}
			}
		}
		else
		{
			if (val[2].equals("delete"))
			{
				qs.unset(val[1]);
			}
			else
			{
				qs.set(val[1], val[2]);
			}
			target.sendPacket(new QuestList(target));
			target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
		}
		actor.sendMessage("");
		outval[0] = "name";
		outval[1] = val[0];
		showQuestMenu(target, actor, outval);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminQuest());
	}
}