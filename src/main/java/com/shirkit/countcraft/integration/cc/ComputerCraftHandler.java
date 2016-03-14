package com.shirkit.countcraft.integration.cc;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ComputerCraftHandler implements IIntegrationHandler, IPeripheralProvider {

	public static ComputerCraftHandler instance;

	public ComputerCraftHandler() {
		instance = this;
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
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);

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

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		ComputerCraftAPI.registerPeripheralProvider(instance);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {

	}

}
