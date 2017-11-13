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
package org.l2junity.gameserver.scripting;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.commons.util.XmlReaderException;
import org.l2junity.gameserver.PathProvider;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.l2junity.gameserver.handler.ActionHandler;
import org.l2junity.gameserver.handler.ActionShiftHandler;
import org.l2junity.gameserver.handler.AdminCommandHandler;
import org.l2junity.gameserver.handler.BypassHandler;
import org.l2junity.gameserver.handler.ChatHandler;
import org.l2junity.gameserver.handler.CommunityBoardHandler;
import org.l2junity.gameserver.handler.ItemHandler;
import org.l2junity.gameserver.handler.PunishmentHandler;
import org.l2junity.gameserver.handler.UserCommandHandler;
import org.l2junity.gameserver.handler.VoicedCommandHandler;
import org.l2junity.gameserver.instancemanager.QuestManager;
import org.l2junity.gameserver.loader.PreLoadGroup;
import org.l2junity.gameserver.loader.ScriptLoadGroup;
import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.w3c.dom.Document;

/**
 * @author lord_rex
 */
public final class ScriptsManager implements IGameXmlReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptsManager.class);
	
	private ClassLoader _scriptsClassLoader;
	
	private final Set<String> _excludedPackages = new HashSet<>();
	private final Set<String> _includedPackages = new HashSet<>();
	private final Set<String> _excludedClasses = new HashSet<>();
	private final Set<String> _includedClasses = new HashSet<>();
	
	protected ScriptsManager()
	{
	}
	
	@Load(group = PreLoadGroup.class)
	public void loadScriptsXml() throws IOException, XmlReaderException
	{
		parseDatapackFile("config/scripts.xml");
	}
	
	@Reload("scripts_jar")
	@Load(group = PreLoadGroup.class, dependencies = @Dependency(clazz = ScriptsManager.class, method = "loadScriptsXml"))
	public void loadJar()
	{
		try
		{
			_scriptsClassLoader = new URLClassLoader(new URL[]
			{
				new PathProvider().resolvePath(Paths.get("data/scripts.jar")).toUri().toURL()
			});
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void runScripts(Class<? extends Annotation> annotation) throws IOException, InvocationTargetException, IllegalAccessException
	{
		final ClassPath classPath = ClassPath.from(_scriptsClassLoader);
		for (ClassInfo classInfo : classPath.getTopLevelClassesRecursive("org.l2junity.scripts"))
		{
			if (!_includedClasses.contains(classInfo.getName()))
			{
				if (_excludedClasses.contains(classInfo.getName()))
				{
					continue;
				}
				
				if (_excludedPackages.stream().anyMatch(e -> classInfo.getPackageName().startsWith(e)) && !_includedPackages.stream().anyMatch(i -> classInfo.getPackageName().startsWith(i)))
				{
					continue;
				}
			}
			
			invokeAnnotatedMethod(classInfo.load(), annotation);
		}
	}
	
	public void runScript(String className, Class<? extends Annotation> annotation) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException
	{
		invokeAnnotatedMethod(Class.forName(className, true, _scriptsClassLoader), annotation);
	}
	
	private void invokeAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException
	{
		for (Method method : clazz.getDeclaredMethods())
		{
			if (!method.isAnnotationPresent(annotation))
			{
				continue;
			}
			
			if (!Modifier.isStatic(method.getModifiers()))
			{
				throw new RuntimeException();
			}
			
			if (method.getParameterCount() != 0)
			{
				throw new RuntimeException();
			}
			
			method.invoke(null);
		}
	}
	
	@Reload("scripts")
	@Load(group = ScriptLoadGroup.class)
	public void load() throws Exception
	{
		LOGGER.info("Loading server script(s) ...");
		
		ScriptsManager.getInstance().runScripts(GameScript.class);
		
		// report handlers
		ActionHandler.getInstance().report();
		ActionShiftHandler.getInstance().report();
		AdminCommandHandler.getInstance().report();
		BypassHandler.getInstance().report();
		ChatHandler.getInstance().report();
		CommunityBoardHandler.getInstance().report();
		ItemHandler.getInstance().report();
		PunishmentHandler.getInstance().report();
		UserCommandHandler.getInstance().report();
		VoicedCommandHandler.getInstance().report();
		
		// report loaded scripts
		QuestManager.getInstance().report();
		
		LOGGER.info("Server script(s) were loaded successfully .");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", n ->
		{
			forEach(n, "excludePackage", excludePackageNode -> _excludedPackages.add(excludePackageNode.getTextContent().endsWith(".") ? excludePackageNode.getTextContent() : excludePackageNode.getTextContent() + "."));
			forEach(n, "includePackage", includePackageNode -> _includedPackages.add(includePackageNode.getTextContent().endsWith(".") ? includePackageNode.getTextContent() : includePackageNode.getTextContent() + "."));
			forEach(n, "excludeClass", excludeClassesNode -> _excludedClasses.add(excludeClassesNode.getTextContent()));
			forEach(n, "includeClass", includeClassesNode -> _includedClasses.add(includeClassesNode.getTextContent()));
		});
		LOGGER.info("Loaded {} Excluded Packages {} Included Packages {} Excluded Classes {} Included Classes", _excludedPackages.size(), _includedPackages.size(), _excludedClasses.size(), _includedClasses.size());
	}
	
	@InstanceGetter
	public static ScriptsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final ScriptsManager INSTANCE = new ScriptsManager();
	}
}