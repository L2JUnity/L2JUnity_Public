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

import java.util.List;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author lord_rex
 */
@ConfigClass(fileName = "Telnet")
public final class TelnetConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "EnableTelnet", value = "false", comment =
	{
		"The defaults are set to be retail-like. If you modify any of these settings your server will deviate from being retail-like.",
		"Warning:",
		"Please take extreme caution when changing anything. Also please understand what you are changing before you do so on a live server.",
		"---------------------------------------------------------------------------",
		"Allows text based monitoring and administration of L2J GS",
		"by using a telnet client. Communication protocol is insecure",
		"and you should use SSL tunnels, VPN, etc. if you plan to connect",
		"over non-trusted channels."
	}, reloadable = false)
	public static boolean TELNET_ENABLED;
	
	@ConfigField(name = "EnableTelnetUPnP", value = "false", comment =
	{
		"Enables automatic port mapping for telnet server.",
		"If you have a router telnet server will request for port forwarding."
	}, reloadable = false)
	public static boolean TELNET_UPNP_ENABLED;
	
	@ConfigField(name = "TelnetPassword", value = "somepassword", comment =
	{
		"If the following is not set, a random password is generated on server startup."
	}, reloadable = false)
	public static String TELNET_PASSWORD;
	
	@ConfigField(name = "BindAddress", value = "127.0.0.1", comment =
	{
		"This is the hostname address on which telnet server will be listening.",
		"Note for all adapters use: *"
	}, reloadable = false)
	public static String TELNET_HOSTNAME;
	
	@ConfigField(name = "ListOfHosts", value = "127.0.0.1,localhost,::1", comment =
	{
		"This list can contain IPs or Hosts of clients you wish to allow. Hostnames must be resolvable to an IP.",
		"Example: 0.0.0.0,host,0.0.0.1,host2,host3,host4,0.0.0.3"
	}, reloadable = false)
	public static List<String> TELNET_HOSTS;
	
	@ConfigField(name = "Port", value = "54321", comment =
	{
		"This is the port L2J should listen to for incoming telnet",
		"requests."
	}, reloadable = false)
	public static int TELNET_PORT;
}
