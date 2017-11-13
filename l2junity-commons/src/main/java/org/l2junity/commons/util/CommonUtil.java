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
package org.l2junity.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonUtil
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	
	private CommonUtil()
	{
		// utility class
	}
	
	/**
	 * Verify if a file name is valid.
	 * @param name the name of the file
	 * @return {@code true} if the file name is valid, {@code false} otherwise
	 */
	public static boolean isValidFileName(String name)
	{
		final File f = new File(name);
		try
		{
			f.getCanonicalPath();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	/**
	 * @param val
	 * @param format
	 * @return formatted double value by specified format.
	 */
	public static String formatDouble(double val, String format)
	{
		final DecimalFormat formatter = new DecimalFormat(format, new DecimalFormatSymbols(Locale.ENGLISH));
		return formatter.format(val);
	}
	
	/**
	 * Format the given date on the given format
	 * @param date : the date to format.
	 * @param format : the format to correct by.
	 * @return a string representation of the formatted date.
	 */
	public static String formatDate(Date date, String format)
	{
		if (date == null)
		{
			return null;
		}
		final DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * Gets the next or same closest date from the specified days in {@code daysOfWeek Array} at specified {@code hour} and {@code min}.
	 * @param daysOfWeek the days of week
	 * @param hour the hour
	 * @param min the min
	 * @return the next or same date from the days of week at specified time
	 * @throws IllegalArgumentException if the {@code daysOfWeek Array} is empty.
	 */
	public static LocalDateTime getNextClosestDateTime(DayOfWeek[] daysOfWeek, int hour, int min) throws IllegalArgumentException
	{
		return getNextClosestDateTime(Arrays.asList(daysOfWeek), hour, min);
	}
	
	/**
	 * Gets the next or same closest date from the specified days in {@code daysOfWeek List} at specified {@code hour} and {@code min}.
	 * @param daysOfWeek the days of week
	 * @param hour the hour
	 * @param min the min
	 * @return the next or same date from the days of week at specified time
	 * @throws IllegalArgumentException if the {@code daysOfWeek List} is empty.
	 */
	public static LocalDateTime getNextClosestDateTime(List<DayOfWeek> daysOfWeek, int hour, int min) throws IllegalArgumentException
	{
		if (daysOfWeek.isEmpty())
		{
			throw new IllegalArgumentException("daysOfWeek should not be empty.");
		}
		
		final LocalDateTime dateNow = LocalDateTime.now();
		final LocalDateTime dateNowWithDifferentTime = dateNow.withHour(hour).withMinute(min).withSecond(0);
		
		// @formatter:off
		return daysOfWeek.stream()
			.map(d -> dateNowWithDifferentTime.with(TemporalAdjusters.nextOrSame(d)))
			.filter(d -> d.isAfter(dateNow))
			.min(Comparator.naturalOrder())
			.orElse(dateNowWithDifferentTime.with(TemporalAdjusters.next(daysOfWeek.get(0))));
		// @formatter:on
	}
	
	/**
	 * Method to get the stack trace of a Throwable into a String
	 * @param t Throwable to get the stacktrace from
	 * @return stack trace from Throwable as String
	 */
	public static String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String getTraceString(StackTraceElement[] stackTraceElements)
	{
		final StringJoiner sj = new StringJoiner(System.lineSeparator());
		for (final StackTraceElement stackTraceElement : stackTraceElements)
		{
			sj.add(stackTraceElement.toString());
		}
		return sj.toString();
	}
	
	public static int min(int value1, int value2, int... values)
	{
		int min = Math.min(value1, value2);
		for (int value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static int max(int value1, int value2, int... values)
	{
		int max = Math.max(value1, value2);
		for (int value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static long min(long value1, long value2, long... values)
	{
		long min = Math.min(value1, value2);
		for (long value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static long max(long value1, long value2, long... values)
	{
		long max = Math.max(value1, value2);
		for (long value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static float min(float value1, float value2, float... values)
	{
		float min = Math.min(value1, value2);
		for (float value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static float max(float value1, float value2, float... values)
	{
		float max = Math.max(value1, value2);
		for (float value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static double min(double value1, double value2, double... values)
	{
		double min = Math.min(value1, value2);
		for (double value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static double max(double value1, double value2, double... values)
	{
		double max = Math.max(value1, value2);
		for (double value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static int getIndexOfMaxValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] > array[index])
			{
				index = i;
			}
		}
		return index;
	}
	
	public static int getIndexOfMinValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < array[index])
			{
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static int map(int input, int inputMin, int inputMax, int outputMin, int outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static long map(long input, long inputMin, long inputMax, long outputMin, long outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static double map(double input, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static int constrain(int input, int min, int max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static long constrain(long input, long min, long max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static double constrain(double input, double min, double max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static float constrain(float input, float min, float max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static boolean startsWith(String[] array, String obj)
	{
		for (String element : array)
		{
			if (element.startsWith(obj))
			{
				return true;
			}
		}
		return false;
	}
	
	public static int parseNextInt(StringTokenizer st, int defaultVal)
	{
		try
		{
			String value = st.nextToken().trim();
			return Integer.parseInt(value);
		}
		catch (Exception e)
		{
			return defaultVal;
		}
	}
	
	public static int parseInt(String value, int defaultValue)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Based on implode() in PHP
	 * @param <T>
	 * @param iteratable
	 * @param delim
	 * @return a delimited string for a given array of string elements.
	 */
	public static <T> String implode(Iterable<T> iteratable, String delim)
	{
		final StringJoiner sj = new StringJoiner(delim);
		iteratable.forEach(o -> sj.add(o.toString()));
		return sj.toString();
	}
	
	/**
	 * Based on implode() in PHP
	 * @param <T>
	 * @param array
	 * @param delim
	 * @return a delimited string for a given array of string elements.
	 */
	public static <T> String implode(T[] array, String delim)
	{
		final StringJoiner sj = new StringJoiner(delim);
		for (T o : array)
		{
			sj.add(o.toString());
		}
		return sj.toString();
	}
	
	/**
	 * Prints a pretty print section to split the console.
	 * @param sectionName
	 */
	public static void printSection(String sectionName)
	{
		final StringBuilder sb = new StringBuilder(120);
		
		for (int i = 0; i < (120 - 3 - sectionName.length() - 2); i++)
		{
			sb.append('-');
		}
		
		sb.append("=[ ").append(sectionName).append(" ]");
		
		LOGGER.info(sb.toString());
	}
	
	@SafeVarargs
	public static <T> List<T> argAndVarArgsToList(T value, T... values)
	{
		final List<T> valueList = new ArrayList<>(1 + (values != null ? values.length : 0));
		forEachArgAndVarArgs(valueList::add, value, values);
		return Collections.unmodifiableList(valueList);
	}
	
	@SafeVarargs
	public static <T> void forEachArgAndVarArgs(Consumer<T> consumer, T value, T... values)
	{
		consumer.accept(value);
		if (values != null)
		{
			for (T v : values)
			{
				consumer.accept(v);
			}
		}
	}
	
	/**
	 * @return general purpose date time formatter
	 */
	public static DateTimeFormatter getDateTimeFormatter()
	{
		return DATE_TIME_FORMATTER;
	}
	
	/**
	 * @return the filename date time formatter, in windows : is not allowed!
	 */
	public static DateTimeFormatter getFilenameDateTimeFormatter()
	{
		return FILENAME_DATE_TIME_FORMATTER;
	}
}
