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
package org.l2junity.commons.util.jar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * A simple class to gather the manifest version information of JAR files.
 * @author lord_rex
 */
public final class VersionInfo
{
	/** A default string for the cases when version info cannot be retrieved. (IDE Mode) */
	private static final String IDE_MODE = "Version Info - IDE Mode.";
	
	private String _filename = null;
	private final Map<String, String> _manifestAttributes;
	
	/**
	 * Gather version information from the class.
	 * @param clazz
	 */
	public VersionInfo(final Class<?> clazz)
	{
		_manifestAttributes = new HashMap<>();
		
		final File file = Locator.getClassSource(clazz);
		if (!file.isFile())
		{
			return;
		}
		
		final String filename = file.getName();
		_filename = filename.substring(0, filename.lastIndexOf("."));
		
		try (final JarFile jarFile = new JarFile(file);)
		{
			final Attributes attributes = jarFile.getManifest().getMainAttributes();
			attributes.entrySet().forEach((entry) -> _manifestAttributes.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
		}
		catch (IOException e)
		{
			// ignore, IDE mode, etc...
		}
	}
	
	/**
	 * Gets a manifest from the manifest attribute map, shows {@link #IDE_MODE} if null.
	 * @param name
	 * @return manifest info
	 */
	public String getManifest(final String name)
	{
		return _manifestAttributes.getOrDefault(name, IDE_MODE);
	}
	
	/**
	 * Gets a pretty formatted class info.
	 * @return formatted class info
	 */
	public String getFormattedClassInfo()
	{
		if ((_filename == null) || _filename.isEmpty())
		{
			return IDE_MODE;
		}
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(_filename).append(": ");
		sb.append(getManifest(VersionInfoManifest.GIT_HASH_SHORT)).append(", ");
		sb.append(getManifest(VersionInfoManifest.GIT_COMMIT_COUNT)).append(", ");
		sb.append(getManifest(VersionInfoManifest.IMPLEMENTATION_TIME));
		sb.append(" [ ").append(getManifest(VersionInfoManifest.GIT_BRANCH)).append(" ]");
		
		return sb.toString();
	}
	
	/**
	 * Standard manifest attribute constants.
	 * @author lord_rex
	 */
	public interface VersionInfoManifest
	{
		String CREATED_BY = "Created-By";
		String BUILT_BY = "Built-By";
		String IMPLEMENTATION_URL = "Implementation-URL";
		String IMPLEMENTATION_TIME = "Implementation-Time";
		String GIT_BRANCH = "Git-Branch";
		String GIT_HASH_FULL = "Git-Hash-Full";
		String GIT_HASH_SHORT = "Git-Hash-Short";
		String GIT_COMMIT_COUNT = "Git-Commit-Count";
	}
	
	/**
	 * Gather version info of multiply classes. You can use it to gather information of Commons/Network/MMOCore/GeoDriver/ETC from LS/GS or any other application.
	 * @param classes
	 * @return string array of version info
	 */
	public static List<String> of(final Class<?>... classes)
	{
		List<String> versions = new ArrayList<>();
		for (final Class<?> clazz : classes)
		{
			final VersionInfo versionInfo = new VersionInfo(clazz);
			versions.add(versionInfo.getFormattedClassInfo());
		}
		
		return versions;
	}
}
