package com.shirkit.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;

import com.shirkit.countcraft.gui.ContainerCounter;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.countcraft.network.UpdateClientPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class SyncUtils {

	public static int SYNC_INTERVAL = 20;
	public static float SYNC_RANGE = 5.0f;

	public static boolean syncTileEntity(ISyncCapable entity) {
		boolean didSync = false;
		if (entity.isDirty() && entity.getTicksRun() % SYNC_INTERVAL == 0) {
			List list = entity.getTileEntity().worldObj.getEntitiesWithinAABB(
					EntityPlayer.class,
					AxisAlignedBB.getAABBPool().getAABB(entity.getTileEntity().xCoord - SYNC_RANGE, entity.getTileEntity().yCoord - SYNC_RANGE,
							entity.getTileEntity().zCoord - SYNC_RANGE, entity.getTileEntity().xCoord + 1 + SYNC_RANGE,
							entity.getTileEntity().yCoord + 1 + SYNC_RANGE, entity.getTileEntity().zCoord + 1 + SYNC_RANGE));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer player = (EntityPlayer) iterator.next();

				if (player.openContainer instanceof ContainerCounter) {
					if (((ContainerCounter) player.openContainer).getTile() == entity.getTileEntity()) {
						sendCounterUpdatePacket(entity, player);
						didSync = true;
					}
				}
			}
			if (didSync)
				entity.setDirty(false);
		}
		return didSync;
	}

	public static void sendCounterUpdatePacket(ISyncCapable holder, EntityPlayer toPlayer) {
		NBTTagCompound tag = new NBTTagCompound();
		holder.getCounter().writeToNBT(tag);

		UpdateClientPacket update = new UpdateClientPacket(holder.getTileEntity().xCoord, holder.getTileEntity().yCoord, holder.getTileEntity().zCoord, tag);

		try {
			Packet toSend = update.getPacket();
			PacketDispatcher.sendPacketToPlayer(toSend, (Player) toPlayer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
