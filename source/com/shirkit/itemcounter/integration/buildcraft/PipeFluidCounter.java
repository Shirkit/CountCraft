package com.shirkit.itemcounter.integration.buildcraft;

import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportFluids;

import com.shirkit.itemcounter.ItemCounter;
import com.shirkit.itemcounter.gui.GuiID;
import com.shirkit.itemcounter.integration.buildcraft.MyPipeTransportFluids.FillerListener;
import com.shirkit.itemcounter.logic.Counter;
import com.shirkit.itemcounter.logic.ICounter;
import com.shirkit.itemcounter.logic.Stack.FluidHandler;
import com.shirkit.itemcounter.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeFluidCounter extends Pipe<PipeTransportFluids> implements ICounter, FillerListener {

	private Counter counter;

	public PipeFluidCounter(int itemID) {
		super(new MyPipeTransportFluids(), itemID);
		counter = new Counter();
	}

	@Override
	public void onBlockPlaced() {
		super.onBlockPlaced();
		((MyPipeTransportFluids) transport).listener = this;
	}

	@Override
	public void onBlockPlacedBy(EntityLivingBase placer) {
		super.onBlockPlacedBy(placer);
		((MyPipeTransportFluids) transport).listener = this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return BuildCraftHandler.iconProvider;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return IconProvider.TYPE.PipeFluidCounter.ordinal();
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

		entityplayer.openGui(ItemCounter.instance, GuiID.COUNTER_GUI, container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		counter.tick();
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

	@Override
	public void onFill(int amountFilled, FluidStack what) {
		counter.add(new FluidHandler(what, amountFilled));
	}

}
