package com.shirkit.countcraft.integration.te;

import net.minecraftforge.client.MinecraftForgeClient;

import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.integration.cc.ComputerCraftHandler;
import com.shirkit.countcraft.render.BufferedRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ThermalExpansionHandler implements IIntegrationHandler {

	public BlockCounterEnergyCell energycell;

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		energycell = new BlockCounterEnergyCell(3959);

		GameRegistry.registerBlock(energycell, ItemBlockCounterEnergyCell.class, "countcraft." + BlockCounterEnergyCell.class.getName());
		GameRegistry.registerTileEntity(TileCounterEnergyCell.class, TileCounterEnergyCell.class.getName());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		if (event.getSide().isClient()) {
			BufferedRenderer energyRender = new BufferedRenderer(1.0f, 0.8f, 0.4f);
			ClientRegistry.bindTileEntitySpecialRenderer(TileCounterEnergyCell.class, energyRender);
			MinecraftForgeClient.registerItemRenderer(energycell.blockID, energyRender);
		}

		ComputerCraftHandler.registerPeripheral(TileCounterEnergyCell.class);
	}

	@Override
	public ICounterFinder getCounterFinder() {
		return null;
	}

	@Override
	public IGuiListener getGuiListener() {
		return null;
	}

}
