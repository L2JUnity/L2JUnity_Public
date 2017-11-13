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
package org.l2junity.loginserver.config;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Email")
public final class EmailConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "ServerInfoName", value = "Unconfigured L2J Unity", comment =
	{
		"Server Name"
	})
	public static String EMAIL_SERVERINFO_NAME;
	
	@ConfigField(name = "ServerInfoAddress", value = "info@myl2jserver.com", comment =
	{
		"Contact Address"
	})
	public static String EMAIL_SERVERINFO_ADDRESS;
	
	@ConfigField(name = "EmailSystemEnabled", value = "false", comment =
	{
		"Enable Email System"
	})
	public static boolean EMAIL_SYS_ENABLED;
	
	@ConfigField(name = "SmtpServerHost", value = "smtp.gmail.com", comment =
	{
		"Mail Server Host"
	})
	public static String EMAIL_SYS_HOST;
	
	@ConfigField(name = "SmtpServerPort", value = "465", comment =
	{
		"Mail Server Port"
	})
	public static int EMAIL_SYS_PORT;
	
	@ConfigField(name = "SmtpAuthRequired", value = "true", comment =
	{
		"Auth SMTP"
	})
	public static boolean EMAIL_SYS_SMTP_AUTH;
	
	@ConfigField(name = "SmtpFactory", value = "javax.net.ssl.SSLSocketFactory", comment =
	{
		"Mail Socket Factory"
	})
	public static String EMAIL_SYS_FACTORY;
	
	@ConfigField(name = "SmtpFactoryCallback", value = "false", comment =
	{
		"Mail Factory Callback"
	})
	public static boolean EMAIL_SYS_FACTORY_CALLBACK;
	
	@ConfigField(name = "SmtpUsername", value = "user@gmail.com", comment =
	{
		"Mail Server Auth - Username"
	})
	public static String EMAIL_SYS_USERNAME;
	
	@ConfigField(name = "SmtpPassword", value = "password", comment =
	{
		"Mail Server Auth - Password"
	})
	public static String EMAIL_SYS_PASSWORD;
	
	@ConfigField(name = "EmailSystemAddress", value = "noreply@myl2jserver.com", comment =
	{
		"Mail Address"
	})
	public static String EMAIL_SYS_ADDRESS;
	
	@ConfigField(name = "EmailDBSelectQuery", value = "SELECT value FROM account_data WHERE account_name", comment =
	{
		"Select Query for Email Addresses"
	})
	public static String EMAIL_SYS_SELECTQUERY;
	
	@ConfigField(name = "EmailDBField", value = "value", comment =
	{
		"Email Address Field"
	})
	public static String EMAIL_SYS_DBFIELD;
}
