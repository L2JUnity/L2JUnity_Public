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
package org.l2junity.gameserver.network.telnet;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.gameserver.config.TelnetConfig;
import org.l2junity.gameserver.loader.LoadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author UnAfraid
 */
public final class TelnetServer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TelnetServer.class);
	
	private final Map<String, ITelnetCommand> _commands = new LinkedHashMap<>();
	private final EventLoopGroup _workerGroup = new NioEventLoopGroup(1);
	
	protected TelnetServer()
	{
	}
	
	@Load(group = LoadGroup.class)
	public void load()
	{
		if (!TelnetConfig.TELNET_ENABLED)
		{
			LOGGER.info("Disabled.");
			return;
		}
		
		addHandler(new ITelnetCommand()
		{
			@Override
			public String getCommand()
			{
				return "help";
			}
			
			@Override
			public String getUsage()
			{
				return "help [command]";
			}
			
			@Override
			public String handle(String ipAddress, String[] args)
			{
				if (args.length == 0)
				{
					final StringBuilder sb = new StringBuilder("Available commands:" + System.lineSeparator());
					for (ITelnetCommand cmd : TelnetServer.getInstance().getCommands())
					{
						sb.append(cmd.getCommand() + System.lineSeparator());
					}
					return sb.toString();
				}
				final ITelnetCommand cmd = TelnetServer.getInstance().getCommand(args[0]);
				if (cmd == null)
				{
					return "Unknown command." + System.lineSeparator();
				}
				return "Usage:" + System.lineSeparator() + cmd.getUsage() + System.lineSeparator();
			}
		});
		
		try
		{
			final InetSocketAddress socket = TelnetConfig.TELNET_HOSTNAME.equals("*") ? new InetSocketAddress(TelnetConfig.TELNET_PORT) : new InetSocketAddress(TelnetConfig.TELNET_HOSTNAME, TelnetConfig.TELNET_PORT);
			//@formatter:off
			new ServerBootstrap().group(_workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(new TelnetServerInitializer())
				.bind(socket);
			//@formatter:on
			LOGGER.info("Listening on " + TelnetConfig.TELNET_HOSTNAME + ":" + TelnetConfig.TELNET_PORT);
		}
		catch (Exception e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
	}
	
	public void addHandler(ITelnetCommand handler)
	{
		_commands.put(handler.getCommand(), handler);
	}
	
	public ITelnetCommand getCommand(String command)
	{
		return _commands.get(command);
	}
	
	public Collection<ITelnetCommand> getCommands()
	{
		return _commands.values();
	}
	
	public void shutdown()
	{
		_workerGroup.shutdownGracefully();
		LOGGER.info("Shutting down..");
	}
	
	@InstanceGetter
	public static TelnetServer getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TelnetServer INSTANCE = new TelnetServer();
	}
}
