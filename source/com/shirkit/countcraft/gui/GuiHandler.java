package com.shirkit.countcraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.transport.TileGenericPipe;

import com.shirkit.countcraft.logic.ICounter;

import cpw.mods.fml.common.network.IGuiHandler;

// TODO Need to remove all the references to Buildcraft 
public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			if (!world.blockExists(x, y, z))
				return null;

			TileEntity tile = world.getBlockTileEntity(x, y, z);

			ICounter counter = null;
			if (tile instanceof ICounter)
				counter = (ICounter) tile;
			else if (tile instanceof TileGenericPipe) {
				if (((TileGenericPipe) tile).pipe instanceof ICounter)
					counter = (ICounter) ((TileGenericPipe) tile).pipe;
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

			TileEntity tile = world.getBlockTileEntity(x, y, z);

			ICounter counter = null;
			if (tile instanceof ICounter)
				counter = (ICounter) tile;
			else if (tile instanceof TileGenericPipe) {
				if (((TileGenericPipe) tile).pipe instanceof ICounter)
					counter = (ICounter) ((TileGenericPipe) tile).pipe;
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
