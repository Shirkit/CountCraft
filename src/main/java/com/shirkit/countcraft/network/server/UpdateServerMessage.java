package com.shirkit.countcraft.network.server;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.side.SideController;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class UpdateServerMessage implements IMessage {

	public static class Handler extends AbstractServerMessageHandler<UpdateServerMessage> {
		@Override
		public IMessage handleServerMessage(EntityPlayer player, UpdateServerMessage message, MessageContext ctx) {
			TileEntity entity = player.worldObj.getTileEntity(message.x, message.y, message.z);

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
				if (message.data.hasKey(ICounter.ACTIVE_TAG))
					counter.getCounter().setActive(message.data.getBoolean(ICounter.ACTIVE_TAG));

				if (message.data.hasKey(SideController.SIDES_TAG)) {
					if (entity instanceof ISideAware) {
						ISideAware iSideAware = (ISideAware) entity;
						iSideAware.getSideController().readFromNBT(message.data);
					}
				}

				if (message.data.hasKey(IComplexCounter.COMPLEX_TAG)) {
					if (counter.getCounter() instanceof IComplexCounter) {
						IComplexCounter counter2 = (IComplexCounter) counter.getCounter();
						counter2.setComplex(message.data.getBoolean(IComplexCounter.COMPLEX_TAG));
					}
				}

				player.worldObj.notifyBlocksOfNeighborChange(message.x, message.y, message.z, player.worldObj.getBlock(message.x, message.y, message.z));
			}
			return null;
		}
	}
	private NBTTagCompound data;

	private int x, y, z;

	public UpdateServerMessage() {
	}

	public UpdateServerMessage(int x, int y, int z, NBTTagCompound nbt) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();

		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);

		ByteBufUtils.writeTag(buffer, data);
	}
}