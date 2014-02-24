package com.shirkit.countcraft.logic;

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

/**
 * Handles the counting of stuff that wants to be counted. Uses an abstraction
 * layer of {@link Stack} to handle different things like Items, Fluids and
 * Energy.
 * 
 * @author Shirkit
 * 
 */
public class Counter {

	private HashMap<String, Integer> count;
	private long totalCounted;
	private boolean active;
	private long ticksRun;

	public Counter() {
		count = new HashMap<String, Integer>();
		totalCounted = 0;
		ticksRun = 0;
		active = true;
	}

	/**
	 * This must be called every tick by the container class.
	 */
	public void tick() {
		if (active)
			ticksRun++;
	}

	private void add(String identifier, Object id, Integer quantity) {
		if (!active)
			return;

		Integer amount = count.get(identifier + ":" + id);

		if (amount == null)
			amount = new Integer(0);

		amount += quantity;
		totalCounted += quantity;
		count.put(identifier + ":" + id, amount);
	}

	/**
	 * Counts a stack and stores that value.
	 * 
	 * @param stack
	 *            to be counted.
	 */
	public void add(Stack stack) {
		add(stack.getIdentifier(), stack.getId(), stack.getAmount());
	}

	/**
	 * Retrieves the current items counted inside this {@link Counter} filled
	 * with implementation instances of {@link Stack}
	 * 
	 * @see Stack
	 */
	public List<Stack> entrySet() {
		List<Stack> list = new ArrayList<Stack>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			// ITENDIFIER : ID
			String[] split = entry.getKey().split(":");

			// Items:	ID - META
			// Fluids:	ID
			// Energy:	ID - DIR - SIDE
			String[] split2 = split[1].split("-");

			if (split[0].equals(Stack.itemID)) {
				Integer meta = Integer.parseInt(split2[1]);
				Integer intId = Integer.parseInt(split2[0]);

				ItemStack stack = new ItemStack(Item.itemsList[intId], entry.getValue(), meta);
				Stack handler = new ItemHandler(stack);
				list.add(handler);
			} else if (split[0].equals(Stack.fluidID)) {
				Integer intId = Integer.parseInt(split2[0]);
				FluidStack stack = new FluidStack(intId, entry.getValue());
				Stack handler = new FluidHandler(stack);
				list.add(handler);
			} else if (split[0].equals(Stack.energyID)) {
				EnergyHandler handler = null;
				if (split2.length == 2) {
					// no side
					handler = new EnergyHandler(split2[0], split2[1], entry.getValue());
				} else {
					// side aware
					handler = new EnergyHandler(split2[0], split2[1], split2[2], entry.getValue());
				}
				list.add(handler);
			}

		}

		return list;
	}

	/**
	 * Gets the total amount of things that were counted by this {@link Counter}
	 * .
	 */
	public long getTotalCounted() {
		return totalCounted;
	}

	/**
	 * Gets the total amount of ticks that this {@link Counter} has ran.
	 */
	public long getTicksRun() {
		return ticksRun;
	}

	/**
	 * Gets the number of things that this counter knows about
	 */
	public int size() {
		return count.size();
	}

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(Stack)}.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * If {@code true} then the counter is processing the things inputed to it,
	 * otherwise it ignores calls to {@link #add(Stack)}.
	 */
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
		totalCounted = data.getLong("total");
		active = data.getBoolean("active");
		ticksRun = data.getLong("ticksrun");

		// Clear invalid items from possible updates

		List<String> invalid = new ArrayList<String>();
		Set<Entry<String, Integer>> entrySet = count.entrySet();
		for (Entry<String, Integer> entry : entrySet) {

			String[] split = entry.getKey().split(":");
			String[] split2 = split[1].split("-");

			if (split[0].equals(Stack.itemID)) {
				Integer intId = Integer.parseInt(split2[0]);
				if (Item.itemsList[intId] == null) {
					invalid.add(entry.getKey());
				}
			} else if (split[0].equals(Stack.fluidID)) {
				Integer intId = Integer.parseInt(split2[0]);
				if (FluidRegistry.getFluid(intId) == null)
					invalid.add(entry.getKey());
			}
		}

		for (String key : invalid)
			count.remove(key);
	}
}
