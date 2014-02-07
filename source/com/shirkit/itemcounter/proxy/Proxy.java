package com.shirkit.itemcounter.proxy;

import com.shirkit.itemcounter.ItemCounter;
import com.shirkit.itemcounter.integration.buildcraft.BuildCraftHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy {
	@SidedProxy(clientSide = "com.shirkit.itemcounter.proxy.ProxyClient", serverSide = "com.shirkit.itemcounter.proxy.Proxy")
	public static Proxy proxy;

	public void searchForIntegration(FMLPreInitializationEvent event) {
		if (Loader.isModLoaded("BuildCraft|Transport")) {
			ItemCounter.instance.integrations.add(new BuildCraftHandler());
		}
	}
}
