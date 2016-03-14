package com.shirkit.countcraft.api;

import com.shirkit.countcraft.api.count.ICounter;

/**
 * Handles different inputs that a {@link ICounter} can deal with by abstracting
 * all the type-related stuff like metadata for items, direction and sides for
 * energy.
 *
 * @author Shirkit
 *
 */
public interface IStack {

	public static final String energyID = "energy";

	public static final String fluidID = "fluid";

	public static final String itemID = "item";

	public Long getAmount();

	public Object getId();

	public String getIdentifier();

	public String getName();

}
