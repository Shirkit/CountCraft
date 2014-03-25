package com.shirkit.countcraft.api;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.upgrade.Upgrade;

public interface IUpgradeableTile extends ICounterContainer {

	public void setCounter(ICounter counter);

	public void registerUpgrade(IUpgrade upgrade);

	public Collection<IUpgrade> getUpgrades();
}
