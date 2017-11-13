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

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.IntStream;

import org.l2junity.commons.config.IConfigLoader;
import org.l2junity.commons.ipsuppliers.RealIPSupplier;
import org.l2junity.commons.util.BasePathProvider;
import org.l2junity.commons.util.PropertiesParser;
import org.l2junity.commons.util.XmlReaderException;
import org.l2junity.gameserver.data.xml.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * @author lord_rex (No, not me - it was eclipse putting my name here.)
 */
public final class NetworkConfig implements IConfigLoader, IGameXmlReader
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(NetworkConfig.class);
	
	public static final Path IP_CONFIG_FILE = BasePathProvider.resolvePath(Paths.get("config", "ipconfig.xml"));
	
	public static List<String> GAME_SERVER_SUBNETS = new ArrayList<>();
	public static List<String> GAME_SERVER_HOSTS = new ArrayList<>();
	
	public NetworkConfig()
	{
		// visibility
	}
	
	@Override
	public void load(PropertiesParser override)
	{
		if (Files.exists(IP_CONFIG_FILE))
		{
			try
			{
				parseFile(IP_CONFIG_FILE);
			}
			catch (XmlReaderException | IOException e)
			{
				LOGGER.error("Error loading ipconfig.xml", e);
			}
		}
		else
		{
			LOGGER.info("Using automatic configuration...");
			autoIpConfig();
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "gameserver", gsNode ->
		{
			forEach(gsNode, "define", defineNode ->
			{
				final NamedNodeMap attrs = defineNode.getAttributes();
				GAME_SERVER_SUBNETS.add(attrs.getNamedItem("subnet").getNodeValue());
				GAME_SERVER_HOSTS.add(attrs.getNamedItem("address").getNodeValue());
				
				if (GAME_SERVER_HOSTS.size() != GAME_SERVER_SUBNETS.size())
				{
					LOGGER.warn("Failed to Load " + IP_CONFIG_FILE + " File - subnets does not match server addresses.");
				}
			});
			
			final String address = parseString(gsNode.getAttributes(), "address", null);
			if (address == null)
			{
				LOGGER.warn("Failed to load " + IP_CONFIG_FILE + " file - default server address is missing.");
				GAME_SERVER_HOSTS.add("127.0.0.1");
			}
			else
			{
				GAME_SERVER_HOSTS.add(address);
			}
			
			GAME_SERVER_SUBNETS.add("0.0.0.0/0");
		});
	}
	
	protected void autoIpConfig()
	{
		try
		{
			Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
			
			while (niList.hasMoreElements())
			{
				NetworkInterface ni = niList.nextElement();
				
				if (!ni.isUp() || ni.isVirtual())
				{
					continue;
				}
				
				if (!ni.isLoopback() && ((ni.getHardwareAddress() == null) || (ni.getHardwareAddress().length != 6)))
				{
					continue;
				}
				
				for (InterfaceAddress ia : ni.getInterfaceAddresses())
				{
					if (ia.getAddress() instanceof Inet6Address)
					{
						continue;
					}
					
					final String hostAddress = ia.getAddress().getHostAddress();
					final int subnetPrefixLength = ia.getNetworkPrefixLength();
					final int subnetMaskInt = IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0) << (32 - subnetPrefixLength);
					final int hostAddressInt = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
					final int subnetAddressInt = hostAddressInt & subnetMaskInt;
					final String subnetAddress = ((subnetAddressInt >> 24) & 0xFF) + "." + ((subnetAddressInt >> 16) & 0xFF) + "." + ((subnetAddressInt >> 8) & 0xFF) + "." + (subnetAddressInt & 0xFF);
					final String subnet = subnetAddress + '/' + subnetPrefixLength;
					if (!GAME_SERVER_SUBNETS.contains(subnet) && !subnet.equals("0.0.0.0/0"))
					{
						GAME_SERVER_SUBNETS.add(subnet);
						GAME_SERVER_HOSTS.add(hostAddress);
						LOGGER.info("NetworkConfig - Adding new subnet: " + subnet + " address: " + hostAddress);
					}
				}
			}
			
			// External host and subnet
			final String externalIp = new RealIPSupplier().get();
			GAME_SERVER_HOSTS.add(externalIp);
			GAME_SERVER_SUBNETS.add("0.0.0.0/0");
			LOGGER.info("NetworkConfig - Adding new subnet: 0.0.0.0/0 address: " + externalIp);
		}
		catch (SocketException e)
		{
			LOGGER.info("NetworkConfig - Configuration failed please configure manually using ipconfig.xml", e);
			System.exit(0);
		}
	}
	
	public List<String> getSubnets()
	{
		if (GAME_SERVER_SUBNETS.isEmpty())
		{
			return Arrays.asList("0.0.0.0/0");
		}
		return GAME_SERVER_SUBNETS;
	}
	
	public List<String> getHosts()
	{
		if (GAME_SERVER_HOSTS.isEmpty())
		{
			return Arrays.asList("127.0.0.1");
		}
		return GAME_SERVER_HOSTS;
	}
}
