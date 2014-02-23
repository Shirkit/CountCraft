package com.shirkit.countcraft.network;

import java.io.IOException;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildcraft.transport.TileGenericPipe;

import com.shirkit.countcraft.integration.buildcraft.PipeItemCounter;
import com.shirkit.countcraft.logic.ICounter;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (player instanceof EntityClientPlayerMP) {
			// Client side
			EntityClientPlayerMP client = (EntityClientPlayerMP) player;

			UpdateClientPacket data = null;
			try {
				data = UpdateClientPacket.fromPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			TileEntity tileEntity = client.worldObj.getBlockTileEntity(data.x, data.y, data.z);

			ICounter counter = null;
			if (tileEntity instanceof ICounter)
				counter = (ICounter) tileEntity;
			else if (tileEntity instanceof TileGenericPipe) {
				TileGenericPipe generic = (TileGenericPipe) tileEntity;
				if (generic.pipe instanceof PipeItemCounter) {
					PipeItemCounter con = (PipeItemCounter) generic.pipe;
					counter = con;
				}
			}
			
			// Prevents throwing an exception when a player just destroyed the block while updating
			if (counter != null && counter.getCounter() != null)
				counter.getCounter().readFromNBT(data.tag);
		} else {

			EntityPlayerMP server = (EntityPlayerMP) player;

			UpdateServerPacket data = null;
			try {
				data = UpdateServerPacket.fromPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			TileEntity entity = server.worldObj.getBlockTileEntity(data.x, data.y, data.z);

			ICounter counter = null;
			if (entity instanceof ICounter)
				counter = (ICounter) entity;
			else if (entity instanceof TileGenericPipe) {
				TileGenericPipe generic = (TileGenericPipe) entity;
				if (generic.pipe instanceof PipeItemCounter) {
					PipeItemCounter con = (PipeItemCounter) generic.pipe;
					counter = con;
				}
			}

			if (counter != null && counter.getCounter() != null)
				counter.getCounter().setActive(data.active);
		}
	}

}
