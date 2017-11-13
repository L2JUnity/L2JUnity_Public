/*
 * Copyright (C) 2004-2016 L2J Unity
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
package org.l2junity.gameserver.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.l2junity.commons.config.IConfigLoader;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lord_rex
 */
public final class ChatFilterConfig implements IConfigLoader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(ChatFilterConfig.class);
	
	public static final String CHAT_FILTER_FILE = "./config/chatfilter.txt";
	
	// chatfilter
	public static List<String> FILTER_LIST;
	
	public ChatFilterConfig()
	{
		// visibility
	}
	
	@Override
	public void load(PropertiesParser override)
	{
		try
		{
			//@formatter:off
			ChatFilterConfig.FILTER_LIST = Files.lines(BasePathProvider.resolvePath(ChatFilterConfig.CHAT_FILTER_FILE), StandardCharsets.UTF_8)
				.map(String::trim)
				.filter(line -> (!line.isEmpty() && (line.charAt(0) != '#')))
				.collect(Collectors.toList());
			//@formatter:on
			LOGGER.info("Loaded " + ChatFilterConfig.FILTER_LIST.size() + " Filter Words.");
		}
		catch (IOException e)
		{
			LOGGER.warn("Error while loading chat filter words!", e);
		}
	}
}
