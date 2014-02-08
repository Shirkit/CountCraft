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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

	public void addItem(Integer id, Integer meta, Integer quantity) {
		if (!active)
			return;

		Integer amount = count.get(id + ":" + meta);

		if (amount == null)
			amount = new Integer(0);

		amount += quantity;
		totalItems += quantity;
		count.put(id + ":" + meta, amount);
	}

	public void addItem(ItemStack stack) {
		addItem(stack.itemID, stack.getItemDamage(), stack.stackSize);
	}

	public List<ItemStack> entrySet() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			Integer id = 0;
			Integer meta = 0;
			String s = entry.getKey();
			int index = s.indexOf(':');
			if (index > 0) {
				id = Integer.parseInt(s.substring(0, index));
				meta = Integer.parseInt(s.substring(index + 1));
			} else {
				id = Integer.parseInt(s);
			}

			ItemStack stack = new ItemStack(Item.itemsList[id], entry.getValue(), meta);
			list.add(stack);
		}

		return list;
	}

	public long getTotalItems() {
		return totalItems;
	}

	public long getTicksRun() {
		return ticksRun;
	}

	public int count(int id) {
		Integer amt = count.get(id);
		return amt != null ? amt : 0;
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
	}
}
