package com.shirkit.itemcounter.proxy;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy {
	@SidedProxy(clientSide = "com.shirkit.itemcounter.proxy.ProxyClient", serverSide = "com.shirkit.itemcounter.proxy.Proxy")
	public static Proxy proxy;

	public void searchForIntegration(FMLPreInitializationEvent event) {

	}
}
