package com.shirkit.countcraft.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

public class TileBufferedFluidCounter extends TileEntity implements ICounterContainer, IFluidHandler, ISyncCapable, ISideAware {

	// Persistent
	private Counter counter = new Counter();
	private FluidTank tank = new FluidTank(16000);
	private SideController sides = new SideController();

	// Transient
	public long ticksRun;
	private boolean needUpdate = false;

	public TileBufferedFluidCounter() {
	}

	// -------------- IFluidHandler
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (sides.isInput(from)) {
			int filledAmount = tank.fill(resource, doFill);
			if (doFill) {
				counter.add(new FluidHandler(resource, filledAmount));
				needUpdate = true;
			}
			return filledAmount;
		}

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (sides.isOutput(from))
			if (resource == null || tank.getFluid().isFluidEqual(resource))
				return drain(from, resource.amount, doDrain);

		return new FluidStack(resource, 0);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (sides.isOutput(from))
			return tank.drain(maxDrain, doDrain);

		return new FluidStack(tank.getFluid(), 0);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (sides.isInput(from))
			return tank.getFluid() == null || tank.getFluid().fluidID == fluid.getID();

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (sides.isOutput(from))
			return tank.getFluid() != null && tank.getFluid().getFluid().getID() == fluid.getID();

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (sides.isDisabled(from))
			return new FluidTankInfo[] {};
		else
			return new FluidTankInfo[] { tank.getInfo() };
	}

	// -------------- TileEntity

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
		SyncUtils.syncTileEntity(this, this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNBT(nbt);
	}

	// -------------- ICounterContainer

	@Override
	public ICounter getCounter() {
		return counter;
	}

	// -------------- ISyncCapable
	@Override
	public long getTicksRun() {
		return ticksRun;
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
	}

	@Override
	public boolean isDirty() {
		return needUpdate;
	}

	@Override
	public void setDirty(boolean dirty) {
		needUpdate = dirty;
	}

	@Override
	public SideController getSideController() {
		return sides;
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		tank.writeToNBT(writing);
		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
	}

	@Override
	public void readNBT(NBTTagCompound reading) {
		tank.readFromNBT(reading);
		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
	}
}
