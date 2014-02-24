package com.shirkit.countcraft.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.shirkit.countcraft.logic.Counter;
import com.shirkit.countcraft.logic.FluidHandler;
import com.shirkit.countcraft.logic.ICounter;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

public class TileBufferedFluidCounter extends TileEntity implements ICounter, IFluidHandler, ISyncCapable {

	// Persistent
	private Counter counter = new Counter();
	private FluidTank tank = new FluidTank(2000);

	// Transient
	public long ticksRun;
	private boolean needUpdate = false;

	public TileBufferedFluidCounter() {
	}

	// -------------- IFluidHandler
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int filledAmount = tank.fill(resource, doFill);
		if (doFill) {
			counter.add(new FluidHandler(resource, filledAmount));
			needUpdate = true;
		}
		return filledAmount;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (tank.getFluid().isFluidEqual(resource))
			return drain(from, resource.amount, doDrain);

		return new FluidStack(resource, 0);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack drain = tank.drain(maxDrain, doDrain);
		return drain;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (tank.getFluid() == null || tank.getFluid().fluidID == fluid.getID())
			return true;
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (tank.getFluid() != null && tank.getFluid().getFluid().getID() == fluid.getID())
			return true;
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
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
		if (worldObj.isRemote)
			return;
		counter.tick();
		++ticksRun;
		SyncUtils.syncTileEntity(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tank.readFromNBT(nbt);
		counter.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
		counter.writeToNBT(nbt);
	}

	// -------------- ICounter

	@Override
	public Counter getCounter() {
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
}
