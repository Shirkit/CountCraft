package com.shirkit.countcraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.api.count.ICounter;

public class ContainerCounter extends Container {

	private ICounter counter;
	private TileEntity tile;

	public ContainerCounter(ICounter counter, TileEntity tile) {
		this.counter = counter;
		this.tile = tile;
	}

	public TileEntity getTile() {
		return tile;
	}

	public ICounter getCounter() {
		return counter;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
