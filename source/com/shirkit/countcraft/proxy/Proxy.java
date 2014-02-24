package com.shirkit.countcraft.proxy;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.integration.buildcraft.BuildCraftHandler;
import com.shirkit.countcraft.integration.nei.NEIHandler;
import com.shirkit.countcraft.integration.te.ThermalExpansionHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy {
	@SidedProxy(clientSide = "com.shirkit.countcraft.proxy.ProxyClient", serverSide = "com.shirkit.countcraft.proxy.Proxy")
	public static Proxy proxy;

	public void searchForIntegration(FMLPreInitializationEvent event) {
		if (Loader.isModLoaded("BuildCraft|Transport"))
			CountCraft.instance.integrations.add(new BuildCraftHandler());

		if (Loader.isModLoaded("NotEnoughItems"))
			CountCraft.instance.integrations.add(new NEIHandler());

		if (Loader.isModLoaded("ThermalExpansion"))
			CountCraft.instance.integrations.add(new ThermalExpansionHandler());
	}
}
