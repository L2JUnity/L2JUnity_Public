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
package org.l2junity.commons.loader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.l2junity.commons.loader.annotations.Dependency;
import org.l2junity.commons.loader.annotations.InstanceGetter;
import org.l2junity.commons.loader.annotations.Load;
import org.l2junity.commons.loader.annotations.Reload;
import org.l2junity.commons.util.ClassPathUtil;
import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.TreeNode;
import org.l2junity.commons.util.concurrent.ThreadPool;

/**
 * @author NosKun
 */
public final class Loader
{
	private final Map<Class<? extends ILoadGroup>, List<TreeNode<LoadHolder>>> _loadTreesGroups = new HashMap<>();
	private final Map<String, LoadHolder> _reloads = new HashMap<>();
	private final Map<LoadHolder, Long> _executionTimes = new ConcurrentHashMap<>();
	
	public Loader(String prefix)
	{
		try
		{
			for (Method loadMethod : ClassPathUtil.getAllMethodsAnnotatedWith(prefix, Load.class))
			{
				final TreeNode<LoadHolder> loadTreeNode = new TreeNode<>(new LoadHolder(findInstanceGetterMethod(loadMethod.getDeclaringClass()), loadMethod));
				final Load loadAnnotation = loadMethod.getAnnotation(Load.class);
				for (Dependency dependency : loadAnnotation.dependencies())
				{
					for (String dependencyMethod : dependency.method())
					{
						try
						{
							final LoadHolder dependencyLoadHolder = new LoadHolder(findInstanceGetterMethod(dependency.clazz()), dependency.clazz().getDeclaredMethod(dependencyMethod));
							final Load dependencyLoadAnnotation = dependencyLoadHolder.getLoadMethod().getAnnotation(Load.class);
							if (dependencyLoadAnnotation == null)
							{
								throw new RuntimeException("Dependency " + dependencyLoadHolder + " of " + loadTreeNode.getValue() + " is not annotated with Load annotation");
							}
							
							if (loadAnnotation.group() != dependencyLoadAnnotation.group())
							{
								throw new RuntimeException("Dependency " + dependencyLoadHolder + " of " + loadTreeNode.getValue() + " can not be in different Load group");
							}
							
							loadTreeNode.addChild(dependencyLoadHolder);
							
							if (loadTreeNode.getChildren().stream().distinct().count() != loadTreeNode.getChildren().size())
							{
								throw new RuntimeException("Duplicated Dependency " + dependencyLoadHolder + " on " + loadTreeNode.getValue());
							}
						}
						catch (NoSuchMethodException e)
						{
							throw new RuntimeException("Dependency method for " + dependency.clazz().getName() + "." + dependencyMethod + "()" + " on " + loadTreeNode.getValue() + " was not found.", e);
						}
					}
				}
				_loadTreesGroups.computeIfAbsent(loadAnnotation.group(), k -> new LinkedList<>()).add(loadTreeNode);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		_loadTreesGroups.values().forEach(loadTrees -> loadTrees.removeIf(loadTreeNode1 ->
		{
			boolean wasAdopted = false;
			for (TreeNode<LoadHolder> loadTreeNode2 : loadTrees)
			{
				if (loadTreeNode1 != loadTreeNode2)
				{
					final List<TreeNode<LoadHolder>> foundLoadTreeNodes = loadTreeNode2.findAll(loadTreeNode1.getValue());
					for (TreeNode<LoadHolder> foundLoadTreeNode : foundLoadTreeNodes)
					{
						foundLoadTreeNode.addChildren(loadTreeNode1.getChildren());
					}
					if (!foundLoadTreeNodes.isEmpty())
					{
						wasAdopted = true;
					}
				}
			}
			return wasAdopted;
		}));
		
		for (List<TreeNode<LoadHolder>> loadTrees : _loadTreesGroups.values())
		{
			for (TreeNode<LoadHolder> loadTree : loadTrees)
			{
				final Queue<Deque<TreeNode<LoadHolder>>> frontier = new LinkedList<>();
				
				{
					final Deque<TreeNode<LoadHolder>> deque = new LinkedList<>();
					deque.add(loadTree);
					frontier.offer(deque);
				}
				
				while (!frontier.isEmpty())
				{
					final Deque<TreeNode<LoadHolder>> front = frontier.poll();
					for (TreeNode<LoadHolder> loadTreeNode : front.getLast().getChildren())
					{
						if (front.contains(loadTreeNode))
						{
							front.add(loadTreeNode);
							while (!front.getFirst().getValue().equals(front.getLast().getValue()))
							{
								front.removeFirst();
							}
							throw new RuntimeException("Cyclic Dependency found [" + front.stream().map(t -> t.getValue().toString()).collect(Collectors.joining(" -> ")) + "]");
						}
						final Deque<TreeNode<LoadHolder>> deque = new LinkedList<>();
						deque.addAll(front);
						deque.add(loadTreeNode);
						frontier.offer(deque);
					}
				}
			}
		}
		
		try
		{
			for (Method reloadMethod : ClassPathUtil.getAllMethodsAnnotatedWith(prefix, Reload.class))
			{
				final String reloadName = reloadMethod.getAnnotation(Reload.class).value();
				final LoadHolder loadHolder = new LoadHolder(findInstanceGetterMethod(reloadMethod.getDeclaringClass()), reloadMethod);
				final LoadHolder previousLoadHolder = _reloads.putIfAbsent(reloadName, loadHolder);
				if (previousLoadHolder != null)
				{
					throw new RuntimeException("More than one Reload with name " + reloadName + " found [" + previousLoadHolder + ", " + loadHolder + "]");
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@SafeVarargs
	public final CompletableFuture<Void> runAsync(Class<? extends ILoadGroup> loadGroup, Class<? extends ILoadGroup>... loadGroups)
	{
		final Deque<CompletableFuture<Void>> loadGroupCompletableFutures = new LinkedList<>();
		CommonUtil.forEachArgAndVarArgs(loadGroup2 ->
		{
			final CompletableFuture<Void> previousLoadGroupCompletableFuture = loadGroupCompletableFutures.peekLast();
			final Map<LoadHolder, CompletableFuture<Void>> completableFutures = new HashMap<>();
			final List<CompletableFuture<Void>> rootCompletableFutures = new LinkedList<>();
			for (TreeNode<LoadHolder> loadTreeNode : _loadTreesGroups.getOrDefault(loadGroup2, Collections.emptyList()))
			{
				CompletableFuture<Void> lastCompletableFuture = null;
				for (TreeNode<LoadHolder> treeNode : loadTreeNode.postOrderTraversal())
				{
					lastCompletableFuture = completableFutures.computeIfAbsent(treeNode.getValue(), k ->
					{
						//@formatter:off
						Stream<CompletableFuture<Void>> dependencyCompletableFutureStream = treeNode.getChildren()
								.stream()
								.map(TreeNode::getValue)
								.map(completableFutures::get);
						if (previousLoadGroupCompletableFuture != null)
						{
							dependencyCompletableFutureStream = Stream.concat(Stream.of(previousLoadGroupCompletableFuture), dependencyCompletableFutureStream);
						}
						return CompletableFuture.allOf(dependencyCompletableFutureStream.toArray(CompletableFuture[]::new))
								.thenRunAsync(() -> runTreeNode(treeNode), ThreadPool.getThreadPoolExecutor());
						//@formatter:on
					});
				}
				rootCompletableFutures.add(lastCompletableFuture);
			}
			loadGroupCompletableFutures.add(CompletableFuture.allOf(rootCompletableFutures.stream().toArray(CompletableFuture[]::new)));
		}, loadGroup, loadGroups);
		return CompletableFuture.allOf(loadGroupCompletableFutures.stream().toArray(CompletableFuture[]::new));
	}
	
	@SafeVarargs
	public final void run(Class<? extends ILoadGroup> loadGroup, Class<? extends ILoadGroup>... loadGroups)
	{
		CommonUtil.forEachArgAndVarArgs(loadGroup2 ->
		{
			final Set<LoadHolder> runNodes = new HashSet<>();
			for (TreeNode<LoadHolder> loadTreeNode : _loadTreesGroups.getOrDefault(loadGroup2, Collections.emptyList()))
			{
				for (TreeNode<LoadHolder> treeNode : loadTreeNode.postOrderTraversal())
				{
					if (runNodes.add(treeNode.getValue()))
					{
						runTreeNode(treeNode);
					}
				}
			}
		}, loadGroup, loadGroups);
	}
	
	private void runTreeNode(TreeNode<LoadHolder> treeNode) throws RuntimeException
	{
		try
		{
			final long startTime = System.nanoTime();
			treeNode.getValue().call();
			_executionTimes.put(treeNode.getValue(), treeNode.getChildren().stream().map(TreeNode::getValue).mapToLong(_executionTimes::get).max().orElse(0) + (System.nanoTime() - startTime));
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException("Calling " + treeNode.getValue() + " failed", e);
		}
	}
	
	@SafeVarargs
	public final String getDependencyTreeString(Class<? extends ILoadGroup> loadGroup, Class<? extends ILoadGroup>... loadGroups)
	{
		final StringJoiner sj = new StringJoiner(System.lineSeparator());
		CommonUtil.forEachArgAndVarArgs(loadGroups2 ->
		{
			sj.add("=========================" + loadGroups2.getName() + "=========================");
			for (Iterator<TreeNode<LoadHolder>> loadTreesIterator = _loadTreesGroups.getOrDefault(loadGroups2, Collections.emptyList()).stream().sorted(Comparator.comparingLong(tn -> _executionTimes.getOrDefault(tn.getValue(), 0L))).iterator(); loadTreesIterator.hasNext();)
			{
				getDependencyTreeString(sj, loadTreesIterator.next(), "", !loadTreesIterator.hasNext());
			}
		}, loadGroup, loadGroups);
		return sj.toString();
	}
	
	private void getDependencyTreeString(StringJoiner sj, TreeNode<LoadHolder> treeNode, String indent, boolean lastChild)
	{
		sj.add(indent + (lastChild ? "\\" : "+") + "--- " + treeNode.getValue() + " " + TimeUnit.NANOSECONDS.toMillis(_executionTimes.getOrDefault(treeNode.getValue(), -1L)) + " ms");
		for (Iterator<TreeNode<LoadHolder>> childrenIterator = treeNode.getChildren().stream().sorted(Comparator.<TreeNode<LoadHolder>> comparingLong(tn -> _executionTimes.getOrDefault(tn.getValue(), 0L)).reversed()).iterator(); childrenIterator.hasNext();)
		{
			getDependencyTreeString(sj, childrenIterator.next(), indent + (lastChild ? " " : "|") + "    ", !childrenIterator.hasNext());
		}
	}
	
	@SafeVarargs
	public final void writeDependencyTreeToFile(Path path, Class<? extends ILoadGroup> loadGroup, Class<? extends ILoadGroup>... loadGroups) throws IOException
	{
		Files.write(path, getDependencyTreeString(loadGroup, loadGroups).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public Map<String, LoadHolder> getReloads()
	{
		return _reloads;
	}
	
	private static Method findInstanceGetterMethod(Class<?> clazz)
	{
		final Method[] instanceGetterMethods = Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(InstanceGetter.class)).toArray(Method[]::new);
		if (instanceGetterMethods.length == 0)
		{
			throw new UnsupportedOperationException(clazz + " contains Load annotated method(s) but it does not have an InstanceGetter annotated method.");
		}
		
		if (instanceGetterMethods.length != 1)
		{
			throw new UnsupportedOperationException("There should be only one InstanceGetter annotated method " + Arrays.toString(instanceGetterMethods));
		}
		
		if (!Modifier.isPublic(instanceGetterMethods[0].getModifiers()))
		{
			throw new UnsupportedOperationException("non public InstanceGetter method " + instanceGetterMethods[0]);
		}
		
		if (!Modifier.isStatic(instanceGetterMethods[0].getModifiers()))
		{
			throw new UnsupportedOperationException("non static InstanceGetter method " + instanceGetterMethods[0]);
		}
		return instanceGetterMethods[0];
	}
}
