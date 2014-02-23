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
	public static IconProvider iconProvider = new IconProvider();

	public PipeItemCounter pipeItem;
	public PipeFluidCounter pipeFluid;
	public Item builtPipeItem, builtPipeFluid;

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

		// Item pipe
		pipeItem = new PipeItemCounter(Options.ITEM_PIPEITEMCOUNTER);
		String name = Character.toLowerCase(PipeItemCounter.class.getSimpleName().charAt(0)) + PipeItemCounter.class.getSimpleName().substring(1);

		int id = pipeItem.itemID;
		builtPipeItem = BlockGenericPipe.registerPipe(id, PipeItemCounter.class);
		builtPipeItem.setUnlocalizedName(PipeItemCounter.class.getSimpleName());
		LanguageRegistry.addName(builtPipeItem, "Item Counter Transport Pipe");

		ItemStack pipeItemStack = new ItemStack(builtPipeItem);

		GameRegistry.addShapelessRecipe(pipeItemStack, new ItemStack(ItemCounter.instance.chest), new ItemStack(BuildCraftTransport.pipeItemsCobblestone));

		// Fluid pipe
		pipeFluid = new PipeFluidCounter(Options.ITEM_PIPEFLUIDCOUNTER);
		name = Character.toLowerCase(PipeFluidCounter.class.getSimpleName().charAt(0)) + PipeFluidCounter.class.getSimpleName().substring(1);

		id = pipeFluid.itemID;
		builtPipeFluid = BlockGenericPipe.registerPipe(id, PipeFluidCounter.class);
		builtPipeFluid.setUnlocalizedName(PipeFluidCounter.class.getSimpleName());
		LanguageRegistry.addName(builtPipeFluid, "Fluid Counter Fluid Pipe");

		ItemStack pipeFluidStack = new ItemStack(builtPipeFluid);

		GameRegistry.addShapelessRecipe(pipeFluidStack, new ItemStack(ItemCounter.instance.tank), new ItemStack(BuildCraftTransport.pipeFluidsCobblestone));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForgeClient.registerItemRenderer(builtPipeItem.itemID, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(builtPipeFluid.itemID, TransportProxyClient.pipeItemRenderer);
	}

}
