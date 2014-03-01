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

	public TileEntity getTileEntity();

	public boolean isDirty();

	public void setDirty(boolean dirty);

	/**
	 * Only reads stuff necessary to build the counter and update the mod's area
	 * around a block. This is necessary for integration when the tileEntity was
	 * extented from complex classes.
	 * 
	 * @param nbttagcompound
	 */
	public void readNBT(NBTTagCompound reading);

	/**
	 * Only writes stuff necessary to save the counter and update the mod's area
	 * around a block. This is necessary for integration when the tileEntity was
	 * extented from complex classes.
	 * 
	 * @param nbttagcompound
	 */
	public void writeNBT(NBTTagCompound writing);

}
