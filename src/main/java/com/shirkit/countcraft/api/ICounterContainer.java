package com.shirkit.countcraft.api;

import com.shirkit.countcraft.api.count.ICounter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Any container that holds an instance to a {@link ICounter} must implement
 * this interface so the other layers (i.e. networking, gui, etc.) can work with
 * a high level of abstraction.
 *
 * @author Shirkit
 *
 */
public interface ICounterContainer {

	public void addCounterListener(ICounterListener listener);

	public boolean canAddListeners();

	/**
	 * @return the current instance of this container's {@link ICounter}.
	 */
	public ICounter getCounter();

	/**
	 *
	 * @return the {@link TileEntity} that holds the {@link ICounter}.
	 */
	public TileEntity getTileEntity();

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
