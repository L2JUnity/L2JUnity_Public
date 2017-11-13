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
package org.l2junity.loginserver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.IXmlReader;
import org.l2junity.commons.util.XmlReaderException;
import org.l2junity.loginserver.config.LoginServerConfig;

/**
 * @author lord_rex
 */
public interface ILoginXmlReader extends IXmlReader
{
	/**
	 * Wrapper for {@link #parseFile(Path)} method.
	 * @param path the relative path to the datapack root of the XML file to parse.
	 * @throws IOException
	 * @throws XmlReaderException
	 */
	default void parseDatapackFile(final String path) throws IOException, XmlReaderException
	{
		parseFile(BasePathProvider.resolveDatapackPath(LoginServerConfig.DATAPACK_ROOT, path));
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(Path, boolean)}.
	 * @param file the path to the directory where the XML files are.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 * @throws IOException
	 */
	@Override
	default boolean parseDirectory(final Path file) throws IOException
	{
		return parseDirectory(file, false);
	}
	
	/**
	 * Loads all XML files from {@code path} and calls {@link #parseFile(Path)} for each one of them.
	 * @param dir the directory object to scan.
	 * @param recursive parses all sub folders if there is.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 * @throws IOException
	 */
	@Override
	default boolean parseDirectory(final Path dir, final boolean recursive) throws IOException
	{
		final Map<String, Path> files = BasePathProvider.resolveDatapackFiles(LoginServerConfig.DATAPACK_ROOT, dir, recursive);
		files.values().forEach(file ->
		{
			try
			{
				parseFile(file);
			}
			catch (final IOException | XmlReaderException e)
			{
				LOGGER.warn("Failed to load file: '" + file + "'", e);
			}
		});
		return true;
	}
	
	/**
	 * Wrapper for {@link #parseDirectory(Path, boolean)}.
	 * @param path the path to the directory where the XML files are
	 * @param recursive parses all sub folders if there is
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise
	 * @throws IOException
	 */
	default boolean parseDatapackDirectory(final String path, final boolean recursive) throws IOException
	{
		return parseDirectory(Paths.get(path), recursive);
	}
	
}
