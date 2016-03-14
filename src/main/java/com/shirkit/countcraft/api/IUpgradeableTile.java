package com.shirkit.countcraft.api;

import java.util.Collection;

import com.shirkit.countcraft.api.count.ICounter;

public interface IUpgradeableTile extends ICounterContainer {

	public Collection<IUpgrade> getUpgrades();

	public void registerUpgrade(IUpgrade upgrade);

	public void setCounter(ICounter counter);
}
