package com.shirkit.countcraft.integration.buildcraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.transport.PipeTransportFluids;

public class MyPipeTransportFluids extends PipeTransportFluids {

	FillerListener listener;

	public MyPipeTransportFluids() {
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int fill = super.fill(from, resource, doFill);
		listener.onFill(fill, resource);
		return fill;
	}

	public interface FillerListener {
		public void onFill(int amountFilled, FluidStack what);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}

}
