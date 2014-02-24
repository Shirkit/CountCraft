package com.shirkit.countcraft.integration.buildcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.logic.Counter;
import com.shirkit.countcraft.logic.ICounter;
import com.shirkit.countcraft.logic.ItemHandler;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.utils.SyncUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeItemCounter extends Pipe<PipeTransportItems> implements ICounter, ISyncCapable {

	// Persistent
	private Counter counter;

	// Transient
	private long ticksRun = 0;
	private boolean needUpdate = false;

	public PipeItemCounter(int itemID) {
		super(new PipeTransportItems(), itemID);
		counter = new Counter();
	}

	// -------------- Buildcraft

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return BuildCraftHandler.iconProvider;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return IconProvider.TYPE.PipeItemCounter.ordinal();
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {

		if (container.worldObj.isRemote)
			return true;

		SyncUtils.syncTileEntity(this);

		entityplayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (container.worldObj.isRemote)
			return;

		ticksRun++;
		counter.tick();
		SyncUtils.syncTileEntity(this);
	}

	public void eventHandler(PipeEventItem.Entered event) {
		needUpdate = true;
		counter.add(new ItemHandler(event.item.getItemStack()));
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		counter.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		counter.readFromNBT(data);
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

}
