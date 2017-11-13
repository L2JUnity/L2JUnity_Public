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
package org.l2junity.commons.lang.management;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.l2junity.commons.sql.DatabaseFactory;
import org.l2junity.commons.util.CommonUtil;
import org.l2junity.commons.util.concurrent.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the functions for shutting down and restarting the server.
 * @author NB4L1
 */
public final class ShutdownManager
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(ShutdownManager.class);
	
	// indicates shutdown in progress (hooks being run)
	private static final AtomicBoolean IN_PROGRESS = new AtomicBoolean(false);
	
	protected static TerminationStatus MODE = TerminationStatus.INVALID;
	
	// indicates that there is a shutdown scheduled in the future
	protected static ShutdownCounter SHUTDOWN_COUNTER;
	
	protected static final Set<ShutdownCounterListener> SHUTDOWN_COUNTER_LISTENERS = ConcurrentHashMap.newKeySet();
	private static final Set<ShutdownAbortListener> SHUTDOWN_ABORT_LISTENERS = ConcurrentHashMap.newKeySet();
	
	private ShutdownManager()
	{
		// utility class
	}
	
	/**
	 * A simple interface to extend shutdown counting with new actions.
	 * @author lord_rex
	 */
	public interface ShutdownCounterListener
	{
		public void onShutdownCount(ShutdownCounter shutdownCounter, int i);
	}
	
	/**
	 * A simple interface to extend shutdown aborting with new actions.
	 * @author lord_rex
	 */
	public interface ShutdownAbortListener
	{
		public void onShutdownAbort(TerminationStatus mode, String initiator);
	}
	
	/**
	 * Add listener to shutdown counting.
	 * @param listener listening to actions during shutdown
	 */
	public static void addShutdownCounterListener(ShutdownCounterListener listener)
	{
		SHUTDOWN_COUNTER_LISTENERS.add(listener);
	}
	
	/**
	 * Add listener to shutdown aborting.
	 * @param listener listening to actions when shutdown is aborted
	 */
	public static void addShutdownAbortListener(ShutdownAbortListener listener)
	{
		SHUTDOWN_ABORT_LISTENERS.add(listener);
	}
	
	/**
	 * Returns to the shutdown counter.
	 * @return the shutdown counter
	 */
	public static ShutdownCounter getCounter()
	{
		return SHUTDOWN_COUNTER;
	}
	
	/**
	 * Returns whether this application has been requested to end execution.
	 * @return whether a shutdown was ordered
	 */
	public static boolean isInProgress()
	{
		return SHUTDOWN_COUNTER != null;
	}
	
	/**
	 * Requests this application to end execution after the specified number of seconds, on behalf of <TT>initiator</TT>. <BR>
	 * <BR>
	 * If there is a pending request, it will be overridden.
	 * @param mode request type
	 * @param duration shutdown delay
	 * @param interval shutdown listener interval
	 * @param initiator requestor (may be <TT>null</TT>)
	 */
	public static synchronized void start(TerminationStatus mode, int duration, int interval, String initiator)
	{
		if (isInProgress())
		{
			abort(initiator);
		}
		
		if (initiator != null)
		{
			LOGGER.info(initiator + " issued a shutdown command: " + mode.getDescription() + " in " + duration + " seconds!");
		}
		else
		{
			LOGGER.info("A shutdown command was issued: " + mode.getDescription() + " in " + duration + " seconds!");
		}
		
		MODE = mode;
		
		SHUTDOWN_COUNTER = new ShutdownCounter(duration, interval, MODE);
		SHUTDOWN_COUNTER.start();
	}
	
	/**
	 * Cancels a requested shutdown on behalf of <TT>initiator</TT>.
	 * @param initiator canceler (may be <TT>null</TT>)
	 */
	public static synchronized void abort(String initiator)
	{
		if (!isInProgress())
		{
			return;
		}
		
		if (initiator != null)
		{
			LOGGER.info(initiator + " issued an abort abort: " + MODE.getDescription() + " has been stopped!");
		}
		else
		{
			LOGGER.info("An abort command was issued: " + MODE.getDescription() + " has been stopped!");
		}
		
		for (final ShutdownAbortListener listener : SHUTDOWN_ABORT_LISTENERS)
		{
			listener.onShutdownAbort(MODE, initiator);
		}
		
		MODE = TerminationStatus.INVALID;
		
		SHUTDOWN_COUNTER = null;
	}
	
	public static final class ShutdownCounter extends Thread
	{
		private final int _duration;
		private final int _interval;
		private final TerminationStatus _mode;
		private boolean _fastMode;
		
		protected ShutdownCounter(int duration, int interval, TerminationStatus mode)
		{
			_duration = duration;
			_interval = interval;
			_mode = mode;
		}
		
		@Override
		public void run()
		{
			for (int i = _duration; i >= _interval; i -= _interval)
			{
				// shutdown aborted
				if (this != SHUTDOWN_COUNTER)
				{
					return;
				}
				
				try
				{
					LOGGER.info("Server is " + _mode.getShortDescription() + "ing in " + i + " second(s).");
					
					for (final ShutdownCounterListener listener : ShutdownManager.SHUTDOWN_COUNTER_LISTENERS)
					{
						listener.onShutdownCount(this, i);
					}
					
					if (_fastMode)
					{
						LOGGER.info("Counting was skipped, using fast mode.");
						break; // fast mode.
					}
					
					if (i > _interval)
					{
						Thread.sleep(_interval * 1000);
					}
					else
					{
						Thread.sleep(i * 1000);
					}
				}
				catch (final InterruptedException e)
				{
					return;
				}
			}
			
			// shutdown aborted
			if (this != SHUTDOWN_COUNTER)
			{
				return;
			}
			
			// last point where logging is operational :(
			LOGGER.info("Shutdown countdown is over: " + _mode.getDescription() + " NOW!");
			
			Runtime.getRuntime().exit(_mode.getStatusCode());
		}
		
		public int getDuration()
		{
			return _duration;
		}
		
		public int getInterval()
		{
			return _interval;
		}
		
		public TerminationStatus getMode()
		{
			return _mode;
		}
		
		public void setFastMode(boolean fastMode)
		{
			_fastMode = fastMode;
		}
		
		public boolean isFastMode()
		{
			return _fastMode;
		}
	}
	
	/**
	 * Issues an orderly shutdown.
	 * @param initiator initiator's ID/name
	 */
	public static void shutdown(String initiator)
	{
		exit(TerminationStatus.MANUAL_SHUTDOWN, initiator);
	}
	
	/**
	 * Issues an orderly shutdown and requests a restart.
	 * @param initiator initiator's ID/name
	 */
	public static void restart(String initiator)
	{
		exit(TerminationStatus.MANUAL_RESTART, initiator);
	}
	
	/**
	 * Exits the server.
	 * @param mode indicates the cause and/or effect of the action
	 */
	public static void exit(TerminationStatus mode)
	{
		exit(mode, null);
	}
	
	/**
	 * Exits the server.
	 * @param mode indicates the cause and/or effect of the action
	 * @param initiator initiator's ID/name
	 */
	public static void exit(TerminationStatus mode, String initiator)
	{
		if (IN_PROGRESS.getAndSet(true))
		{
			return;
		}
		
		try
		{
			MODE = mode;
			
			if (initiator != null)
			{
				LOGGER.info(initiator + " issued a shutdown command: " + mode.getDescription() + "!");
			}
			else
			{
				LOGGER.info("A shutdown command was issued: " + mode.getDescription() + "!");
			}
		}
		finally
		{
			runSpecialHooks();
			Runtime.getRuntime().exit(mode.getStatusCode());
		}
	}
	
	/**
	 * Issues a forced shutdown and requests a restart.
	 * @param initiator initiator's ID/name
	 */
	public static void halt(String initiator)
	{
		halt(TerminationStatus.MANUAL_RESTART, initiator);
	}
	
	/**
	 * Halts the server.
	 * @param mode indicates the cause and/or effect of the action
	 */
	public static void halt(TerminationStatus mode)
	{
		halt(mode, null);
	}
	
	/**
	 * Halts the server.
	 * @param mode indicates the cause and/or effect of the action
	 * @param initiator initiator's ID/name
	 */
	public static void halt(TerminationStatus mode, String initiator)
	{
		try
		{
			MODE = mode;
			
			if (initiator != null)
			{
				LOGGER.info(initiator + " issued a halt command: " + mode.getDescription() + "!");
			}
			else
			{
				LOGGER.info("A halt command was issued: " + mode.getDescription() + "!");
			}
			
			// L2LogManager.flush();
		}
		finally
		{
			Runtime.getRuntime().halt(mode.getStatusCode());
		}
	}
	
	/**
	 * Adds a global shutdown hook to run all registered shutdown hooks sequentially in order they were registered.
	 */
	public static void initShutdownHook()
	{
		LOGGER.info("Initialized.");
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			CommonUtil.printSection("Shutdown Hook(s)");
			try
			{
				runShutdownHooks();
			}
			finally
			{
				final int exitCode = MODE.getStatusCode();
				if (System.getProperty("org.l2junity/write_exit_code") != null)
				{
					try (final Writer w = Files.newBufferedWriter(Paths.get("exit_code")))
					{
						w.write(String.valueOf(exitCode));
					}
					catch (final IOException e)
					{
						// ignore, no logging at this stage anymore
					}
				}
			}
		}, "CumulativeShutdownHook"));
	}
	
	private static final PriorityQueue<ShutdownHook> SHUTDOWN_HOOKS = new PriorityQueue<>(), SPECIAL_HOOKS = new PriorityQueue<>();
	
	/**
	 * Adds a managed shutdown hook to be run before the application terminates unless an uncaught termination signal is used.
	 * @param hook shutdown hook
	 */
	public static synchronized void addShutdownHook(Runnable hook)
	{
		addShutdownHook(0, hook);
	}
	
	public static synchronized void addShutdownHook(int priority, Runnable hook)
	{
		SHUTDOWN_HOOKS.add(new ShutdownHook(priority, hook));
	}
	
	public static synchronized void addSpecialHook(int priority, Runnable hook)
	{
		SPECIAL_HOOKS.add(new ShutdownHook(priority, hook));
	}
	
	public static boolean isRunningHooks()
	{
		return IN_PROGRESS.get();
	}
	
	protected static synchronized void runShutdownHooks()
	{
		while (!SHUTDOWN_HOOKS.isEmpty())
		{
			try
			{
				SHUTDOWN_HOOKS.remove().run();
			}
			catch (final Throwable t)
			{
				LOGGER.warn("", t);
			}
		}
		
		DatabaseFactory.shutdown();
		ThreadPool.shutdown();
	}
	
	protected static synchronized void runSpecialHooks()
	{
		while (!SPECIAL_HOOKS.isEmpty())
		{
			try
			{
				SPECIAL_HOOKS.remove().run();
			}
			catch (final Throwable t)
			{
				LOGGER.warn("", t);
			}
		}
	}
	
	private static final class ShutdownHook implements Comparable<ShutdownHook>, Runnable
	{
		private final int _priority;
		private final Runnable _hook;
		
		protected ShutdownHook(int priority, Runnable hook)
		{
			_priority = priority;
			_hook = hook;
		}
		
		@Override
		public void run()
		{
			_hook.run();
		}
		
		@Override
		public int compareTo(ShutdownHook o)
		{
			return _priority - o._priority;
		}
		
		@Override
		public String toString()
		{
			return "[" + _priority + "] " + _hook;
		}
	}
}
