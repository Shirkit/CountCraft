package com.shirkit.countcraft.integration.te;

import com.shirkit.countcraft.integration.IIntegrationHandler;

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
	}

}
