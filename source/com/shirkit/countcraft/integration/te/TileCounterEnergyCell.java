package com.shirkit.countcraft.integration.te;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import shirkit.cofh.util.EnergyHelper;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

import com.shirkit.countcraft.count.Counter;
import com.shirkit.countcraft.count.EnergyHandler;
import com.shirkit.countcraft.count.EnergyHandler.Kind;
import com.shirkit.countcraft.count.ICounterContainer;
import com.shirkit.countcraft.logic.ISideAware;
import com.shirkit.countcraft.logic.SideController;
import com.shirkit.countcraft.logic.SideState;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

public class TileCounterEnergyCell extends TileEntity implements IEnergyHandler, ICounterContainer, ISyncCapable, ISideAware {

	protected EnergyStorage storage = new EnergyStorage(32000);
	private Counter counter = new Counter();
	private int ticksRun;
	private SideController sides = new SideController(SideState.Off, false);

	private boolean needUpdate = false;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
		readNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		writeNBT(nbt);
	}

	@Override
	public void readNBT(NBTTagCompound reading) {
		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
	}

	@Override
	public void updateEntity() {
		ticksRun++;

		if (worldObj.isRemote)
			return;

		counter.tick();

		int extracted = storage.extractEnergy(storage.getEnergyStored(), false);
		int inserted = 0;

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (sides.isOutput(dir) && extracted > 0) {
				int job = EnergyHelper.insertEnergyIntoAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), extracted, false);
				extracted -= job;
				inserted += job;
				needUpdate = true;
			}
		}

		counter.add(new EnergyHandler(Kind.REDSTONE_FLUX, inserted));
		storage.receiveEnergy(extracted, false);
		SyncUtils.syncTileEntity(this);
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (sides.isInput(from)) {
			int added = storage.receiveEnergy(maxReceive, simulate);
			return added;
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (sides.isOutput(from)) {
			int removed = storage.extractEnergy(maxExtract, simulate);
			counter.add(new EnergyHandler(Kind.REDSTONE_FLUX, removed));
			needUpdate = true;
			return removed;
		}
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return !sides.isDisabled(from);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public Counter getCounter() {
		return counter;
	}

	public void sendContents(World world, EntityPlayer player) {
	}

	@Override
	public SideController getSideController() {
		return sides;
	}

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
