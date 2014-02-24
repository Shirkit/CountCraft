package com.shirkit.countcraft.logic;

/**
 * Any container that holds an instance to a {@link Counter} must implement this
 * interface so the other layers (i.e. networking, gui, etc.) can work with a
 * high level of abstraction.
 * 
 * @author Shirkit
 * 
 */
public interface ICounter {

	/**
	 * @return the current instance of this container's {@link Counter}.
	 */
	public Counter getCounter();

}
