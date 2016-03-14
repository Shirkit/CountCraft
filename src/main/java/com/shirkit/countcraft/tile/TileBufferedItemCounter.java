package com.shirkit.countcraft.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class TileBufferedItemCounter extends AbstractTileEntityCounter implements ISidedInventory, IUpgradeableTile, IRedstoneEmitter {

	// Persistent
	private ItemStack[] externalInventory = new ItemStack[27];

	private ItemStack[] internalInventory = new ItemStack[27];

	private List<ICounterListener> listeners = new ArrayList<ICounterListener>();

	private boolean signal = false;

	private List<IUpgrade> upgrades = new ArrayList<IUpgrade>();

	public TileBufferedItemCounter() {
		counter = new Counter();
	}

	// -------------- ISidedInventory

	@Override
	public void addCounterListener(ICounterListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean canAddListeners() {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return sides.isOutput(side) && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return sides.isInput(side) && isItemValidForSlot(slot, itemstack);
	}

	@Override
	public void closeInventory() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack modifying = internalInventory[slot];
		if (modifying != null) {
			if (modifying.stackSize > amount) {
				ItemHandler handler = new ItemHandler(modifying, amount);

				if (counter.add(handler))
					onAdd(handler);

				ItemStack newStack = modifying.splitStack(amount);
				externalInventory[slot].stackSize = modifying.stackSize;
				markDirty();
				return newStack;
			} else {
				ItemHandler handler = new ItemHandler(modifying, modifying.stackSize);

				if (counter.add(handler))
					onAdd(handler);

				internalInventory[slot] = null;
				externalInventory[slot] = null;
				markDirty();
				return modifying;
			}
		}
		return null;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (sides.isDisabled(side))
			return new int[] {};
		else
			return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	}

	public String getComponentName() {
		return "bufferedItemCounter";
	}

	@Override
	public String getInventoryName() {
		return "Buffered Item Counter";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean getSignal() {
		return signal;
	}

	// -------------- IInventory
	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return externalInventory[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public Collection<IUpgrade> getUpgrades() {
		return upgrades;
	}

	// -------------- TileEntity

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return internalInventory[slot] == null || (internalInventory[slot].isItemEqual(internalInventory[slot]));
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false
				: player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	private void onAdd(IStack stack) {
		for (ICounterListener listener : listeners) {
			listener.onAdd(stack);
		}
	}

	// -------------- ICounterContainer

	@Override
	public void openInventory() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNBT(nbt);
	}

	@Override
	public void readNBT(NBTTagCompound reading) {
		NBTTagList nbttaglist = reading.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		this.internalInventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTag = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
			int j = nbtTag.getByte("Slot") & 255;

			if (j >= 0 && j < internalInventory.length) {
				internalInventory[j] = ItemStack.loadItemStackFromNBT(nbtTag);
			}
		}

		NBTTagList installed = reading.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < installed.tagCount(); i++) {
			NBTTagCompound tagAt = (NBTTagCompound) installed.getCompoundTagAt(i);
			UpgradeManager.loadUpgrade(tagAt, this);
		}

		for (int i = 0; i < internalInventory.length; i++)
			if (internalInventory[i] != null)
				externalInventory[i] = internalInventory[i].copy();

		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
		signal = reading.getBoolean("signal");
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
	public void registerUpgrade(IUpgrade upgrade) {
		upgrades.add(upgrade);
	}

	/** IUpgradeableTile **/
	@Override
	public void setCounter(ICounter counter) {
		this.counter = counter;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		ItemStack previous = internalInventory[slot];

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		if (previous != null) {

			if (previous.isItemEqual(stack)) {

				int diff = previous.stackSize - stack.stackSize;

				if (diff > 0) {
					ItemHandler handler = new ItemHandler(stack, diff);
					if (counter.add(handler))
						onAdd(handler);
				}
			} else {
				ItemHandler handler = new ItemHandler(previous, previous.stackSize);
				if (counter.add(handler))
					onAdd(handler);
			}
		}

		internalInventory[slot] = stack.copy();
		externalInventory[slot] = stack.copy();

		markDirty();
	}

	@Override
	public void setSignal(boolean signal) {
		this.signal = signal;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, signal ? 1 : 0, 3);
	}

	// -------------- IRedstoneEmitter

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
	public void writeNBT(NBTTagCompound writing) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < internalInventory.length; ++i) {
			if (internalInventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				internalInventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		writing.setTag("Items", nbttaglist);

		NBTTagList installed = new NBTTagList();
		for (IUpgrade up : upgrades) {
			NBTTagCompound compound = new NBTTagCompound();
			up.writeToNBT(compound);
			installed.appendTag(compound);
		}
		writing.setTag("Upgrades", installed);

		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
		writing.setBoolean("signal", signal);
	}

	// -------------- IIntegrationProvider

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNBT(nbt);
	}

}
