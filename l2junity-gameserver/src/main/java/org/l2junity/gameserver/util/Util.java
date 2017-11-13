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
package org.l2junity.gameserver.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.l2junity.commons.util.concurrent.ThreadPool;
import org.l2junity.gameserver.config.GeneralConfig;
import org.l2junity.gameserver.enums.HtmlActionScope;
import org.l2junity.gameserver.enums.IllegalActionPunishmentType;
import org.l2junity.gameserver.model.World;
import org.l2junity.gameserver.model.WorldObject;
import org.l2junity.gameserver.model.actor.Creature;
import org.l2junity.gameserver.model.actor.instance.PlayerInstance;
import org.l2junity.gameserver.model.actor.tasks.player.IllegalPlayerActionTask;
import org.l2junity.gameserver.network.client.send.AbstractHtmlPacket;
import org.l2junity.gameserver.network.client.send.NpcHtmlMessage;
import org.l2junity.gameserver.network.client.send.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General Utility functions related to game server.
 */
public final class Util
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
	private static final NumberFormat ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	
	private Util()
	{
		// utility class
	}
	
	public static void handleIllegalPlayerAction(PlayerInstance actor, String message, IllegalActionPunishmentType punishment)
	{
		ThreadPool.schedule(new IllegalPlayerActionTask(actor, message, punishment), 5000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return degree value of object 2 to the horizontal line with object 1 being the origin
	 */
	public static double calculateAngleFrom(double fromX, double fromY, double toX, double toY)
	{
		double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return angleTarget;
	}
	
	public static double convertHeadingToDegree(int clientHeading)
	{
		return clientHeading / 182.044444444;
	}
	
	public static double convertHeadingToRadian(int clientHeading)
	{
		return (clientHeading / 65535.0) * 6.283185307179586;
	}
	
	/**
	 * Calculates heading from a given angle in degree following the retail-like formula:<br>
	 * <u>(unsigned short)(int)floor(radian * 65535.0 / 6.283185307179586)</u><br>
	 * <font color=red>It is highly suggested to use {@linkplain Util#convertRadianToHeading(double)} if you are working with radians. This method is not as accurate.</font>
	 * @param degree angle in degrees.
	 * @return heading in value range from 0 to 65535, where:
	 *         <ul>
	 *         <li>0° = 0 heading.</li>
	 *         <li>90° = 16383(x3FFF) heading.</li>
	 *         <li>180° = 32767(x7FFF) heading.</li>
	 *         <li>-180° = 32769(x8001) heading.</li>
	 *         <li>-90° = 49153(xC001) heading.</li>
	 *         <li>-0.01° = 65535(xFFFF) heading.</li>
	 *         </ul>
	 */
	public static int convertDegreeToHeading(double degree)
	{
		return convertRadianToHeading(Math.toRadians(degree));
	}
	
	/**
	 * Calculates heading from a given angle in degree following the retail-like formula:<br>
	 * <u>(unsigned short)(int)floor(radian * 65535.0 / 6.283185307179586)</u>
	 * @param radians angle in radians.
	 * @return heading in value range from 0 to 65535, where:
	 *         <ul>
	 *         <li>0 = 0 heading.</li>
	 *         <li>pi/2 = 16383(x3FFF) heading.</li>
	 *         <li>pi = 32767(x7FFF) heading.</li>
	 *         <li>-pi = 32768(x8000) heading.</li>
	 *         <li>-pi/2 = 49152(xC000) heading.</li>
	 *         <li>-0.00001 = 65535(xFFFF) heading.</li>
	 *         </ul>
	 */
	public static int convertRadianToHeading(double radians)
	{
		return Short.toUnsignedInt((short) Math.floor((radians * 65535.0) / 6.283185307179586));
	}
	
	/**
	 * Calculates heading from a point to a point following the retail-like formula:<br>
	 * <u>(unsigned short)(int)floor(atan2(toY - fromY, toX - fromX) * 65535.0 / 6.283185307179586)</u>
	 * @param fromX from X coord
	 * @param fromY from Y coord
	 * @param toX to X coord
	 * @param toY to Y coord
	 * @return heading in value range from 0 to 65535.
	 */
	public static int calculateHeadingFrom(double fromX, double fromY, double toX, double toY)
	{
		return convertRadianToHeading(Math.atan2(toY - fromY, toX - fromX));
	}
	
	public static int calculateHeadingFrom(double dx, double dy)
	{
		return convertRadianToHeading(Math.atan2(dy, dx));
	}
	
	/**
	 * @param range
	 * @param obj1
	 * @param obj2
	 * @param includeZAxis
	 * @return {@code true} if the two objects are within specified range between each other, {@code false} otherwise
	 */
	public static boolean checkIfInRange(int range, WorldObject obj1, WorldObject obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null) || (obj1.getInstanceWorld() != obj2.getInstanceWorld()))
		{
			return false;
		}
		if (range == -1)
		{
			return true; // not limited
		}
		
		if (obj1.isCreature())
		{
			range += ((Creature) obj1).getTemplate().getCollisionRadius();
		}
		if (obj2.isCreature())
		{
			range += ((Creature) obj2).getTemplate().getCollisionRadius();
		}
		
		return includeZAxis ? obj1.isInRadius3d(obj2, range) : obj1.isInRadius2d(obj2, range);
	}
	
	/**
	 * Checks if object is within short (sqrt(int.max_value)) radius, not using collisionRadius. Faster calculation than checkIfInRange if distance is short and collisionRadius isn't needed. Not for long distance checks (potential teleports, far away castles etc).
	 * @param range
	 * @param obj1
	 * @param obj2
	 * @param includeZAxis if true, check also Z axis (3-dimensional check), otherwise only 2D
	 * @return {@code true} if objects are within specified range between each other, {@code false} otherwise
	 */
	public static boolean checkIfInShortRange(int range, WorldObject obj1, WorldObject obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return false;
		}
		if (range == -1)
		{
			return true; // not limited
		}
		
		return includeZAxis ? obj1.isInRadius3d(obj2, range) : obj1.isInRadius2d(obj2, range);
	}
	
	/**
	 * Checks if the cube intersects the sphere.
	 * @param x1 the cube's first point x
	 * @param y1 the cube's first point y
	 * @param z1 the cube's first point z
	 * @param x2 the cube's second point x
	 * @param y2 the cube's second point y
	 * @param z2 the cube's second point z
	 * @param sX the sphere's middle x
	 * @param sY the sphere's middle y
	 * @param sZ the sphere's middle z
	 * @param radius the sphere's radius
	 * @return {@code true} if cube intersects sphere, {@code false} otherwise
	 */
	public static boolean cubeIntersectsSphere(double x1, double y1, double z1, double x2, double y2, double z2, double sX, double sY, double sZ, int radius)
	{
		double d = radius * radius;
		if (sX < x1)
		{
			d -= Math.pow(sX - x1, 2);
		}
		else if (sX > x2)
		{
			d -= Math.pow(sX - x2, 2);
		}
		if (sY < y1)
		{
			d -= Math.pow(sY - y1, 2);
		}
		else if (sY > y2)
		{
			d -= Math.pow(sY - y2, 2);
		}
		if (sZ < z1)
		{
			d -= Math.pow(sZ - z1, 2);
		}
		else if (sZ > z2)
		{
			d -= Math.pow(sZ - z2, 2);
		}
		return d > 0;
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only numbers, {@code false} otherwise
	 */
	public static boolean isDigit(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (char c : text.toCharArray())
		{
			if (!Character.isDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} is integer, {@code false} otherwise
	 */
	public static boolean isInteger(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} is float, {@code false} otherwise
	 */
	public static boolean isFloat(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		try
		{
			Float.parseFloat(text);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} is double, {@code false} otherwise
	 */
	public static boolean isDouble(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		try
		{
			Double.parseDouble(text);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * @param <T>
	 * @param name - the text to check
	 * @param enumType
	 * @return {@code true} if {@code text} is enum, {@code false} otherwise
	 */
	public static <T extends Enum<T>> boolean isEnum(String name, Class<T> enumType)
	{
		if ((name == null) || name.isEmpty())
		{
			return false;
		}
		try
		{
			return Enum.valueOf(enumType, name) != null;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only letters and/or numbers, {@code false} otherwise
	 */
	public static boolean isAlphaNumeric(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (char c : text.toCharArray())
		{
			if (!Character.isLetterOrDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Format the specified digit using the digit grouping symbol "," (comma).<br>
	 * For example, 123456789 becomes 123,456,789.
	 * @param amount - the amount of adena
	 * @return the formatted adena amount
	 */
	public static String formatAdena(long amount)
	{
		synchronized (ADENA_FORMATTER)
		{
			return ADENA_FORMATTER.format(amount);
		}
	}
	
	public static String getDateString(Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date.getTime());
	}
	
	private static void buildHtmlBypassCache(PlayerInstance player, HtmlActionScope scope, String html)
	{
		String htmlLower = html.toLowerCase(Locale.ENGLISH);
		int bypassEnd = 0;
		int bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
		int bypassStartEnd;
		while (bypassStart != -1)
		{
			bypassStartEnd = bypassStart + 9;
			bypassEnd = htmlLower.indexOf("\"", bypassStartEnd);
			if (bypassEnd == -1)
			{
				break;
			}
			
			int hParamPos = htmlLower.indexOf("-h ", bypassStartEnd);
			String bypass;
			if ((hParamPos != -1) && (hParamPos < bypassEnd))
			{
				bypass = html.substring(hParamPos + 3, bypassEnd).trim();
			}
			else
			{
				bypass = html.substring(bypassStartEnd, bypassEnd).trim();
			}
			
			int firstParameterStart = bypass.indexOf(AbstractHtmlPacket.VAR_PARAM_START_CHAR);
			if (firstParameterStart != -1)
			{
				bypass = bypass.substring(0, firstParameterStart + 1);
			}
			
			if (GeneralConfig.HTML_ACTION_CACHE_DEBUG)
			{
				LOGGER.info("Cached html bypass(" + scope.toString() + "): '" + bypass + "'");
			}
			player.addHtmlAction(scope, bypass);
			bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
		}
	}
	
	private static void buildHtmlLinkCache(PlayerInstance player, HtmlActionScope scope, String html)
	{
		String htmlLower = html.toLowerCase(Locale.ENGLISH);
		int linkEnd = 0;
		int linkStart = htmlLower.indexOf("=\"link ", linkEnd);
		int linkStartEnd;
		while (linkStart != -1)
		{
			linkStartEnd = linkStart + 7;
			linkEnd = htmlLower.indexOf("\"", linkStartEnd);
			if (linkEnd == -1)
			{
				break;
			}
			
			String htmlLink = html.substring(linkStartEnd, linkEnd).trim();
			if (htmlLink.isEmpty())
			{
				LOGGER.warn("Html link path is empty!");
				continue;
			}
			
			if (htmlLink.contains(".."))
			{
				LOGGER.warn("Html link path is invalid: " + htmlLink);
				continue;
			}
			
			if (GeneralConfig.HTML_ACTION_CACHE_DEBUG)
			{
				LOGGER.info("Cached html link(" + scope.toString() + "): '" + htmlLink + "'");
			}
			// let's keep an action cache with "link " lowercase literal kept
			player.addHtmlAction(scope, "link " + htmlLink);
			linkStart = htmlLower.indexOf("=\"link ", linkEnd);
		}
	}
	
	/**
	 * Builds the html action cache for the specified scope.<br>
	 * An {@code npcObjId} of 0 means, the cached actions can be clicked<br>
	 * without beeing near an npc which is spawned in the world.
	 * @param player the player to build the html action cache for
	 * @param scope the scope to build the html action cache for
	 * @param npcObjId the npc object id the html actions are cached for
	 * @param html the html code to parse
	 */
	public static void buildHtmlActionCache(PlayerInstance player, HtmlActionScope scope, int npcObjId, String html)
	{
		if ((player == null) || (scope == null) || (npcObjId < 0) || (html == null))
		{
			throw new IllegalArgumentException();
		}
		
		if (GeneralConfig.HTML_ACTION_CACHE_DEBUG)
		{
			LOGGER.info("Set html action npc(" + scope.toString() + "): " + npcObjId);
		}
		player.setHtmlActionOriginObjectId(scope, npcObjId);
		buildHtmlBypassCache(player, scope, html);
		buildHtmlLinkCache(player, scope, html);
	}
	
	/**
	 * Helper method to send a NpcHtmlMessage to the specified player.
	 * @param activeChar the player to send the html content to
	 * @param html the html content
	 */
	public static void sendHtml(PlayerInstance activeChar, String html)
	{
		final NpcHtmlMessage npcHtml = new NpcHtmlMessage();
		npcHtml.setHtml(html);
		activeChar.sendPacket(npcHtml);
	}
	
	/**
	 * Helper method to send a community board html to the specified player.<br>
	 * HtmlActionCache will be build with npc origin 0 which means the<br>
	 * links on the html are not bound to a specific npc.
	 * @param activeChar the player
	 * @param html the html content
	 */
	public static void sendCBHtml(PlayerInstance activeChar, String html)
	{
		sendCBHtml(activeChar, html, 0);
	}
	
	/**
	 * Helper method to send a community board html to the specified player.<br>
	 * When {@code npcObjId} is greater -1 the HtmlActionCache will be build<br>
	 * with the npcObjId as origin. An origin of 0 means the cached bypasses<br>
	 * are not bound to a specific npc.
	 * @param activeChar the player to send the html content to
	 * @param html the html content
	 * @param npcObjId bypass origin to use
	 */
	public static void sendCBHtml(PlayerInstance activeChar, String html, int npcObjId)
	{
		sendCBHtml(activeChar, html, null, npcObjId);
	}
	
	/**
	 * Helper method to send a community board html to the specified player.<br>
	 * HtmlActionCache will be build with npc origin 0 which means the<br>
	 * links on the html are not bound to a specific npc. It also fills a<br>
	 * multiedit field in the send html if fillMultiEdit is not null.
	 * @param activeChar the player
	 * @param html the html content
	 * @param fillMultiEdit text to fill the multiedit field with(may be null)
	 */
	public static void sendCBHtml(PlayerInstance activeChar, String html, String fillMultiEdit)
	{
		sendCBHtml(activeChar, html, fillMultiEdit, 0);
	}
	
	/**
	 * Helper method to send a community board html to the specified player.<br>
	 * It fills a multiedit field in the send html if {@code fillMultiEdit}<br>
	 * is not null. When {@code npcObjId} is greater -1 the HtmlActionCache will be build<br>
	 * with the npcObjId as origin. An origin of 0 means the cached bypasses<br>
	 * are not bound to a specific npc.
	 * @param activeChar the player
	 * @param html the html content
	 * @param fillMultiEdit text to fill the multiedit field with(may be null)
	 * @param npcObjId bypass origin to use
	 */
	public static void sendCBHtml(PlayerInstance activeChar, String html, String fillMultiEdit, int npcObjId)
	{
		if ((activeChar == null) || (html == null))
		{
			return;
		}
		
		activeChar.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);
		
		if (npcObjId > -1)
		{
			buildHtmlActionCache(activeChar, HtmlActionScope.COMM_BOARD_HTML, npcObjId, html);
		}
		
		if (fillMultiEdit != null)
		{
			activeChar.sendPacket(new ShowBoard(html, "1001"));
			fillMultiEditContent(activeChar, fillMultiEdit);
		}
		else
		{
			if (html.length() < 16250)
			{
				activeChar.sendPacket(new ShowBoard(html, "101"));
				activeChar.sendPacket(new ShowBoard(null, "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
			else if (html.length() < (16250 * 2))
			{
				activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250), "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
			else if (html.length() < (16250 * 3))
			{
				activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
			}
			else
			{
				activeChar.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
				activeChar.sendPacket(new ShowBoard(null, "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
		}
	}
	
	/**
	 * Fills the community board's multiedit window with text. Must send after sendCBHtml
	 * @param activeChar
	 * @param text
	 */
	public static void fillMultiEditContent(PlayerInstance activeChar, String text)
	{
		activeChar.sendPacket(new ShowBoard(Arrays.asList("0", "0", "0", "0", "0", "0", activeChar.getName(), Integer.toString(activeChar.getObjectId()), activeChar.getAccountName(), "9", " ", " ", text.replaceAll("<br>", System.lineSeparator()), "0", "0", "0", "0")));
	}
	
	public static boolean isInsideRangeOfObjectId(WorldObject obj, int targetObjId, int radius)
	{
		final WorldObject target = World.getInstance().findObject(targetObjId);
		return (target != null) && obj.isInRadius3d(target, radius);
	}
	
	public static String readAllLines(Path path, Charset cs, String newLineDelimiter) throws IOException
	{
		final StringBuilder sb = new StringBuilder();
		
		final List<String> lines = Files.readAllLines(path, cs);
		lines.forEach(line ->
		{
			sb.append(line);
			if (newLineDelimiter != null)
			{
				sb.append(newLineDelimiter);
			}
		});
		
		return sb.toString();
	}
}
