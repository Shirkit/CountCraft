package com.shirkit.countcraft.logic;


/**
 * Handles different inputs that a {@link Counter} can deal with by abstracting
 * all the type-related stuff like metadata for items, direction and sides for
 * energy.
 * 
 * @author Shirkit
 * 
 */
public interface Stack {

	public static final String itemID = "item";
	public static final String fluidID = "fluid";
	public static final String energyID = "energy";

	public String getIdentifier();

	public Integer getAmount();

	public Object getId();

	public String getName();

	public Object getStack();

}
