package com.shirkit.countcraft.tile;

import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.utils.SyncUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBufferedFluidCounter extends AbstractTileEntityCounter implements IFluidHandler {

	private static final int BUCKET_CAPACITY = Integer.MAX_VALUE;

	private long lastTickExtracted = 0;

	private int t0Input, t0Extract, t1Input, t1Extract, t2Input, t2Extract;
	private FluidTank tank = new FluidTank(BUCKET_CAPACITY);
	private int tickFluid = 0;

	public TileBufferedFluidCounter() {
		counter = new Counter();
	}

	@Override
	public void addCounterListener(ICounterListener listener) {
	}

	@Override
	public boolean canAddListeners() {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (sides.isOutput(from))
			return tank.getFluid() != null && tank.getFluid().getFluidID() == fluid.getID();

		return false;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (sides.isInput(from))
			return tank.getFluid() == null || tank.getFluid().getFluidID() == fluid.getID();

		return false;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (sides.isOutput(from))
			if (resource == null || tank.getFluid().isFluidEqual(resource)) {
				return drain(from, resource.amount, doDrain);
			}

		return new FluidStack(resource, 0);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (sides.isOutput(from)) {
			FluidStack drained = tank.drain(maxDrain, doDrain);
			if (drained != null && drained.amount > 0) {
				t0Extract += drained.amount;
				lastTickExtracted = getTicksRun();
			}
			return drained;
		}

		return new FluidStack(tank.getFluid(), 0);
	}

	// -------------- TileEntity

	// -------------- IFluidHandler
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (sides.isInput(from)) {
			int filledAmount = 0;

			if (tickFluid == 0)
				filledAmount = tank.fill(resource, doFill);

			else if (getTicksRun() - lastTickExtracted < 3 && t0Input < Math.max(t1Extract, t2Extract)) {

				FluidStack stack = resource.copy();
				stack.amount = Math.min(Math.min(t1Extract - tank.getFluidAmount(), t2Extract - tank.getFluidAmount()), resource.amount);

				if (stack.amount > 0)
					filledAmount = tank.fill(stack, doFill);
			}

			if (doFill) {
				t0Input += filledAmount;
				counter.add(new FluidHandler(resource, filledAmount));
				setDirty(true);
			}
			return filledAmount;
		}

		return 0;
	}

	public String getComponentName() {
		return "bufferedFluidCounter";
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (sides.isDisabled(from))
			return new FluidTankInfo[] {};
		else
			return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNBT(nbt);
	}

	// -------------- ICounterContainer

	@Override
	public void readNBT(NBTTagCompound reading) {
		tank.readFromNBT(reading);
		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
	}

	@Override
	public boolean receiveClientEvent(int event, int value) {
		if (event == 1) {
			return true;
		} else {
			return super.receiveClientEvent(event, value);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		ticksRun++;
		if (worldObj.isRemote)
			return;
		counter.tick();

		t2Input = t1Input;
		t2Extract = t1Extract;
		t1Input = t0Input;
		t1Extract = t0Extract;
		t0Extract = t0Input = 0;

		// System.out.println("Current: " + tank.getFluidAmount() + " - t2Input:
		// " + t2Input + " - t2Extract: " + t2Extract + " - t1Input: " + t1Input
		// + " - t1Extract: " + t1Extract);

		tickFluid = tank.getFluidAmount();

		SyncUtils.syncTileEntity(this, this);
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		tank.writeToNBT(writing);
		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
	}

	// -------------- IIntegrationProvider

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNBT(nbt);
	}

}
