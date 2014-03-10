package com.shirkit.countcraft.integration.te;

import net.minecraftforge.client.MinecraftForgeClient;

import com.shirkit.countcraft.render.BufferedRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ProxyClient extends Proxy {

	@Override
	public void registerRender(FMLPostInitializationEvent event) {

		BufferedRenderer energyRender = new BufferedRenderer(1.0f, 0.8f, 0.4f);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCounterEnergyCell.class, energyRender);
		MinecraftForgeClient.registerItemRenderer(ThermalExpansionHandler.instance.energycell.blockID, energyRender);

	}

}
