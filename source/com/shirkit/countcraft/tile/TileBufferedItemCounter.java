package com.shirkit.countcraft.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.Counter;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

public class TileBufferedItemCounter extends TileEntity implements ICounterContainer, ISyncCapable, ISidedInventory, ISideAware {

	// Persistent
	private ItemStack[] inventory = new ItemStack[9];
	private ItemStack[] copy = new ItemStack[9];
	private Counter counter = new Counter();
	private SideController sides = new SideController();

	// Transient
	private long ticksRun;
	private boolean needUpdate = false;

	public TileBufferedItemCounter() {
	}

	// -------------- Counter control
	private void checkForChanges(int slot) {
		ItemStack current = inventory[slot];
		ItemStack old = copy[slot];
		if (current != null && old != null && current.itemID == old.itemID) {
			int sum = current.stackSize - old.stackSize;
			if (sum > 0) {
				counter.add(new ItemHandler(current, sum));
				needUpdate = true;
			}
		} else if (current != null && old == null) {
			counter.add(new ItemHandler(current));
			needUpdate = true;
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
		ItemStack modifying = inventory[slot];
		if (modifying != null) {
			if (modifying.stackSize <= amount) {
				inventory[slot] = null;
				onInventoryChanged();
				return modifying;
			} else {
				modifying = modifying.splitStack(amount);
				onInventoryChanged();
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

		onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return "Buffered Item Counter";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D,
				(double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
	}

	@Override
	public void closeChest() {
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, 1);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
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

	@Override
	public void readNBT(NBTTagCompound reading) {
		NBTTagList nbttaglist = reading.getTagList("Items");
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTag = (NBTTagCompound) nbttaglist.tagAt(i);
			int j = nbtTag.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(nbtTag);
			}
		}

		for (int i = 0; i < inventory.length; i++)
			if (inventory[i] != null)
				copy[i] = inventory[i].copy();

		counter.readFromNBT(reading);
		sides.readFromNBT(reading);
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

		counter.writeToNBT(writing);
		sides.writeToNBT(writing);
	}
}
