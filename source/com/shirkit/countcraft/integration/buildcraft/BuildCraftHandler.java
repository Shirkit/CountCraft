package com.shirkit.countcraft.integration.buildcraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.BuildCraftTransport;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TransportProxyClient;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.api.integration.INetworkListener;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.data.Options;
import com.shirkit.countcraft.network.ISyncCapable;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class BuildCraftHandler implements IIntegrationHandler, INetworkListener {

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
		// Pipes
		pipeItem = new PipeItemCounter(Options.ITEM_BC_PIPEITEMCOUNTER);
		pipeFluid = new PipeFluidCounter(Options.ITEM_BC_PIPEFLUIDCOUNTER);

		// Pipe items
		builtPipeItem = BlockGenericPipe.registerPipe(pipeItem.itemID, PipeItemCounter.class);
		builtPipeFluid = BlockGenericPipe.registerPipe(pipeFluid.itemID, PipeFluidCounter.class);

		// Overriding the configs
		builtPipeItem.setCreativeTab(CountcraftTab.TAB);
		builtPipeItem.setUnlocalizedName("countcraft.bc.pipeitemcounter");
		GameRegistry.registerItem(builtPipeItem, builtPipeItem.getUnlocalizedName());

		builtPipeFluid.setCreativeTab(CountcraftTab.TAB);
		builtPipeFluid.setUnlocalizedName("countcraft.bc.pipefluidcounter");
		GameRegistry.registerItem(builtPipeFluid, builtPipeFluid.getUnlocalizedName());

		// Recipe
		ItemStack pipeItemStack = new ItemStack(builtPipeItem);
		GameRegistry.addShapelessRecipe(pipeItemStack, new ItemStack(CountCraft.instance.chest), new ItemStack(BuildCraftTransport.pipeItemsCobblestone));

		ItemStack pipeFluidStack = new ItemStack(builtPipeFluid);
		GameRegistry.addShapelessRecipe(pipeFluidStack, new ItemStack(CountCraft.instance.tank), new ItemStack(BuildCraftTransport.pipeFluidsCobblestone));

		// Localization
		for (String language : CountCraft.loadedLocalizations) {
			Localization.addLocalization(CountCraft.LOCALIZATIONS_FOLDER + "buildcraft_", "en_US");
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		if (event.getSide().isClient()) {
			MinecraftForgeClient.registerItemRenderer(builtPipeItem.itemID, TransportProxyClient.pipeItemRenderer);
			MinecraftForgeClient.registerItemRenderer(builtPipeFluid.itemID, TransportProxyClient.pipeItemRenderer);
		}
	}

	@Override
	public ISyncCapable getSyncCapableFrom(World world, TileEntity tileentity) {
		if (tileentity instanceof TileGenericPipe) {
			TileGenericPipe pipe = (TileGenericPipe) tileentity;
			if (pipe.pipe instanceof ISyncCapable)
				return (ISyncCapable) pipe.pipe;
		}
		return null;
	}

	@Override
	public ICounterContainer getCounterContainerFrom(World world, TileEntity tileentity) {
		if (tileentity instanceof TileGenericPipe) {
			TileGenericPipe pipe = (TileGenericPipe) tileentity;
			if (pipe.pipe instanceof ICounterContainer)
				return (ICounterContainer) pipe.pipe;
		}
		return null;
	}

	@Override
	public INetworkListener getNetworkListener() {
		return instance;
	}

	@Override
	public IGuiListener getGuiListener() {
		return null;
	}

}
