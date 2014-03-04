package com.shirkit.countcraft.integration.cc;

import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dan200.computer.api.ComputerCraftAPI;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheralHandler;

public class ComputerCraftHandler implements IIntegrationHandler, IPeripheralHandler {

	public static ComputerCraftHandler instance;

	public ComputerCraftHandler() {
		instance = this;
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		registerPeripheral(TileBufferedFluidCounter.class);
		registerPeripheral(TileBufferedItemCounter.class);
	}

	public static void registerPeripheral(Class<? extends net.minecraft.tileentity.TileEntity> clazz) {
		if (instance != null)
			ComputerCraftAPI.registerExternalPeripheral(clazz, instance);
	}

	@Override
	public ICounterFinder getCounterFinder() {
		return null;
	}

	@Override
	public IGuiListener getGuiListener() {
		return null;
	}

	@Override
	public IHostedPeripheral getPeripheral(TileEntity tile) {
		ICounterContainer container = null;
		if (tile instanceof ICounterContainer)
			container = (ICounterContainer) tile;
		else {
			for (ICounterFinder finder : CountCraft.instance.finders) {
				ICounterContainer containerFrom = finder.getCounterContainerFrom(tile);
				if (containerFrom != null)
					container = containerFrom;
			}
		}
		if (container != null)
			return new CounterPeripheralWrapper(container);
		return null;
	}

}
