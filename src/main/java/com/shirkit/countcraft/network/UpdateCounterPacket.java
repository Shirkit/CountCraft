package com.shirkit.countcraft.network;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.side.SideController;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class UpdateCounterPacket extends AbstractPacket {
	
	public int x, y, z;
	public NBTTagCompound tag;
	
	/**
	 * Serialization only
	 */
	public UpdateCounterPacket() {
	}
	
	public UpdateCounterPacket(int x, int y, int z, NBTTagCompound tag) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}
	

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		ByteBufUtils.writeTag(buffer, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.tag = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		TileEntity tileEntity = player.worldObj.getTileEntity(x, y, z);
		ICounterContainer sync = null;

		if (tileEntity instanceof ICounterContainer)
			sync = (ICounterContainer) tileEntity;
		else {
			for (ICounterFinder listener : CountCraft.instance.finders) {
				ICounterContainer te = listener.getCounterContainerFrom(tileEntity);
				if (te != null) {
					sync = te;
					break;
				}
			}
		}
		
		// Prevents throwing an exception when a player just destroyed the
		// block while updating
		if (sync != null)
			sync.readNBT(tag);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		
		TileEntity entity = player.worldObj.getTileEntity(x, y, z);

		ICounterContainer counter = null;
		if (entity instanceof ICounterContainer)
			counter = (ICounterContainer) entity;
		else {
			for (ICounterFinder listener : CountCraft.instance.finders) {
				ICounterContainer te = listener.getCounterContainerFrom(entity);
				if (te != null) {
					counter = te;
					break;
				}
			}
		}

		
		if (counter != null && counter.getCounter() != null) {
			if (tag.hasKey(ICounter.ACTIVE_TAG))
				counter.getCounter().setActive(tag.getBoolean(ICounter.ACTIVE_TAG));

			if (tag.hasKey(SideController.SIDES_TAG)) {
				if (entity instanceof ISideAware) {
					ISideAware iSideAware = (ISideAware) entity;
					iSideAware.getSideController().readFromNBT(tag);
				}
			}
			
			if (tag.hasKey(IComplexCounter.COMPLEX_TAG)) {
				if (counter.getCounter() instanceof IComplexCounter) {
					IComplexCounter counter2 = (IComplexCounter) counter.getCounter();
					counter2.setComplex(tag.getBoolean(IComplexCounter.COMPLEX_TAG));
				}
			}
			
			player.worldObj.notifyBlocksOfNeighborChange(x, y, z, player.worldObj.getBlock(x, y, z));
		}
	}
}
