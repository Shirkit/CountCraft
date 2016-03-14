package com.shirkit.countcraft.network.client;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.integration.ICounterFinder;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * 
 * A packet to send ALL data stored in your extended properties to the client.
 * This is handy if you only need to send your data once per game session or all
 * of your data needs to be synchronized together; it's also handy while first
 * starting, since you only need one packet for everything - however, you should
 * NOT use such a packet in your final product!!!
 * 
 * Each packet should handle one thing and one thing only, in order to minimize
 * network traffic as much as possible. There is no point sending 20+ fields'
 * worth of data when you just need the current mana amount; conversely, it's
 * foolish to send 20 packets for all the data when the player first loads, when
 * you could send it all in one single packet.
 * 
 * TL;DR - make separate packets for each piece of data, and one big packet for
 * those times when you need to send everything.
 *
 */
public class UpdateClientCounterMessage implements IMessage
// remember - the IMessageHandler will be implemented as a static inner class
{
	// Previously, we've been writing each field in our properties one at a
	// time,
	// but that is really annoying, and we've already done it in the save and
	// load
	// NBT methods anyway, so here's a slick way to efficiently send all of your
	// extended data, and no matter how much you add or remove, you'll never
	// have
	// to change the packet / synchronization of your data.

	public static class Handler extends AbstractClientMessageHandler<UpdateClientCounterMessage> {

		@Override
		public IMessage handleClientMessage(EntityPlayer player, UpdateClientCounterMessage message, MessageContext ctx) {

			TileEntity tileEntity = player.worldObj.getTileEntity(message.x, message.y, message.z);
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
				sync.readNBT(message.data);

			return null;
		}
	}
	// this will store our ExtendedPlayer data, allowing us to easily read and
	// write
	private NBTTagCompound data;

	private int x, y, z;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public UpdateClientCounterMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public UpdateClientCounterMessage(int x, int y, int z, NBTTagCompound nbt) {
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