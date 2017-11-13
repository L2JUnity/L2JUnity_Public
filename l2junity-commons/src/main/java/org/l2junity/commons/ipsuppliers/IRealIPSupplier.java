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
package org.l2junity.commons.ipsuppliers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author UnAfraid
 */
public interface IRealIPSupplier extends Comparable<IRealIPSupplier>
{
	public String getIP() throws IOException;
	
	default boolean validateIP(String ip)
	{
		return (ip != null) && ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
	}
	
	default String defaultIPObtainMethod(String urlAddress) throws IOException
	{
		final URL url = new URL(urlAddress);
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(2 * 1000);
		con.setReadTimeout(2 * 1000);
		con.setDoInput(true);
		con.connect();
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
		{
			final String ip = in.readLine();
			if (validateIP(ip))
			{
				return ip;
			}
		}
		
		return null;
	}
	
	public int getPriority();
	
	@Override
	default int compareTo(IRealIPSupplier o)
	{
		return Integer.compare(getPriority(), o.getPriority());
	}
}
