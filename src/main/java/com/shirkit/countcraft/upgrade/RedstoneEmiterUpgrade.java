package com.shirkit.countcraft.upgrade;

import net.minecraft.nbt.NBTTagCompound;

import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.tile.IRedstoneEmitter;

public class RedstoneEmiterUpgrade implements IUpgrade, ICounterListener {

	private int additions = 0;
	private IRedstoneEmitter emitter;

	@Override
	public boolean canApply(IUpgradeableTile tile) {
		for (IUpgrade u : tile.getUpgrades())
			if (u instanceof RedstoneEmiterUpgrade)
				return false;
		return tile.canAddListeners() && (tile instanceof IRedstoneEmitter || tile.getTileEntity() instanceof IRedstoneEmitter);
	}

	@Override
	public void onApply(IUpgradeableTile tile) {
		if (tile instanceof IRedstoneEmitter)
			emitter = (IRedstoneEmitter) tile;
		else
			emitter = (IRedstoneEmitter) tile.getTileEntity();
		tile.addCounterListener(this);
	}

	@Override
	public void onAdd(IStack stack) {
		if (additions % 5 == 0) {
			emitter.setSignal(true);
		} else {
			emitter.setSignal(false);
		}
		additions++;
	}

	@Override
	public void writeToNBT(NBTTagCompound writing) {
	}

	@Override
	public void readFromNBT(NBTTagCompound writing) {
	}

}