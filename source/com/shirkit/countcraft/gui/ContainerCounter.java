package com.shirkit.countcraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.count.Counter;

public class ContainerCounter extends Container {

	private Counter counter;
	private TileEntity tile;

	public ContainerCounter(Counter counter, TileEntity tile) {
		this.counter = counter;
		this.tile = tile;
	}

	public TileEntity getTile() {
		return tile;
	}

	public Counter getCounter() {
		return counter;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
