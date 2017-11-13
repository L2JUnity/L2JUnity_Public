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
package org.l2junity.gameserver.config;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "SellBuff")
public final class SellBuffConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "SellBuffEnable", value = "false", comment =
	{
		"Enable/Disable selling buffs"
	})
	public static boolean SELLBUFF_ENABLED;
	
	@ConfigField(name = "MpCostMultipler", value = "1", comment =
	{
		"Multipler for mana cost of buffs"
	})
	public static int SELLBUFF_MP_MULTIPLER;
	
	@ConfigField(name = "PaymentID", value = "57", comment =
	{
		"Payment for Sell Buff System, can be changed to custom server coin"
	})
	public static int SELLBUFF_PAYMENT_ID;
	
	@ConfigField(name = "MinimalPrice", value = "100000", comment =
	{
		"Minimal price of every buff"
	})
	public static long SELLBUFF_MIN_PRICE;
	
	@ConfigField(name = "MaximalPrice", value = "100000000", comment =
	{
		"Maximal price of every buff"
	})
	public static long SELLBUFF_MAX_PRICE;
	
	@ConfigField(name = "MaxBuffs", value = "20", comment =
	{
		"Maximum count of buffs in sell list"
	})
	public static int SELLBUFF_MAX_BUFFS;
}
