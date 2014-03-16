package com.shirkit.countcraft.api.count;

import java.util.List;

import com.shirkit.countcraft.api.IStack;

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
	 * This must be called every tick by the container class.
	 */
	public void tick();

	/**
	 * Counts a stack and stores that value.
	 * 
	 * @param stack
	 *            to be counted.
	 */
	public boolean add(IStack stack);

	/**
	 * Retrieves the current items counted inside this {@link ICounter} filled
	 * with implementation instances of {@link IStack}
	 * 
	 * @see IStack
	 */
	public List<IStack> entrySet();

	/**
	 * Gets the total amount of things that were counted by this
	 * {@link ICounter} .
	 */
	public long getTotalCounted();

	/**
	 * Gets the total amount of ticks that this {@link ICounter} has ran.
	 */
	public long getTicksRun();

	/**
	 * Gets the number of things that this counter knows about
	 */
	public int size();

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(IStack)}.
	 */
	public boolean isActive();

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(IStack)}.
	 */
	public void setActive(boolean active);
}
