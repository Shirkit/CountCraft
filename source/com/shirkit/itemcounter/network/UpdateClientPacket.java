package com.shirkit.itemcounter.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.shirkit.itemcounter.ItemCounter;

public class UpdateClientPacket {

	public int x, y, z;
	public NBTTagCompound tag;

	public UpdateClientPacket(int x, int y, int z, NBTTagCompound tag) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}

	public Packet getPacket() throws IOException {
		Packet250CustomPayload packet = new Packet250CustomPayload();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		byte[] abyte = CompressedStreamTools.compress(tag);
		out.writeShort((short) abyte.length);
		out.write(abyte);
		baos.flush();

		packet.channel = ItemCounter.CHANNEL;
		packet.data = baos.toByteArray();
		packet.length = packet.data.length;
		packet.isChunkDataPacket = false;

		return packet;
	}

	public static UpdateClientPacket fromPacket(Packet250CustomPayload packet) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(packet.data);
		DataInput in = new DataInputStream(bais);

		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();

		NBTTagCompound tag = Packet.readNBTTagCompound(in);

		return new UpdateClientPacket(x, y, z, tag);
	}

}
