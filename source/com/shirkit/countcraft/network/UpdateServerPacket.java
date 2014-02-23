package com.shirkit.countcraft.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.shirkit.countcraft.CountCraft;

public class UpdateServerPacket {

	public boolean active;
	public int x, y, z;

	public UpdateServerPacket(int x, int y, int z, boolean active) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.active = active;
	}

	public Packet getPacket() throws IOException {
		Packet250CustomPayload packet = new Packet250CustomPayload();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeBoolean(active);
		baos.flush();

		packet.channel = CountCraft.CHANNEL;
		packet.data = baos.toByteArray();
		packet.length = packet.data.length;
		packet.isChunkDataPacket = false;

		return packet;
	}

	public static UpdateServerPacket fromPacket(Packet250CustomPayload packet) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(packet.data);
		DataInput in = new DataInputStream(bais);

		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		boolean active = in.readBoolean();

		return new UpdateServerPacket(x, y, z, active);
	}
}
