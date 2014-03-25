package com.shirkit.countcraft.proxy;

import net.minecraftforge.client.MinecraftForgeClient;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.gui.GuiCounter;
import com.shirkit.countcraft.render.BufferedRenderer;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ProxyClient extends Proxy {

	@Override
	public void searchForIntegration(FMLPreInitializationEvent event) {
		super.searchForIntegration(event);

		// Then we search for any network/gui finders, if they have one
		for (IIntegrationHandler handler : CountCraft.instance.integrations) {

			IGuiListener guiListener = handler.getGuiListener();
			if (guiListener != null)
				GuiCounter.listeners.add(guiListener);
		}
	}

	@Override
	public void registerRenderers(FMLPostInitializationEvent event) {
		BufferedRenderer fluidRender = new BufferedRenderer(0.6f, 0.6f, 1.0f);
		BufferedRenderer itemRender = new BufferedRenderer(1.0f, 0.75f, 0.75f);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedItemCounter.class, itemRender);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedFluidCounter.class, fluidRender);
		MinecraftForgeClient.registerItemRenderer(CountCraft.instance.chestItem, itemRender);
		MinecraftForgeClient.registerItemRenderer(CountCraft.instance.tankItem, fluidRender);
	}

}