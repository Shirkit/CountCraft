package com.shirkit.countcraft.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface ISyncCapable {

	/**
	 * This does not need to return an absolute from all time that this entity
	 * has ran, only since the last launch. This should work (i.e. the increment
	 * should happen) in client side.
	 * 
	 * @return a long representing how many ticks this entity has ticked.
	 */
	public long getTicksRun();

	public boolean isDirty();

	public void setDirty(boolean dirty);



}
