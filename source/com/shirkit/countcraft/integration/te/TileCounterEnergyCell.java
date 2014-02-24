package com.shirkit.countcraft.integration.te;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import shirkit.cofh.util.EnergyHelper;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;

import com.shirkit.countcraft.gui.ContainerCounter;
import com.shirkit.countcraft.logic.Counter;
import com.shirkit.countcraft.logic.EnergyHandler;
import com.shirkit.countcraft.logic.ICounter;
import com.shirkit.countcraft.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileCounterEnergyCell extends TileEntity implements IEnergyHandler, ICounter {

	protected EnergyStorage storage = new EnergyStorage(32000);
	private Counter counter = new Counter();
	public int ticksSinceSync;

	private boolean needUpdate = false;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
		counter.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		counter.writeToNBT(nbt);
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;

		counter.tick();

		int extracted = storage.extractEnergy(storage.getEnergyStored(), false);
		int inserted = EnergyHelper.insertEnergyIntoAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), extracted, false);

		counter.add(new EnergyHandler("rf", "out", "bottom", inserted));

		storage.receiveEnergy(extracted - inserted, false);

		needUpdate = true;

		++ticksSinceSync;
		float f;

		if (needUpdate && ticksSinceSync % 2 == 0) {
			f = 5.0F;
			List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(xCoord - f, yCoord - f, zCoord - f, xCoord + 1 + f, yCoord + 1 + f, zCoord + 1 + f));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {

				EntityPlayer entityplayer = (EntityPlayer) iterator.next();

				if (entityplayer.openContainer instanceof ContainerCounter)
					sendContents(worldObj, entityplayer);
			}

			needUpdate = false;
		}
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (from.equals(ForgeDirection.UP)) {
			int added = storage.receiveEnergy(maxReceive, simulate);
			counter.add(new EnergyHandler("rf", "in", "top", added));
			needUpdate = true;
			return added;
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (from.equals(ForgeDirection.DOWN)) {
			int removed = storage.extractEnergy(maxExtract, simulate);
			counter.add(new EnergyHandler("rf", "out", "bottom", removed));
			needUpdate = true;
			return removed;
		}
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		switch (from) {
		case UP:
		case DOWN:
			return true;

		default:
			return false;
		}
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
