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

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.SystemUtil;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.IAdminCommandHandler;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdminSystem implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminSystem.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_system_time",
		"admin_os_info",
		"admin_cpu_info",
		"admin_runtime_info",
		"admin_jre_info",
		"admin_jvm_info",
		"admin_memory_usage",
		"admin_view_threads",
		"admin_dump_threads",
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance player)
	{
		switch (command.toLowerCase())
		{
			case "admin_system_time":
				for (final String line : SystemUtil.getSystemTime())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_os_info":
				for (final String line : SystemUtil.getOSInfo())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_cpu_info":
				for (final String line : SystemUtil.getCPUInfo())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_runtime_info":
				for (final String line : SystemUtil.getRuntimeInfo())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_jre_info":
				for (final String line : SystemUtil.getJREInfo())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_jvm_info":
				for (final String line : SystemUtil.getJVMInfo())
				{
					player.sendMessage(line);
				}
				break;
			
			case "admin_memory_usage":
				for (final String line : SystemUtil.getMemoryUsageStatistics())
				{
					player.sendMessage(line);
				}
				break;
			case "admin_dump_threads":
				try
				{
					//@formatter:off
					final Path path = Files.createDirectories(BasePathProvider.resolvePath(Paths.get("log", "thread-dumps")))
							.resolve(LocalDateTime.now().format(CommonUtil.getFilenameDateTimeFormatter()) + ".txt");
					final List<String> threadDump = Arrays.stream(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true))
							.map(Object::toString)
							.collect(Collectors.toList());
					//@formatter:on
					Files.write(path, threadDump, StandardCharsets.UTF_8);
					LOGGER.info("Thread Dump successfully saved to {}!", path);
					player.sendMessage("Thread Dump done. Please see 'log/thread-dumps' directory.");
				}
				catch (Exception e)
				{
					LOGGER.warn("", e);
					player.sendMessage("Thread Dump failed. Please check logs for more information.");
				}
				break;
		}
		
		if (command.startsWith("admin_view_threads"))
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("<html><title>Thread Viewer: ").append("</title><body>");
			ThreadPool.getStats().forEach(line -> sb.append(line).append("<br1>"));
			sb.append("</body></html>");
			player.sendPacket(new NpcHtmlMessage(sb.toString()));
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	@GameScript
	public static void main()
	{
		AdminCommandHandler.getInstance().registerHandler(new AdminSystem());
	}
}