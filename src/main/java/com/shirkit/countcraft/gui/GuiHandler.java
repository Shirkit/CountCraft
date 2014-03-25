package com.shirkit.countcraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.ICounterFinder;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			if (!world.blockExists(x, y, z))
				return null;

			TileEntity tile = world.getTileEntity(x, y, z);

			ICounterContainer counter = null;
			if (tile instanceof ICounterContainer)
				counter = (ICounterContainer) tile;
			else {
				for (ICounterFinder listener : CountCraft.instance.finders) {
					ICounterContainer te = listener.getCounterContainerFrom(tile);
					if (te != null) {
						counter = te;
						break;
					}
				}
			}

			if (counter == null || counter.getCounter() == null)
				return null;

			switch (ID) {
			case GuiID.COUNTER_GUI:
				return new ContainerCounter(counter.getCounter(), tile);

			default:
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			if (!world.blockExists(x, y, z))
				return null;

			TileEntity tile = world.getTileEntity(x, y, z);

			ICounterContainer counter = null;
			if (tile instanceof ICounterContainer)
				counter = (ICounterContainer) tile;
			else {
				for (ICounterFinder listener : CountCraft.instance.finders) {
					ICounterContainer te = listener.getCounterContainerFrom(tile);
					if (te != null) {
						counter = te;
						break;
					}
				}
			}

			if (counter == null || counter.getCounter() == null)
				return null;
			
			switch (ID) {
			case GuiID.COUNTER_GUI:
				return new GuiCounter(counter.getCounter(), tile);

			default:
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
