package com.shirkit.countcraft.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

public class TileBufferedItemCounter extends TileEntity implements ICounterContainer, ISyncCapable, ISidedInventory, ISideAware, IUpgradeableTile, IRedstoneEmitter {

	// Persistent
	private ItemStack[] inventory = new ItemStack[9];
	private ItemStack[] copy = new ItemStack[9];
	private ICounter counter = new Counter();
	private SideController sides = new SideController();
	private List<IUpgrade> upgrades = new ArrayList<IUpgrade>();
	private boolean signal = false;
	private List<ICounterListener> listeners = new ArrayList<ICounterListener>();

	// Transient
	private long ticksRun;
	private boolean needUpdate = false;

	public TileBufferedItemCounter() {
	}

	// -------------- Counter control
	private void checkForChanges(int slot) {
		ItemStack current = inventory[slot];
		ItemStack old = copy[slot];
		if (current != null && old != null && current.isItemEqual(old)) {
			int sum = current.stackSize - old.stackSize;
			if (sum > 0) {
				ItemHandler handler = new ItemHandler(current, sum);
				if (counter.add(handler)) {
					needUpdate = true;
					onAdd(handler);
				}
			}
		} else if (current != null && old == null) {
			ItemHandler handler = new ItemHandler(current);
			if (counter.add(handler)) {
				onAdd(handler);
				needUpdate = true;
			}
		}
	}

	// -------------- ISideAware

	@Override
	public SideController getSideController() {
		return sides;
	}

	// -------------- ISidedInventory

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (sides.isDisabled(side))
			return new int[] {};
		else
			return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		if (sides.isInput(side))
			return inventory[slot] == null || (inventory[slot].isItemEqual(inventory[slot]));
		else
			return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		if (sides.isOutput(side))
			return inventory[slot] != null && (inventory[slot].isItemEqual(inventory[slot]));
		else
			return false;
	}

	// -------------- IInventory
	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		checkForChanges(slot);
		if (inventory[slot] != null)
			copy[slot] = inventory[slot].copy();
		else
			copy[slot] = null;
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack modifying = getStackInSlot(slot);
		if (modifying != null) {
			if (modifying.stackSize <= amount) {
				inventory[slot] = null;
				// onInventoryChanged();
				return modifying;
			} else {
				modifying = modifying.splitStack(amount);
				// onInventoryChanged();
			}
		}
		return modifying;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		checkForChanges(slot);

		inventory[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		// onInventoryChanged();
	}

	@Override
	public String getInventoryName() {
		return "Buffered Item Counter";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
				(double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.blockType, 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.blockType);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.blockType);
	}

	@Override
	public void closeInventory() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.blockType, 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.blockType);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.blockType);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return inventory[slot] == null || (inventory[slot].isItemEqual(inventory[slot]));
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

	@Override
	public boolean canAddListeners() {
		return true;
	}

	@Override
	public void addCounterListener(ICounterListener listener) {
		listeners.add(listener);
	}

	private void onAdd(IStack stack) {
		for (ICounterListener listener : listeners) {
			listener.onAdd(stack);
		}
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
	public void readNBT(NBTTagCompound reading) {
		NBTTagList nbttaglist = (NBTTagList) reading.getTag("Items");
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTag = nbttaglist.getCompoundTagAt(i);
			int j = nbtTag.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(nbtTag);
			}
		}

		NBTTagList installed = (NBTTagList) reading.getTag("Upgrades");
		for (int i = 0; i < installed.tagCount(); i++) {
			NBTTagCompound tagAt = (NBTTagCompound) installed.getCompoundTagAt(i);
			UpgradeManager.loadUpgrade(tagAt, this);
		}

		for (int i = 0; i < inventory.length; i++)
			if (inventory[i] != null)
				copy[i] = inventory[i].copy();

		((Counter) counter).readFromNBT(reading);
		sides.readFromNBT(reading);
		signal = reading.getBoolean("signal");
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inventory.length; ++i) {
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(nbttagcompound1);
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

		((Counter) counter).writeToNBT(writing);
		sides.writeToNBT(writing);
		writing.setBoolean("signal", signal);
	}

	// -------------- IUpgradeableTile
	@Override
	public void setCounter(ICounter counter) {
		this.counter = counter;
	}

	@Override
	public void registerUpgrade(IUpgrade upgrade) {
		upgrades.add(upgrade);
	}

	public Collection<IUpgrade> getUpgrades() {
		return upgrades;
	}

	// -------------- IRedstoneEmitter

	@Override
	public void setSignal(boolean signal) {
		this.signal = signal;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, signal ? 1 : 0, 3);
	}

	@Override
	public boolean getSignal() {
		return signal;
	}
}
