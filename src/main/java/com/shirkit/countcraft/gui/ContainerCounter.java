package com.shirkit.countcraft.gui;

import com.shirkit.countcraft.api.count.ICounter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerCounter extends Container {

	private ICounter counter;

	private TileEntity tile;

	public ContainerCounter(ICounter counter, TileEntity tile) {
		this.counter = counter;
		this.tile = tile;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	public ICounter getCounter() {
		return counter;
	}

	public TileEntity getTile() {
		return tile;
	}

}
