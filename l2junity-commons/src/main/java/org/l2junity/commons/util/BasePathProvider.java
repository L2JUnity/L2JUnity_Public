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
package org.l2junity.commons.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.l2junity.commons.util.AppInit.ApplicationMode;

/**
 * @author UnAfraid
 */
public final class BasePathProvider
{
	private static final Path BASE_PATH;
	static
	{
		final ApplicationMode appMode = AppInit.MODE;
		if (appMode == null)
		{
			throw new NullPointerException("Application Mode is missing! Your application is not initialized properly!");
		}
		
		final String appModeName = appMode.getModeName();
		final Path baseRepoPath = Paths.get("l2junity-gameserver", "dist", appModeName);
		final Path projectPath = Paths.get("dist", appModeName);
		final Path gamePath = Paths.get(".");
		BASE_PATH = Files.exists(baseRepoPath) ? baseRepoPath : Files.exists(projectPath) ? projectPath : gamePath;
	}
	
	private BasePathProvider()
	{
		// Visibility
	}
	
	public static Path getBasePath()
	{
		return BASE_PATH;
	}
	
	public static Path resolvePath(final String path)
	{
		return BASE_PATH.resolve(path).normalize();
	}
	
	public static Path resolvePath(final Path path)
	{
		return BASE_PATH.resolve(path).normalize();
	}
	
	public static Path resolveDatapackPath(final Path datapackRoot, final String path)
	{
		final Path resolvedPath = datapackRoot.resolve(path);
		final Path basePath = BASE_PATH.resolve(path);
		return Files.exists(basePath) ? basePath : Files.exists(resolvedPath) ? resolvedPath : Paths.get(path);
	}
	
	public static Path resolveDatapackPath(final Path datapackRoot, final Path path)
	{
		final Path resolvedPath = datapackRoot.resolve(path);
		final Path basePath = BASE_PATH.resolve(path);
		return Files.exists(basePath) ? basePath : Files.exists(resolvedPath) ? resolvedPath : path;
	}
	
	public static Map<String, Path> resolveFiles(Path dir, boolean recursive) throws IOException
	{
		final Map<String, Path> files = new LinkedHashMap<>();
		final Path path = resolvePath(dir);
		if (Files.exists(path))
		{
			Files.walk(path, recursive ? Integer.MAX_VALUE : 1).forEach(file -> files.put(file.toAbsolutePath().normalize().toString(), file));
		}
		return files;
	}
	
	public static Map<String, Path> resolveDatapackFiles(final Path datapackRoot, Path dir, boolean recursive) throws IOException
	{
		final Map<String, Path> files = resolveFiles(dir, recursive);
		
		final Path datapackPath = datapackRoot.resolve(dir);
		if (Files.exists(datapackPath) && !Files.isSameFile(resolvePath(dir.normalize()).toAbsolutePath(), datapackPath.toAbsolutePath()))
		{
			Files.walk(datapackPath, recursive ? Integer.MAX_VALUE : 1).forEach(file -> files.put(file.toAbsolutePath().normalize().toString(), file));
		}
		return files;
	}
}
