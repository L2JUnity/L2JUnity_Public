Here you can place plugins as jar files.

Plugins are loaded using SPI (Service Provider Interface)

Each plugin MUST to contains META-INF/services/org.l2junity.gameserver.plugins.AbstractServerPlugin file
The contents of the file must be full class name to a class that implements AbstractServerPlugin

As an example u can check "l2junity-plugin-yal2logger" project

To activate plugin type in game //plugins
