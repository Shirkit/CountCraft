package com.shirkit.countcraft.gui;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.ICounterFinder;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			switch (ID) {
			case GuiID.COUNTER_GUI:

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

				return new GuiCounter(counter.getCounter(), tile);

			case GuiID.TEST_GUI:

				if (!world.blockExists(x, y, z))
					return null;

				TileEntity tile2 = world.getTileEntity(x, y, z);

				ICounterContainer counter2 = null;
				if (tile2 instanceof ICounterContainer)
					counter2 = (ICounterContainer) tile2;
				else {
					for (ICounterFinder listener : CountCraft.instance.finders) {
						ICounterContainer te = listener.getCounterContainerFrom(tile2);
						if (te != null) {
							counter2 = te;
							break;
						}
					}
				}

				if (counter2 == null || counter2.getCounter() == null)
					return null;

				return new MyGuiBase(new MyGuiBaseContainer(tile2, player.inventory));

			default:
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			switch (ID) {
			case GuiID.COUNTER_GUI:

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

				return new ContainerCounter(counter.getCounter(), tile);

			case GuiID.TEST_GUI:

				if (!world.blockExists(x, y, z))
					return null;

				TileEntity tile2 = world.getTileEntity(x, y, z);

				ICounterContainer counter2 = null;
				if (tile2 instanceof ICounterContainer)
					counter2 = (ICounterContainer) tile2;
				else {
					for (ICounterFinder listener : CountCraft.instance.finders) {
						ICounterContainer te = listener.getCounterContainerFrom(tile2);
						if (te != null) {
							counter2 = te;
							break;
						}
					}
				}

				if (counter2 == null || counter2.getCounter() == null)
					return null;

				return new MyGuiBaseContainer(tile2, player.inventory);

			default:
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
