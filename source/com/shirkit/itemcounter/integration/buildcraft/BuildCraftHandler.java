package com.shirkit.itemcounter.integration.buildcraft;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TransportProxyClient;

import com.shirkit.itemcounter.ItemCounter;
import com.shirkit.itemcounter.integration.IIntegrationHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BuildCraftHandler implements IIntegrationHandler {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ItemCounter.instance.iconProvider = new IconProvider();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (event.getSide().isClient())
			Localization.addLocalization("/lang/itemcounter/", "en_US");

		ItemCounter.instance.pipe = new PipeItemCounter(5003);
		String name = Character.toLowerCase(PipeItemCounter.class.getSimpleName().charAt(0)) + PipeItemCounter.class.getSimpleName().substring(1);

		int id = ItemCounter.instance.pipe.itemID;
		ItemCounter.instance.builtPipe = BlockGenericPipe.registerPipe(id, PipeItemCounter.class);
		ItemCounter.instance.builtPipe.setUnlocalizedName(PipeItemCounter.class.getSimpleName());
		LanguageRegistry.addName(ItemCounter.instance.builtPipe, "Item Counter Transport Pipe");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForgeClient.registerItemRenderer(ItemCounter.instance.builtPipe.itemID, TransportProxyClient.pipeItemRenderer);
	}

}
