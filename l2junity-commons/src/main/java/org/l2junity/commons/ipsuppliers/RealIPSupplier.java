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

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.l2junity.commons.ipsuppliers.impl.AmazongIPSupplier;
import org.l2junity.commons.ipsuppliers.impl.IFConfigIPSupplier;
import org.l2junity.commons.ipsuppliers.impl.UnityIPSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class RealIPSupplier
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RealIPSupplier.class);
	
	private static final SortedSet<IRealIPSupplier> IP_PROVIDERS = new TreeSet<>(Arrays.asList(new AmazongIPSupplier(), new IFConfigIPSupplier(), new UnityIPSupplier()));
	private static final String LOCALHOST = "127.0.0.1";
	
	public String get()
	{
		for (IRealIPSupplier provider : IP_PROVIDERS)
		{
			try
			{
				final String ip = provider.getIP();
				if (ip != null)
				{
					return ip;
				}
			}
			catch (IOException e)
			{
				LOGGER.warn("Failed to obtain IP from provider: {} reason: {}", provider.getClass().getSimpleName(), e.getMessage());
				LOGGER.debug("Failed to obtain IP from provider: {}", provider.getClass().getSimpleName(), e);
			}
		}
		return LOCALHOST;
	}
}
