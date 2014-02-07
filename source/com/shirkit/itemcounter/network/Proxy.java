package com.shirkit.itemcounter.network;

import cpw.mods.fml.common.SidedProxy;

public class Proxy {
	@SidedProxy(clientSide = "com.shirkit.itemcounter.network.ProxyClient", serverSide = "com.shirkit.itemcounter.network.Proxy")
	public static Proxy proxy;

	public void initializeTileEntities() {
	}

	public void initializeEntityRenders() {
	}

}
