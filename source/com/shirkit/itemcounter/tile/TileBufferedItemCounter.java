package com.shirkit.itemcounter.tile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.shirkit.itemcounter.logic.Counter;
import com.shirkit.itemcounter.logic.ICounter;
import com.shirkit.itemcounter.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileBufferedItemCounter extends TileEntity implements ICounter, IInventory {

	private ItemStack[] inventory = new ItemStack[9];
	private ItemStack[] copy = new ItemStack[9];
	private Counter counter = new Counter();

	// Animation
	public int numUsingPlayers;

	// Server
	public int ticksSinceSync;
	private boolean needUpdate = false;

	public TileBufferedItemCounter() {
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}

	private void checkForChanges(int slot) {
		ItemStack current = inventory[slot];
		ItemStack old = copy[slot];
		if (current != null && old != null && current.itemID == old.itemID) {
			int sum = current.stackSize - old.stackSize;
			if (sum > 0) {
				counter.addItem(current.itemID, current.getItemDamage(), sum);
				needUpdate = true;
			}
		} else if (current != null && old == null) {
			counter.addItem(current);
			needUpdate = true;
		}
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
	public boolean receiveClientEvent(int event, int value) {
		if (event == 1) {
			this.numUsingPlayers = value;
			return true;
		} else {
			return super.receiveClientEvent(event, value);
		}
	}

	@Override
	public void openChest() {
		if (this.numUsingPlayers < 0) {
			this.numUsingPlayers = 0;
		}

		++this.numUsingPlayers;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
	}

	@Override
	public void closeChest() {
		--this.numUsingPlayers;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		counter.tick();

		++ticksSinceSync;
		float f;

		if (!worldObj.isRemote && (ticksSinceSync) % 20 == 0) {
			numUsingPlayers = 0;
			f = 5.0F;
			List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(xCoord - f, yCoord - f, zCoord - f, xCoord + 1 + f, yCoord + 1 + f, zCoord + 1 + f));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = (EntityPlayer) iterator.next();

				if (entityplayer.openContainer instanceof ContainerChest) {
					IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this) {
						++this.numUsingPlayers;
					}
				}
				if (needUpdate) {
					sendContents(worldObj, entityplayer);
				}
			}
			needUpdate = false;
		}
	}

	@Override
	public Counter getCounter() {
		return counter;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items");
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTag = (NBTTagCompound) nbttaglist.tagAt(i);
			int j = nbtTag.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(nbtTag);
			}
		}
		counter.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inventory.length; ++i) {
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbt.setTag("Items", nbttaglist);

		counter.writeToNBT(nbt);
	}

	public void sendContents(World world, EntityPlayer player) {
		NBTTagCompound tag = new NBTTagCompound();
		counter.writeToNBT(tag);

		UpdateClientPacket update = new UpdateClientPacket(xCoord, yCoord, zCoord, tag);

		try {
			Packet toSend = update.getPacket();
			PacketDispatcher.sendPacketToPlayer(toSend, (Player) player);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
