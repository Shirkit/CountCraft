package com.shirkit.countcraft.network;

import com.shirkit.countcraft.logic.ICounter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface ISyncCapable extends ICounter {

	/**
	 * This does not need to return an absolute from all time that this entity
	 * has ran, only since the last launch.
	 * 
	 * @return a long representing how many ticks this entity has ticked.
	 */
	public long getTicksRun();

	public TileEntity getTileEntity();

	public boolean isDirty();

	public void setDirty(boolean dirty);

}
