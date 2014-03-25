package com.shirkit.countcraft.upgrade;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.TimedCounter;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;

public class TimerUpgrade implements IUpgrade {

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
	public boolean canApply(IUpgradeableTile tile) {
		// Checks also if the tile is already upgraded
		return !(tile.getCounter() instanceof IComplexCounter);
	}

	@Override
	public void writeToNBT(NBTTagCompound writing) {
	}

	@Override
	public void readFromNBT(NBTTagCompound writing) {
	}
}
