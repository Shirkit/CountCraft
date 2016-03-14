package com.shirkit.countcraft.integration.te;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ESideState;
import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.EnergyHandler.Kind;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.tile.AbstractTileEntityCounter;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

import cofh.api.energy.IEnergyHandler;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCounterEnergyCell extends AbstractTileEntityCounter implements IEnergyHandler, IUpgradeableTile {

	private int currentEnergy = 0, tickEnergy = 0;
	private long lastTickExtracted = 0;
	private int t0Input, t0Extract, t1Input, t1Extract, t2Input, t2Extract;

	private List<IUpgrade> upgrades = new ArrayList<IUpgrade>();

	public TileCounterEnergyCell() {
		counter = new Counter();
		sides = new SideController(ESideState.Off, false);
	}

	@Override
	public void addCounterListener(ICounterListener listener) {
	}

	@Override
	public boolean canAddListeners() {
		return false;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return !sides.isDisabled(from);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (sides.isOutput(from)) {
			int job = Math.min(maxExtract, currentEnergy);
			if (!simulate && job > 0) {
				currentEnergy -= job;
				counter.add(new EnergyHandler(Kind.REDSTONE_FLUX, job));
				setDirty(true);
				lastTickExtracted = getTicksRun();
				t0Extract += job;
			}
			return job;
		}
		return 0;
	}

	public String getComponentName() {
		return "energyCounter";
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return currentEnergy;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Collection<IUpgrade> getUpgrades() {
		return upgrades;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNBT(nbt);
	}

	@Override
	public void readNBT(NBTTagCompound reading) {
		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
		currentEnergy = reading.getInteger("currentEnergy");

		NBTTagList installed = reading.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < installed.tagCount(); i++) {
			NBTTagCompound tagAt = (NBTTagCompound) installed.getCompoundTagAt(i);
			UpgradeManager.loadUpgrade(tagAt, this);
		}
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (sides.isInput(from) && (tickEnergy == 0 || (getTicksRun() - lastTickExtracted < 3) && (t0Input < Math.max(t1Extract, t2Extract)))) {
			int job = Math.min((Integer.MAX_VALUE - currentEnergy), maxReceive);
			if (!simulate) {
				currentEnergy += job;
				t0Input += job;
			}
			return job;
		}
		return 0;
	}

	@Override
	public void registerUpgrade(IUpgrade upgrade) {
		upgrades.add(upgrade);
	}

	/** IUpgradeableTile **/
	@Override
	public void setCounter(ICounter counter) {
		this.counter = counter;
	}

	@Override
	public void updateEntity() {
		ticksRun++;

		if (worldObj.isRemote)
			return;

		t2Input = t1Input;
		t2Extract = t1Extract;
		t1Input = t0Input;
		t1Extract = t0Extract;
		t0Extract = t0Input = 0;

		counter.tick();

		List<ForgeDirection> outputSides = new ArrayList<>();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (sides.isOutput(dir)) {
				outputSides.add(dir);
			}
		}

		int total = 0;
		// Minor loss due to division (eyh, it's costy !)
		for (ForgeDirection output : outputSides) {
			int inserted = EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, output.ordinal(), currentEnergy, false);
			currentEnergy -= inserted;
			total += inserted;
		}

		if (total > 0) {
			setDirty(true);
			lastTickExtracted = getTicksRun();
			t0Extract += total;
			counter.add(new EnergyHandler(Kind.REDSTONE_FLUX, total));
			SyncUtils.syncTileEntity(this, this);
		}

		tickEnergy = currentEnergy;
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
		writing.setInteger("currentEnergy", currentEnergy);

		NBTTagList installed = new NBTTagList();
		for (IUpgrade up : upgrades) {
			NBTTagCompound compound = new NBTTagCompound();
			up.writeToNBT(compound);
			installed.appendTag(compound);
		}
		writing.setTag("Upgrades", installed);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNBT(nbt);
	}

}
