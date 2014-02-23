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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.TileFluidHandler;

import com.shirkit.itemcounter.logic.Counter;
import com.shirkit.itemcounter.logic.ICounter;
import com.shirkit.itemcounter.logic.Stack;
import com.shirkit.itemcounter.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileBufferedFluidCounter extends TileEntity implements ICounter, IFluidHandler, ISyncCapable {

	private Counter counter = new Counter();
	private FluidTank tank = new FluidTank(Integer.MAX_VALUE);

	// Server
	public int ticksSinceSync;
	private boolean needUpdate = false;

	public TileBufferedFluidCounter() {
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
		counter.tick();

		++ticksSinceSync;
		float f;

		if (!worldObj.isRemote && (ticksSinceSync) % 2 == 0) {
			f = 5.0F;
			List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(xCoord - f, yCoord - f, zCoord - f, xCoord + 1 + f, yCoord + 1 + f, zCoord + 1 + f));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = (EntityPlayer) iterator.next();
				//if (needUpdate) {
				sendContents(worldObj, entityplayer);
				//}
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
		tank.readFromNBT(nbt);
		counter.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
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

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int filledAmount = tank.fill(resource, doFill);
		if (doFill) {
			counter.add(new Stack.FluidHandler(resource, filledAmount));
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

	@Override
	public int getTicksSinceSync() {
		return ticksSinceSync;
	}
}
