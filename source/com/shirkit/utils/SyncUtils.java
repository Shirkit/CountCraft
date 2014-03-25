package com.shirkit.utils;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.gui.ContainerCounter;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.countcraft.network.UpdateCounterPacket;

public class SyncUtils {

	public static int SYNC_INTERVAL = 20;
	public static float SYNC_RANGE = 5.0f;

	public static boolean syncTileEntity(ISyncCapable sync, ICounterContainer entity) {
		boolean didSync = false;
		if (sync.isDirty() && sync.getTicksRun() % SYNC_INTERVAL == 0) {
			List list = entity.getTileEntity().getWorldObj().getEntitiesWithinAABB(
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
				sync.setDirty(false);
		}
		return didSync;
	}

	public static void sendCounterUpdatePacket(ICounterContainer holder, EntityPlayer toPlayer) {
		NBTTagCompound tag = new NBTTagCompound();
		holder.writeNBT(tag);
		
		UpdateCounterPacket update = new UpdateCounterPacket(holder.getTileEntity().xCoord, holder.getTileEntity().yCoord, holder.getTileEntity().zCoord, tag);
		CountCraft.PACKET_PIPELINE.sendTo(update, (EntityPlayerMP) toPlayer);
		
	}
}
