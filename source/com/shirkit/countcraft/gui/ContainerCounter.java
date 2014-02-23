package com.shirkit.countcraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.logic.Counter;

public class ContainerCounter extends Container {

	public ContainerCounter(Counter counter, TileEntity tile) {
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
