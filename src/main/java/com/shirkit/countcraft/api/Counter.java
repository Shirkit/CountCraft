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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;

public class Counter implements ICounter {

	protected HashMap<String, Integer> count;
	protected long totalCounted;
	protected boolean active;
	protected long ticksRun;

	public Counter() {
		count = new HashMap<String, Integer>();
		totalCounted = 0;
		ticksRun = 0;
		active = true;
	}

	public void tick() {
		if (active)
			ticksRun++;
	}

	protected boolean add(String identifier, Object id, Integer quantity) {
		if (!active)
			return false;

		Integer amount = count.get(identifier + IStack.TYPE_SEPARATOR + id);

		if (amount == null)
			amount = new Integer(0);

		amount += quantity;
		totalCounted += quantity;
		count.put(identifier + IStack.TYPE_SEPARATOR + id, amount);
		return true;
	}

	public boolean add(IStack stack) {
		return add(stack.getIdentifier(), stack.getId(), stack.getAmount());
	}

	public List<IStack> entrySet() {
		List<IStack> list = new ArrayList<IStack>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			// ITENDIFIER : ID
			String[] split = entry.getKey().split(IStack.TYPE_SEPARATOR);

			// Items: ID - META
			// Fluids: ID
			// Energy: ID - DIR - SIDE
			String[] split2 = split[1].split(IStack.METADATA_SEPARATOR);

			if (split[0].equals(IStack.itemID)) {
				Integer meta = Integer.parseInt(split2[1]);
				String id = split2[0];

				ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(id), entry.getValue(), meta);
				IStack handler = new ItemHandler(stack);
				list.add(handler);
			} else if (split[0].equals(IStack.fluidID)) {
				Integer intId = Integer.parseInt(split2[0]);
				FluidStack stack = new FluidStack(intId, entry.getValue());
				IStack handler = new FluidHandler(stack);
				list.add(handler);
			} else if (split[0].equals(IStack.energyID)) {
				EnergyHandler handler = null;
				handler = new EnergyHandler(EnergyHandler.Kind.valueOf(split2[0]), entry.getValue());
				list.add(handler);
			}
		}

		return list;
	}

	public long getTotalCounted() {
		return totalCounted;
	}

	public long getTicksRun() {
		return ticksRun;
	}

	public int size() {
		return count.size();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound data) {
		byte[] byteArray = data.getByteArray("count");
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			count = (HashMap<String, Integer>) ois.readObject();
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
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			String[] split = entry.getKey().split(IStack.TYPE_SEPARATOR);
			String[] split2 = split[1].split(IStack.METADATA_SEPARATOR);

			if (split[0].equals(IStack.itemID)) {
				String id = split2[0];
				if (Item.itemRegistry.getObject(id) == null) {
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
}
