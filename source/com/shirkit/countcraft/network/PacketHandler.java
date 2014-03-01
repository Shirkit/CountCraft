package com.shirkit.countcraft.network;

import java.io.IOException;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildcraft.transport.TileGenericPipe;

import com.shirkit.countcraft.count.Counter;
import com.shirkit.countcraft.count.ICounterContainer;
import com.shirkit.countcraft.logic.ISideAware;
import com.shirkit.countcraft.logic.SideController;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

//TODO Need to remove all the references to Buildcraft
public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (player instanceof EntityClientPlayerMP) {
			// Came from server side
			EntityClientPlayerMP client = (EntityClientPlayerMP) player;

			UpdateClientPacket data = null;
			try {
				data = UpdateClientPacket.fromPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			TileEntity tileEntity = client.worldObj.getBlockTileEntity(data.x, data.y, data.z);

			// Prevents throwing an exception when a player just destroyed the
			// block while updating
			if (tileEntity instanceof ISyncCapable)
				((ISyncCapable) tileEntity).readNBT(data.tag);
		} else {

			EntityPlayerMP server = (EntityPlayerMP) player;

			UpdateServerPacket data = null;
			try {
				data = UpdateServerPacket.fromPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			TileEntity entity = server.worldObj.getBlockTileEntity(data.x, data.y, data.z);

			ICounterContainer counter = null;
			if (entity instanceof ICounterContainer)
				counter = (ICounterContainer) entity;
			else if (entity instanceof TileGenericPipe) {
				TileGenericPipe generic = (TileGenericPipe) entity;
				if (generic.pipe instanceof ICounterContainer) {
					counter = (ICounterContainer) generic.pipe;
				}
			}

			NBTTagCompound tag = data.tag;

			if (counter != null && counter.getCounter() != null) {
				if (tag.hasKey(Counter.ACTIVE_TAG))
					counter.getCounter().setActive(tag.getBoolean(Counter.ACTIVE_TAG));

				if (tag.hasKey(SideController.SIDES_TAG)) {
					if (entity instanceof ISideAware) {
						ISideAware iSideAware = (ISideAware) entity;
						iSideAware.getSideController().readFromNBT(tag);
					}
				}

				server.worldObj.notifyBlocksOfNeighborChange(data.x, data.y, data.z, server.worldObj.getBlockId(data.x, data.y, data.z));
			}
		}
	}

}
