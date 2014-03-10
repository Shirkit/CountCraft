package com.shirkit.countcraft.integration.buildcraft;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ProxyClient extends Proxy {

	public static IconProvider iconProvider = new IconProvider();

	@Override
	public void registerRender(FMLPostInitializationEvent event) {
		MinecraftForgeClient.registerItemRenderer(BuildCraftHandler.instance.builtPipeItem.itemID, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(BuildCraftHandler.instance.builtPipeFluid.itemID, TransportProxyClient.pipeItemRenderer);
	}

}
