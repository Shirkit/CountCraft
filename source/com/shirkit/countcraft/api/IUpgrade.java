package com.shirkit.countcraft.api;

import java.io.Serializable;

import net.minecraft.nbt.NBTTagCompound;

public interface IUpgrade {

	public boolean canApply(IUpgradeableTile tile);

	/**
	 * This will only be called on server side.
	 * 
	 * @param tile
	 */
	public void onApply(IUpgradeableTile tile);

	public void writeToNBT(NBTTagCompound writing);

	public void readFromNBT(NBTTagCompound writing);

}