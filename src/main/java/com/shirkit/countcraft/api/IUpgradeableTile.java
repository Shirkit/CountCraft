package com.shirkit.countcraft.api;

import java.util.Collection;

import com.shirkit.countcraft.api.count.ICounter;

public interface IUpgradeableTile extends ICounterContainer {

	public void setCounter(ICounter counter);

	public void registerUpgrade(IUpgrade upgrade);

	public Collection<IUpgrade> getUpgrades();

}
