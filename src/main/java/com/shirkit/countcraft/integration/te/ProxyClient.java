package com.shirkit.countcraft.integration.te;

import com.shirkit.countcraft.render.BufferedRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ProxyClient extends Proxy {

	@Override
	public void registerRender(FMLPostInitializationEvent event) {

		BufferedRenderer energyRender = new BufferedRenderer(1.0f, 0.8f, 0.4f);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCounterEnergyCell.class, energyRender);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ThermalExpansionHandler.instance.energycell), energyRender);

	}

}
