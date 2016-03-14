package com.shirkit.countcraft.api.count;

import java.util.List;

import com.shirkit.countcraft.api.IStack;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Handles the counting of stuff that wants to be counted. Uses an abstraction
 * layer of {@link IStack} to handle different things like Items, Fluids and
 * Energy.
 *
 * @author Shirkit
 *
 */
public interface ICounter {

	public static final String ACTIVE_TAG = "active";

	/**
	 * Counts a stack and stores that value.
	 *
	 * @param stack
	 *            to be counted.
	 */
	public boolean add(IStack stack);

	/**
	 * Retrieves the current items counted inside this {@link ICounter} filled
	 * with implementation instances of {@link IStack}. Multiple calls per tick
	 * are not advised, cache it once per tick.
	 *
	 * @see IStack
	 */
	public List<IStack> entrySet();

	/**
	 * Gets the total amount of ticks that this {@link ICounter} has ran.
	 */
	public long getTicksRun();

	/**
	 * Gets the total amount of things that were counted by this
	 * {@link ICounter} .
	 */
	public long getTotalCounted();

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(IStack)}.
	 */
	public boolean isActive();

	/**
	 * Read NBTTag data to the counter
	 *
	 * @param data
	 */
	public void readFromNBT(NBTTagCompound data);

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(IStack)}.
	 */
	public void setActive(boolean active);

	/**
	 * Gets the number of things that this counter knows about
	 */
	public int size();

	/**
	 * This must be called every tick by the container class.
	 */
	public void tick();

	/**
	 * Write counter data to the NBTTag
	 *
	 * @param data
	 */
	public void writeToNBT(NBTTagCompound data);
}
