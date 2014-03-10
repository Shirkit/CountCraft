package com.shirkit.countcraft.integration.buildcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.count.Counter;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.integration.buildcraft.MyPipeTransportFluids.FillerListener;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeFluidCounter extends Pipe<MyPipeTransportFluids> implements ICounterContainer, FillerListener, ISyncCapable {

	// Persistent
	private Counter counter;

	// Transient
	private boolean needUpdate = false;
	private long ticksRun;

	public PipeFluidCounter(int itemID) {
		super(new MyPipeTransportFluids(), itemID);
		counter = new Counter();
	}

	// -------------- FluidHandling

	@Override
	public void setTile(TileEntity tile) {
		super.setTile(tile);
		((MyPipeTransportFluids) transport).listener = this;
	}

	@Override
	public void onFill(int amountFilled, FluidStack what) {
		counter.add(new FluidHandler(what, amountFilled));
		needUpdate = true;
	}

	// -------------- Buildcraft
	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ProxyClient.iconProvider;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return IconProvider.TYPE.PipeFluidCounter.ordinal();
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {

		if (container.worldObj.isRemote)
			return true;

		SyncUtils.sendCounterUpdatePacket(this, entityplayer);
		entityplayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (container.worldObj.isRemote)
			return;

		counter.tick();
		ticksRun++;
		SyncUtils.syncTileEntity(this, this);
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		writeNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		readNBT(data);
	}

	// -------------- ICounterContainer

	@Override
	public Counter getCounter() {
		return counter;
	}

	@Override
	public long getTicksRun() {
		return ticksRun;
	}

	@Override
	public TileEntity getTileEntity() {
		return this.container;
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
		counter.readFromNBT(reading);
	}

	@Override
	public void writeNBT(NBTTagCompound writing) {
		counter.writeToNBT(writing);
	}
}
