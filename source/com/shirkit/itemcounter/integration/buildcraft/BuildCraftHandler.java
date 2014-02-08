package com.shirkit.itemcounter.integration.buildcraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.BuildCraftTransport;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TransportProxyClient;

import com.shirkit.itemcounter.ItemCounter;
import com.shirkit.itemcounter.data.Options;
import com.shirkit.itemcounter.integration.IIntegrationHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BuildCraftHandler implements IIntegrationHandler {

	public static BuildCraftHandler instance;

	public PipeItemCounter pipe;
	public Item builtPipe;
	public IconProvider iconProvider = new IconProvider();

	public BuildCraftHandler() {
		instance = this;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (event.getSide().isClient())
			Localization.addLocalization("/lang/itemcounter/", "en_US");

		pipe = new PipeItemCounter(Options.ITEM_PIPEITEMCOUNTER);
		String name = Character.toLowerCase(PipeItemCounter.class.getSimpleName().charAt(0)) + PipeItemCounter.class.getSimpleName().substring(1);

		int id = pipe.itemID;
		builtPipe = BlockGenericPipe.registerPipe(id, PipeItemCounter.class);
		builtPipe.setUnlocalizedName(PipeItemCounter.class.getSimpleName());
		LanguageRegistry.addName(builtPipe, "Item Counter Transport Pipe");

		ItemStack pipeStack = new ItemStack(builtPipe);

		GameRegistry.addShapelessRecipe(pipeStack, new ItemStack(ItemCounter.instance.chest), new ItemStack(BuildCraftTransport.pipeItemsCobblestone));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForgeClient.registerItemRenderer(builtPipe.itemID, TransportProxyClient.pipeItemRenderer);
	}

}
