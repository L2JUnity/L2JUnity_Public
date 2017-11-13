/*
 * Copyright (c) 2016, RaveN Network INC. and/or its affiliates. All rights reserved.
 * RAVEN NETWORK INC. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Licensed Materials - Property of RaveN Network INC.
 * Restricted Rights - Use, duplication or disclosure restricted.
 */
package org.l2junity.gameserver.config;

import org.l2junity.commons.config.ConfigPropertiesLoader;
import org.l2junity.commons.config.annotation.ConfigClass;
import org.l2junity.commons.config.annotation.ConfigField;

/**
 * @author _dev_
 */
@ConfigClass(fileName = "ThreadPool")
public final class ThreadPoolConfig extends ConfigPropertiesLoader
{
	@ConfigField(name = "ScheduledThreadPoolSize", value = "-1", comment =
	{
		"Specifies how many threads will be in scheduled thread pool.",
		"If set to -1 (which is recommended), the server will decide the amount depending on the available processors"
	}, reloadable = false)
	public static int SCHEDULED_THREAD_POOL_SIZE;
	
	@ConfigField(name = "ThreadPoolSize", value = "-1", comment =
	{
		"Specifies how many threads will be in thread pool.",
		"If set to -1 (which is recommended), the server will decide the amount depending on the available processors"
	}, reloadable = false)
	public static int THREAD_POOL_SIZE;
}
