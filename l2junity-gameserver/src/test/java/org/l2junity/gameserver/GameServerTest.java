package org.l2junity.gameserver;

import org.junit.Test;
import org.l2junity.commons.loader.Loader;

/**
 * @author NosKun
 */
public class GameServerTest
{
    @Test
    public void loader()
    {
        new Loader(getClass().getPackage().getName());
    }
}