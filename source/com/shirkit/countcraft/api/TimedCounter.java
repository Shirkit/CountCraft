package com.shirkit.countcraft.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import com.shirkit.countcraft.api.EntryCounter.Pair;
import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;

public class TimedCounter extends Counter implements ICounter, IComplexCounter {

	protected HashMap<String, Integer> count2;
	protected LinkedList<Pair<Long, Pair<String, Integer>>> list;
	/**
	 * Expressed in ticks.
	 */
	protected int limit;
	protected long myTicks;
	protected boolean countComplex = true;

	public TimedCounter() {
		super();
		count2 = new HashMap<String, Integer>();
		list = new LinkedList<Pair<Long, Pair<String, Integer>>>();
		limit = 20 * 20;
	}

	@Override
	public void tick() {
		super.tick();
		if (active) {
			myTicks++;
			
			if (ticksRun % 20 == 0) {
				boolean hasWork = list.size() > 0;
				while (hasWork) {
					Pair<Long, Pair<String, Integer>> element = list.peekFirst();
					if (ticksRun - element.first > limit) {
						Integer amount = count2.get(element.second.first);

						if (amount == null)
							amount = new Integer(0);

						amount += element.second.second;
						totalCounted -= element.second.second;
						count2.put(element.second.first, amount);
						list.removeFirst();
						hasWork = list.size() > 0;
					} else
						hasWork = false;
				}
			}
			
			if (myTicks > limit)
				myTicks = limit;
		}
	}

	@Override
	protected boolean add(String identifier, Object id, Integer quantity) {
		boolean result = super.add(identifier, id, quantity);
		if (result) {
			list.add(new Pair(ticksRun, new Pair(identifier + IStack.TYPE_SEPARATOR + id, quantity)));
		}
		return result;
	}

	@Override
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

			Integer amount = entry.getValue();

			if (countComplex) {
				Integer amount2 = count2.get(entry.getKey());
				if (amount2 != null) {
					amount -= amount2;
				}
				if (amount < 1)
					continue;
			}

			if (split[0].equals(IStack.itemID)) {
				Integer meta = Integer.parseInt(split2[1]);
				String id = split2[0];

				ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(id), amount, meta);
				IStack handler = new ItemHandler(stack);
				list.add(handler);
			} else if (split[0].equals(IStack.fluidID)) {
				Integer intId = Integer.parseInt(split2[0]);
				FluidStack stack = new FluidStack(intId, amount);
				IStack handler = new FluidHandler(stack);
				list.add(handler);
			} else if (split[0].equals(IStack.energyID)) {
				EnergyHandler handler = null;
				handler = new EnergyHandler(EnergyHandler.Kind.valueOf(split2[0]), amount);
				list.add(handler);
			}

		}

		return list;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		ObjectOutputStream oos;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(count2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			oos.flush();
			oos.close();
			baos.flush();
			data.setByteArray("count2", baos.toByteArray());
			baos.close();

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(list);
			} catch (IOException e) {
				e.printStackTrace();
			}
			oos.flush();
			oos.close();
			baos.flush();
			data.setByteArray("list", baos.toByteArray());
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		data.setBoolean(IComplexCounter.COMPLEX_TAG, countComplex);
		data.setInteger("ticklimit", limit);
		data.setLong("myTicks", myTicks);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		byte[] byteArray = data.getByteArray("count2");
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			count2 = (HashMap<String, Integer>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		byteArray = data.getByteArray("list");
		bais = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			list = (LinkedList<Pair<Long, Pair<String, Integer>>>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		countComplex = data.getBoolean(IComplexCounter.COMPLEX_TAG);
		limit = data.getInteger("ticklimit");
		myTicks = data.getLong("myTicks");
	}

	@Override
	public long getTicksRun() {
		if (countComplex)
			return myTicks;
		else
			return super.getTicksRun();
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	public boolean isComplex() {
		return countComplex;
	}

	public void setComplex(boolean complex) {
		this.countComplex = complex;
	}
}
