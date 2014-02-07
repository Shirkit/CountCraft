package com.shirkit.itemcounter.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.transport.TileGenericPipe;

import com.shirkit.itemcounter.integration.buildcraft.PipeItemCounter;
import com.shirkit.itemcounter.logic.Counter;
import com.shirkit.itemcounter.logic.ICounter;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			if (!world.blockExists(x, y, z))
				return null;

			TileEntity tile = world.getBlockTileEntity(x, y, z);

			Counter counter = null;
			if (tile instanceof ICounter)
				counter = ((ICounter) tile).getCounter();
			else if (tile instanceof TileGenericPipe)
				if (((TileGenericPipe) tile).pipe instanceof PipeItemCounter)
					counter = ((ICounter) ((TileGenericPipe) tile).pipe).getCounter();

			if (counter == null)
				return null;

			switch (ID) {
			case GuiID.COUNTER_GUI:
				return new ContainerCounter(counter);

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

			Counter counter = null;
			if (tile instanceof ICounter)
				counter = ((ICounter) tile).getCounter();
			else if (tile instanceof TileGenericPipe)
				if (((TileGenericPipe) tile).pipe instanceof PipeItemCounter)
					counter = ((ICounter) ((TileGenericPipe) tile).pipe).getCounter();

			if (counter == null)
				return null;

			switch (ID) {
			case GuiID.COUNTER_GUI:
				return new GuiCounter(counter);

			default:
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
