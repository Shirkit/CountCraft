package com.shirkit.countcraft.upgrade;

import java.util.List;

import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.TimedCounter;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;

import net.minecraft.nbt.NBTTagCompound;

public class TimerUpgrade implements IUpgrade {

	@Override
	public boolean canApply(IUpgradeableTile tile) {
		for (IUpgrade u : tile.getUpgrades())
			if (u instanceof TimerUpgrade)
				return false;
		return !(tile.getCounter() instanceof IComplexCounter);
	}

	@Override
	public void onApply(IUpgradeableTile tile) {
		ICounter current = tile.getCounter();
		ICounter newOne = new TimedCounter();

		List<IStack> set = current.entrySet();
		for (IStack stack : set)
			newOne.add(stack);

		tile.setCounter(newOne);
	}

	@Override
	public void readFromNBT(NBTTagCompound writing) {
	}

	@Override
	public void writeToNBT(NBTTagCompound writing) {
		writing.setString("class", this.getClass().getName());
	}
}
