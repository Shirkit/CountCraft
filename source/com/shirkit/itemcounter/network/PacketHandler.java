package com.shirkit.itemcounter.network;

import java.io.IOException;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildcraft.transport.TileGenericPipe;

import com.shirkit.itemcounter.integration.buildcraft.PipeItemCounter;
import com.shirkit.itemcounter.logic.ICounter;

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

			counter.getCounter().readFromNBT(data.tag);
		}
	}

}
