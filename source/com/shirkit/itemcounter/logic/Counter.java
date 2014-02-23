package com.shirkit.itemcounter.logic;

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

import com.shirkit.itemcounter.logic.Stack.FluidHandler;
import com.shirkit.itemcounter.logic.Stack.ItemHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Counter {

	private HashMap<String, Integer> count;
	private long totalItems;
	private boolean active;
	private long ticksRun;

	public Counter() {
		count = new HashMap<String, Integer>();
		totalItems = 0;
		ticksRun = 0;
		active = true;
	}

	/**
	 * Must be called every tick
	 */
	public void tick() {
		if (active)
			ticksRun++;
	}

	public void add(String identifier, Integer id, Integer meta, Integer quantity) {
		if (!active)
			return;

		Integer amount = count.get(identifier + ":" + id + ":" + meta);

		if (amount == null)
			amount = new Integer(0);

		amount += quantity;
		totalItems += quantity;
		count.put(identifier + ":" + id + ":" + meta, amount);
	}

	public void add(Stack stack) {
		add(stack.getIdentifier(), stack.getId(), stack.getMetadata(), stack.getAmount());
	}

	public List<Stack> entrySet() {
		List<Stack> list = new ArrayList<Stack>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			String s = entry.getKey();
			int idx1 = s.indexOf(':');
			int idx2 = s.lastIndexOf(':');
			String ident = s.substring(0, idx1);
			String id = s.substring(idx1 + 1, idx2);
			Integer intId = Integer.parseInt(id);

			if (ident.equals(Stack.itemID)) {
				Integer meta = 0;
				if (s.length() > idx2) {
					meta = Integer.parseInt(s.substring(idx2 + 1));
				}

				ItemStack stack = new ItemStack(Item.itemsList[intId], entry.getValue(), meta);
				Stack handler = new ItemHandler(stack);
				list.add(handler);
			} else if (ident.equals(Stack.fluidID)) {
				FluidStack stack = new FluidStack(intId, entry.getValue());
				Stack handler = new FluidHandler(stack);
				list.add(handler);
			}

		}

		return list;
	}

	public long getTotalItems() {
		return totalItems;
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
		data.setLong("total", totalItems);
		data.setBoolean("active", active);
		data.setLong("ticksrun", ticksRun);
	}

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
		totalItems = data.getLong("total");
		active = data.getBoolean("active");
		ticksRun = data.getLong("ticksrun");

		Set<Entry<String, Integer>> entrySet2 = count.entrySet();
		List<String> oldKey = new ArrayList<String>();
		for (Entry<String, Integer> entry : entrySet2) {
			String key = entry.getKey();
			if (key.indexOf(':') == key.lastIndexOf(':')) {
				// We only have 1 entry, it means it's an old item
				oldKey.add(key);
			}
		}

		// Fix loading on existing world
		for (String key : oldKey) {
			Integer amt = count.remove(key);
			count.put(Stack.itemID + ":" + key, amt);
		}

		// Clear invalid items from possible updates

		List<String> invalid = new ArrayList<String>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			String s = entry.getKey();
			String ident = s.substring(0, s.indexOf(':'));
			String id = s.substring(s.indexOf(':') + 1, s.lastIndexOf(':'));
			Integer intId = Integer.parseInt(id);

			if (ident.equals(Stack.itemID)) {
				if (Item.itemsList[intId] == null) {
					invalid.add(s);
				}
			} else if (ident.equals(Stack.fluidID)) {
				if (FluidRegistry.getFluid(intId) == null)
					invalid.add(s);
			}
		}

		for (String key : invalid)
			count.remove(key);
	}
}
