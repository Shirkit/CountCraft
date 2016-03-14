package com.shirkit.utils;

import java.util.Iterator;
import java.util.List;

import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.gui.ContainerCounter;
import com.shirkit.countcraft.network.ISyncCapable;
import com.shirkit.countcraft.network.PacketDispatcher;
import com.shirkit.countcraft.network.client.UpdateClientCounterMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class SyncUtils {

	public static int SYNC_INTERVAL = 20;

	public static float SYNC_RANGE = 5.0f;

	public static void sendCounterUpdatePacket(ICounterContainer holder, EntityPlayer toPlayer) {
		NBTTagCompound tag = new NBTTagCompound();
		holder.writeNBT(tag);

		final TileEntity tileEntity = holder.getTileEntity();
		PacketDispatcher.sendTo(new UpdateClientCounterMessage(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tag), (EntityPlayerMP) toPlayer);
	}

	@SuppressWarnings("rawtypes")
	public static boolean syncTileEntity(ISyncCapable sync, ICounterContainer entity) {
		boolean didSync = false;
		if (sync.getTicksRun() % SYNC_INTERVAL == 0) {
			List list = entity.getTileEntity().getWorldObj().getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getBoundingBox(entity.getTileEntity().xCoord - SYNC_RANGE, entity.getTileEntity().yCoord - SYNC_RANGE, entity.getTileEntity().zCoord - SYNC_RANGE,
							entity.getTileEntity().xCoord + 1 + SYNC_RANGE, entity.getTileEntity().yCoord + 1 + SYNC_RANGE, entity.getTileEntity().zCoord + 1 + SYNC_RANGE));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer player = (EntityPlayer) iterator.next();

				if (player.openContainer instanceof ContainerCounter) {
					if (((ContainerCounter) player.openContainer).getTile() == entity.getTileEntity()) {

						// player.worldObj.markBlockForUpdate(entity.getTileEntity().xCoord,
						// entity.getTileEntity().yCoord,
						// entity.getTileEntity().zCoord);
						// entity.getTileEntity().markDirty();

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
}
