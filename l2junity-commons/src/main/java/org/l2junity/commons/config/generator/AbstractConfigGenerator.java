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
package org.l2junity.commons.config.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.l2junity.commons.config.IConfigLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;
import org.l2junity.commons.config.annotation.ConfigGroupBeginning;
import org.l2junity.commons.config.annotation.ConfigGroupEnding;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.ClassPathUtil;
import org.l2junity.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NB4L1 (L2JFree concept)
 * @author _dev_ (L2Emu concept)
 * @author lord_rex (l2junity concept)
 */
public abstract class AbstractConfigGenerator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigGenerator.class);
	
	public AbstractConfigGenerator()
	{
		try
		{
			ClassPathUtil.getAllClassesExtending(getPackageName(), IConfigLoader.class).forEach(clazz ->
			{
				try
				{
					printConfigClass(clazz);
				}
				catch (IOException e)
				{
					LOGGER.warn("Failed to generate config.", e);
				}
			});
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to scan for configs", e);
		}
	}
	
	protected abstract String getPackageName();
	
	public static void printConfigClass(final Class<?> clazz) throws IOException
	{
		final StringBuilder out = new StringBuilder();
		
		final ConfigClass configClass = clazz.getAnnotation(ConfigClass.class);
		if (configClass == null)
		{
			return;
		}
		
		final String settings = StringUtil.capitalizeFirst(configClass.fileName().replace("_", " ")) + " Settings";
		
		// Header.
		out.append("################################################################################\r\n");
		out.append("## Copyright (C) 2004-2017 L2J Unity - " + settings).append(System.lineSeparator());
		out.append("################################################################################\r\n");
		
		out.append(System.lineSeparator()); // separator
		
		// File comment if exists.
		if ((configClass.comment() != null) && (configClass.comment().length > 0))
		{
			for (final String line : configClass.comment())
			{
				out.append("# ").append(line).append(System.lineSeparator());
			}
			out.append(System.lineSeparator());
		}
		
		for (Field field : clazz.getFields())
		{
			printConfigField(out, field);
		}
		
		final Path configPath = BasePathProvider.resolvePath(Paths.get("", configClass.pathNames()).resolve(configClass.fileName() + configClass.fileExtension()));
		Files.createDirectories(configPath.getParent());
		
		try (final BufferedWriter bw = Files.newBufferedWriter(configPath))
		{
			bw.append(out.toString());
		}
		
		LOGGER.info("Generated: '{}'", configPath);
	}
	
	private static void printConfigField(final StringBuilder out, final Field field)
	{
		final ConfigField configField = field.getAnnotation(ConfigField.class);
		
		if (configField == null)
		{
			return;
		}
		
		final ConfigGroupBeginning beginningGroup = field.getAnnotation(ConfigGroupBeginning.class);
		if (beginningGroup != null)
		{
			out.append("########################################").append(System.lineSeparator());
			out.append("## Section BEGIN: ").append(beginningGroup.name()).append(System.lineSeparator());
			
			for (final String line : beginningGroup.comment())
			{
				out.append("# ").append(line).append(System.lineSeparator());
			}
			
			out.append(System.lineSeparator());
		}
		
		for (final String line : configField.comment())
		{
			out.append("# ").append(line).append(System.lineSeparator());
		}
		
		if (!configField.onlyComment())
		{
			out.append("# Default: ").append(configField.value()).append(System.lineSeparator());
			if (field.getType().isEnum())
			{
				out.append("# Available: ").append(Arrays.stream(field.getType().getEnumConstants()).map(String::valueOf).collect(Collectors.joining("|"))).append(System.lineSeparator());
			}
			else if (field.getType().isArray())
			{
				final Class<?> fieldComponentType = field.getType().getComponentType();
				if (fieldComponentType.isEnum())
				{
					out.append("# Available: ").append(Arrays.stream(field.getType().getEnumConstants()).map(String::valueOf).collect(Collectors.joining(","))).append(System.lineSeparator());
				}
			}
			out.append(configField.name()).append(" = ").append(configField.value()).append(System.lineSeparator());
			out.append(System.lineSeparator());
		}
		
		final ConfigGroupEnding endingGroup = field.getAnnotation(ConfigGroupEnding.class);
		if (endingGroup != null)
		{
			for (final String line : endingGroup.comment())
			{
				out.append("# ").append(line).append(System.lineSeparator());
			}
			
			out.append("## Section END: ").append(endingGroup.name()).append(System.lineSeparator());
			out.append("########################################").append(System.lineSeparator());
			
			out.append(System.lineSeparator());
		}
	}
}
