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
package org.l2junity.gameserver.model.interfaces;

import org.l2junity.commons.util.Rnd;
import org.l2junity.gameserver.enums.Position;
import org.l2junity.gameserver.model.Location;
import org.l2junity.gameserver.util.Util;

/**
 * Object world location storage interface.
 * @author xban1x
 */
public interface ILocational
{
	/**
	 * Gets the X coordinate of this object.
	 * @return the X coordinate
	 */
	double getX();
	
	/**
	 * Gets the Y coordinate of this object.
	 * @return the current Y coordinate
	 */
	double getY();
	
	/**
	 * Gets the Z coordinate of this object.
	 * @return the current Z coordinate
	 */
	double getZ();
	
	/**
	 * Gets the heading of this object.
	 * @return the current heading
	 */
	int getHeading();
	
	/**
	 * Gets a random position around the current location.
	 * @param minRange the minimum range from the center to pick a point.
	 * @param maxRange the maximum range from the center to pick a point.
	 * @return a random location between minRange and maxRange of the center location.
	 */
	default Location getRandomPosition(int minRange, int maxRange)
	{
		final int randomX = Rnd.get(minRange, maxRange);
		final int randomY = Rnd.get(minRange, maxRange);
		final double rndAngle = Math.toRadians(Rnd.get(360));
		
		final double newX = getX() + (randomX * Math.cos(rndAngle));
		final double newY = getY() + (randomY * Math.sin(rndAngle));
		
		return new Location(newX, newY, getZ());
	}
	
	/**
	 * @param to
	 * @return degree value of object 2 to the horizontal line with object 1 being the origin
	 */
	default double calculateAngleTo(ILocational to)
	{
		return Util.calculateAngleFrom(getX(), getY(), to.getX(), to.getY());
	}
	
	/**
	 * @param to
	 * @return the heading to the target specified
	 */
	default int calculateHeadingTo(ILocational to)
	{
		return Util.calculateHeadingFrom(getX(), getY(), to.getX(), to.getY());
	}
	
	/**
	 * Calculates the angle in degrees from this object to the given object.<br>
	 * The return value can be described as how much this object has to turn<br>
	 * to have the given object directly in front of it.
	 * @param target the object to which to calculate the angle
	 * @return the angle this object has to turn to have the given object in front of it
	 */
	default double calculateDirectionTo(ILocational target)
	{
		int heading = Util.calculateHeadingFrom(getX(), getY(), target.getX(), target.getY()) - getHeading();
		if (heading < 0)
		{
			heading = 65535 + heading;
		}
		return Util.convertHeadingToDegree(heading);
	}
	
	/**
	 * Computes the 2D Euclidean distance between this locational and (x, y).
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the 2D Euclidean distance between this locational and (x, y)
	 */
	default double distance2d(double x, double y)
	{
		return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
	}
	
	/**
	 * Computes the 2D Euclidean distance between this locational and locational loc.
	 * @param loc the locational
	 * @return the 2D Euclidean distance between this locational and locational loc
	 */
	default double distance2d(ILocational loc)
	{
		return distance2d(loc.getX(), loc.getY());
	}
	
	/**
	 * Computes the 3D Euclidean distance between this locational and (x, y, z).
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the 3D Euclidean distance between this locational and (x, y, z)
	 */
	default double distance3d(double x, double y, double z)
	{
		return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2) + Math.pow(getZ() - z, 2));
	}
	
	/**
	 * Computes the 3D Euclidean distance between this locational and locational loc.
	 * @param loc the locational
	 * @return the 3D Euclidean distance between this locational and locational loc
	 */
	default double distance3d(ILocational loc)
	{
		return distance3d(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Checks if this locational is in 2D Euclidean radius of (x, y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param radius the radius
	 * @return {@code true} if this locational is in radius of (x, y), {@code false} otherwise
	 */
	default boolean isInRadius2d(double x, double y, double radius)
	{
		return distance2d(x, y) <= radius;
	}
	
	/**
	 * Checks if this locational is in 2D Euclidean radius of locational loc
	 * @param loc the locational
	 * @param radius the radius
	 * @return {@code true} if this locational is in radius of locational loc, {@code false} otherwise
	 */
	default boolean isInRadius2d(ILocational loc, double radius)
	{
		return isInRadius2d(loc.getX(), loc.getY(), radius);
	}
	
	/**
	 * Checks if this locational is in 3D Euclidean radius of (x, y, z)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param radius the radius
	 * @return {@code true} if this locational is in radius of (x, y, z), {@code false} otherwise
	 */
	default boolean isInRadius3d(double x, double y, double z, double radius)
	{
		return distance3d(x, y, z) <= radius;
	}
	
	/**
	 * Checks if this locational is in 3D Euclidean radius of locational loc
	 * @param loc the locational
	 * @param radius the radius
	 * @return {@code true} if this locational is in radius of locational loc, {@code false} otherwise
	 */
	default boolean isInRadius3d(ILocational loc, double radius)
	{
		return isInRadius3d(loc.getX(), loc.getY(), loc.getZ(), radius);
	}
	
	/**
	 * @param target
	 * @return {@code true} if this location is in front of the target location based on the game's concept of position.
	 */
	default boolean isInFrontOf(ILocational target)
	{
		return Position.FRONT.equals(Position.getPosition(this, target));
	}
	
	/**
	 * @param target
	 * @return {@code true} if this location is in one of the sides of the target location based on the game's concept of position.
	 */
	default boolean isOnSideOf(ILocational target)
	{
		return Position.SIDE.equals(Position.getPosition(this, target));
	}
	
	/**
	 * @param target
	 * @return {@code true} if this location is behind the target location based on the game's concept of position.
	 */
	default boolean isBehind(ILocational target)
	{
		return Position.BACK.equals(Position.getPosition(this, target));
	}
}
