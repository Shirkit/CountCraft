package com.shirkit.countcraft.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Counter implements ICounter {

	protected boolean active;

	protected HashMap<String, Long> count;

	protected long ticksRun;

	protected long totalCounted;

	public Counter() {
		count = new HashMap<String, Long>();
		totalCounted = 0;
		ticksRun = 0;
		active = true;
	}

	public boolean add(IStack stack) {
		return add(stack.getIdentifier(), stack.getId(), stack.getAmount());
	}

	protected boolean add(String identifier, Object id, Long quantity) {
		if (!active)
			return false;

		Long amount = count.get(identifier + ":" + id);

		if (amount == null)
			amount = new Long(0);

		amount += quantity;
		totalCounted += quantity;
		count.put(identifier + ":" + id, amount);
		return true;
	}

	public List<IStack> entrySet() {
		List<IStack> list = new ArrayList<IStack>();
		Set<Entry<String, Long>> entrySet = count.entrySet();
		for (Entry<String, Long> entry : entrySet) {

			// IDENTIFIER : ID
			String[] split = entry.getKey().split(":");

			// Items: MODID / ID - META
			// Fluids: ID
			// Energy: ID - DIR - SIDE
			String[] split2 = split[1].split("-");

			if (split[0].equals(IStack.itemID)) {
				split2 = (split[1] + ":" + split[2]).split("-");
				Integer meta = Integer.parseInt(split2[1]);
				UniqueIdentifier uid = new UniqueIdentifier(split2[0]);
				Item item = GameRegistry.findItem(uid.modId, uid.name);

				ItemStack stack = new ItemStack(item);
				stack.setItemDamage(meta);
				IStack handler = new ItemHandler(stack, entry.getValue());
				list.add(handler);
			} else if (split[0].equals(IStack.fluidID)) {
				Integer fluidID = Integer.parseInt(split2[0]);
				FluidStack stack = new FluidStack(FluidRegistry.getFluid(fluidID), 0);
				IStack handler = new FluidHandler(stack, entry.getValue());
				list.add(handler);
			} else if (split[0].equals(IStack.energyID)) {
				EnergyHandler handler = null;
				handler = new EnergyHandler(EnergyHandler.Kind.valueOf(split2[0]), entry.getValue());
				list.add(handler);
			}
		}

		return list;
	}

	public long getTicksRun() {
		return ticksRun;
	}

	public long getTotalCounted() {
		return totalCounted;
	}

	public boolean isActive() {
		return active;
	}

	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound data) {
		byte[] byteArray = data.getByteArray("count");
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			count = (HashMap<String, Long>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		totalCounted = data.getLong("total");
		active = data.getBoolean(ACTIVE_TAG);
		ticksRun = data.getLong("ticksrun");

		// Clear invalid items from possible updates

		List<String> invalid = new ArrayList<String>();
		Set<Entry<String, Long>> entrySet = count.entrySet();
		for (Entry<String, Long> entry : entrySet) {

			String[] split = entry.getKey().split(":");
			String[] split2 = split[1].split("-");

			if (split[0].equals(IStack.itemID)) {
				split2 = (split[1] + ":" + split[2]).split("-");
				UniqueIdentifier uid = new UniqueIdentifier(split2[0]);
				Item item = GameRegistry.findItem(uid.modId, uid.name);
				if (item == null) {
					invalid.add(entry.getKey());
				}
			} else if (split[0].equals(IStack.fluidID)) {
				Integer intId = Integer.parseInt(split2[0]);
				if (FluidRegistry.getFluid(intId) == null)
					invalid.add(entry.getKey());
			}
		}

		for (String key : invalid)
			count.remove(key);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int size() {
		return count.size();
	}

	public void tick() {
		if (active)
			ticksRun++;
	}

	public void writeToNBT(NBTTagCompound data) {
		ObjectOutputStream oos;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(count);
			} catch (IOException e) {
				e.printStackTrace();
			}
			oos.flush();
			oos.close();
			baos.flush();
			data.setByteArray("count", baos.toByteArray());
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.setLong("total", totalCounted);
		data.setBoolean(ACTIVE_TAG, active);
		data.setLong("ticksrun", ticksRun);
	}
}
