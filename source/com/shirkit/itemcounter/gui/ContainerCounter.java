package com.shirkit.itemcounter.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import com.shirkit.itemcounter.logic.Counter;

public class ContainerCounter extends Container {

	public ContainerCounter(Counter counter) {
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
