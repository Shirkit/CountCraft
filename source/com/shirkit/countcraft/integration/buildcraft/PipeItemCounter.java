package com.shirkit.countcraft.integration.buildcraft;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.logic.Counter;
import com.shirkit.countcraft.logic.ICounter;
import com.shirkit.countcraft.logic.Stack;
import com.shirkit.countcraft.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeItemCounter extends Pipe implements ICounter {

	private Counter counter;

	public PipeItemCounter(int itemID) {
		super(new PipeTransportItems(), itemID);
		counter = new Counter();
	}

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

		NBTTagCompound tag = new NBTTagCompound();
		counter.writeToNBT(tag);

		UpdateClientPacket packet = new UpdateClientPacket(container.xCoord, container.yCoord, container.zCoord, tag);
		try {
			PacketDispatcher.sendPacketToPlayer(packet.getPacket(), (Player) entityplayer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		entityplayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		counter.tick();
	}

	public void eventHandler(PipeEventItem.Entered event) {
		counter.add(new Stack.ItemHandler(event.item.getItemStack()));
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

	@Override
	public Counter getCounter() {
		return counter;
	}

}
