package com.shirkit.countcraft.api;

public interface IUpgrade {

	public boolean canApply(IUpgradeableTile tile);

	/**
	 * This will only be called on server side.
	 * 
	 * @param tile
	 */
	public void onApply(IUpgradeableTile tile);

	/**
	 * This will be eventually called on client side when a sync is being done.
	 * 
	 * @param tile
	 */
	public void onLoad(IUpgradeableTile tile);

}