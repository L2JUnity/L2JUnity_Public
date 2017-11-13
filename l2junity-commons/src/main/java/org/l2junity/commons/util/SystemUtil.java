/*
 * Copyright (C) 2004-2017 L2J Unity
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

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NB4L1, Savormix & UnAfraid (memory statistics)
 * @author Charus & Rayan RPG (the OS, CPU, JRE utilities)
 * @author lord_rex (re-architecture)
 */
public final class SystemUtil
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtil.class);
	
	private SystemUtil()
	{
		// utility class
	}
	
	/**
	 * The how many processors are installed on this system.
	 * @return statistics
	 */
	public static final String[] getCPUInfo()
	{
		return new String[]
		{
			"Avaible CPU(s): " + Runtime.getRuntime().availableProcessors(),
			"Processor(s) Identifier: " + System.getenv("PROCESSOR_IDENTIFIER"),
			"..................................................",
			".................................................."
		};
	}
	
	/**
	 * The the operational system server is running on it.
	 * @return statistics
	 */
	public static final String[] getOSInfo()
	{
		return new String[]
		{
			"OS: " + System.getProperty("os.name") + " Build: " + System.getProperty("os.version"),
			"OS Arch: " + System.getProperty("os.arch"),
			"..................................................",
			".................................................."
		};
	}
	
	/**
	 * The JAVA Runtime Enviroment properties.
	 * @return statistics
	 */
	public static final String[] getJREInfo()
	{
		return new String[]
		{
			"Java Platform Information",
			"Java Runtime  Name: " + System.getProperty("java.runtime.name"),
			"Java Version: " + System.getProperty("java.version"),
			"Java Class Version: " + System.getProperty("java.class.version"),
			"..................................................",
			".................................................."
		};
	}
	
	/**
	 * The general info related to the machine.
	 * @return statistics
	 */
	public static final String[] getRuntimeInfo()
	{
		return new String[]
		{
			"Runtime Information",
			"Current Free Heap Size: " + formatBytes(Runtime.getRuntime().freeMemory()),
			"Current Heap Size: " + formatBytes(Runtime.getRuntime().totalMemory()),
			"Maximum Heap Size: " + formatBytes(Runtime.getRuntime().maxMemory()),
			"..................................................",
			".................................................."
		};
	}
	
	/**
	 * The time service to get system time.
	 * @return statistics
	 */
	public static final String[] getSystemTime()
	{
		// instanciates Date Objec
		final Date dateInfo = new Date();
		
		// generates a simple date format
		final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
		
		// generates String that will get the formater info with values
		final String dayInfo = df.format(dateInfo);
		
		return new String[]
		{
			"..................................................",
			"System Time: " + dayInfo,
			".................................................."
		};
	}
	
	/**
	 * The system JVM properties.
	 * @return statistics
	 */
	public static final String[] getJVMInfo()
	{
		return new String[]
		{
			"Virtual Machine Information (JVM)",
			"JVM Name: " + System.getProperty("java.vm.name"),
			"JVM installation directory: " + System.getProperty("java.home"),
			"JVM version: " + System.getProperty("java.vm.version"),
			"JVM Vendor: " + System.getProperty("java.vm.vendor"),
			"JVM Info: " + System.getProperty("java.vm.info"),
			"..................................................",
			".................................................."
		};
	}
	
	/**
	 * Prints all other methods.
	 */
	public static final void printGeneralSystemInfo()
	{
		for (final String line : getSystemTime())
		{
			LOGGER.info(line);
		}
		
		for (final String line : getOSInfo())
		{
			LOGGER.info(line);
		}
		
		for (final String line : getCPUInfo())
		{
			LOGGER.info(line);
		}
		
		for (final String line : getRuntimeInfo())
		{
			LOGGER.info(line);
		}
		
		for (final String line : getJREInfo())
		{
			LOGGER.info(line);
		}
		
		for (final String line : getJVMInfo())
		{
			LOGGER.info(line);
		}
	}
	
	/**
	 * Gets the memory usage statistics of this application.
	 * @return memory usage statistics
	 */
	public static String[] getMemoryUsageStatistics()
	{
		final double max = Runtime.getRuntime().maxMemory(); // maxMemory is the upper limit the jvm can use
		final double allocated = Runtime.getRuntime().totalMemory(); // totalMemory the size of the current allocation pool
		final double nonAllocated = max - allocated; // non allocated memory till jvm limit
		final double cached = Runtime.getRuntime().freeMemory(); // freeMemory the unused memory in the allocation pool
		final double used = allocated - cached; // really used memory
		final double useable = max - used; // allocated, but non-used and non-allocated memory
		
		final DecimalFormat df = new DecimalFormat(" (0.00'%')");
		
		return new String[]
		{
			"+----", // ...
			"| Global Memory Information at " + CommonUtil.formatDate(new Date(), "H:mm:ss") + ":", // ...
			"|    |", // ...
			"| Allowed Memory: " + formatBytes(max),
			"|    |= Allocated Memory: " + formatBytes(allocated) + df.format((allocated / max) * 100),
			"|    |= Non-Allocated Memory: " + formatBytes(nonAllocated) + df.format((nonAllocated / max) * 100),
			"| Allocated Memory: " + formatBytes(allocated),
			"|    |= Used Memory: " + formatBytes(used) + df.format((used / max) * 100),
			"|    |= Unused (cached) Memory: " + formatBytes(cached) + df.format((cached / max) * 100),
			"| Useable Memory: " + formatBytes(useable) + df.format((useable / max) * 100), // ...
			"+----"
		};
	}
	
	public static String formatBytes(double bytes)
	{
		if (bytes < 1024)
		{
			return bytes + " B";
		}
		int z = (63 - Long.numberOfLeadingZeros(Math.round(bytes))) / 10;
		return String.format("%.1f %sB", bytes / (1L << (z * 10)), " KMGTPE".charAt(z));
	}


	public static String getUptime()
	{
		return TimeAmountInterpreter.consolidateMillis(ManagementFactory.getRuntimeMXBean().getUptime());
	}

	public static String getShortUptime()
	{
		final long uptimeInSec = (long) Math.ceil(ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
		final long s = (uptimeInSec / 1) % 60;
		final long m = (uptimeInSec / 60) % 60;
		final long h = (uptimeInSec / 3600) % 24;
		final long d = uptimeInSec / 86400;

		final StringBuilder tb = new StringBuilder();
		if (d > 0)
		{
			tb.append(d + "d");
		}

		if ((h > 0) || (tb.length() != 0))
		{
			tb.append(h + "h");
		}

		if ((m > 0) || (tb.length() != 0))
		{
			tb.append(m + "m");
		}

		if ((s > 0) || (tb.length() != 0))
		{
			tb.append(s + "s");
		}

		return tb.toString();
	}
}
