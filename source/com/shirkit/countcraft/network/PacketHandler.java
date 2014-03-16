package com.shirkit.countcraft.network;

import java.io.IOException;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.side.SideController;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

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
				sync.readNBT(data.tag);
		} else {

			EntityPlayerMP server = (EntityPlayerMP) player;

			UpdateServerPacket data = null;
			try {
				data = UpdateServerPacket.fromPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			TileEntity entity = server.worldObj.getBlockTileEntity(data.x, data.y, data.z);
			NBTTagCompound tag = data.tag;

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

				server.worldObj.notifyBlocksOfNeighborChange(data.x, data.y, data.z, server.worldObj.getBlockId(data.x, data.y, data.z));
			}
		}
	}

}
