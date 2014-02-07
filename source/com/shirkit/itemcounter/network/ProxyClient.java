package com.shirkit.itemcounter.network;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;

import com.shirkit.itemcounter.ItemCounter;
import com.shirkit.itemcounter.render.BufferedRenderer;
import com.shirkit.itemcounter.tile.BufferedItemCounter;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ProxyClient extends Proxy {

	@Override
	public void initializeEntityRenders() {
		super.initializeEntityRenders();

		//ClientRegistry.bindTileEntitySpecialRenderer(BufferedItemCounter.class, new BufferedRenderer());
		MinecraftForgeClient.registerItemRenderer(ItemCounter.instance.builtPipe.itemID, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void initializeTileEntities() {
		super.initializeTileEntities();
	}
}
