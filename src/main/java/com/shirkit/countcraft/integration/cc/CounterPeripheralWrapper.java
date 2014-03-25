package com.shirkit.countcraft.integration.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.IStack;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;

public class CounterPeripheralWrapper implements IHostedPeripheral {

	private ICounterContainer container;
	private String[] methods = new String[] { "getTotalCounted", "getTicksRun", "entrySet", "help" };

	public CounterPeripheralWrapper(ICounterContainer container) {
		this.container = container;
	}

	@Override
	public String getType() {
		return "counter";
	}

	@Override
	public String[] getMethodNames() {
		return methods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		switch (method) {
		case 0:
			return new Object[] { container.getCounter().getTotalCounted() };

		case 1:
			return new Object[] { container.getCounter().getTicksRun() };

		case 2:
			List<IStack> set = container.getCounter().entrySet();
			HashMap<Object, Object> map1 = new HashMap<Object, Object>();
			HashMap<Object, Object> map2 = new HashMap<Object, Object>();
			List<Object> obj = new ArrayList<Object>();
			for (IStack stack : set) {
				map1.put(stack.getId(), stack.getAmount());
				map2.put(stack.getId(), stack.getName());
			}
			return new Object[] { map1, map2 };

		case 3:
			return new Object[] {
					"The entrySet() is the main method in question. Currently it returns two tables (t1/t2), with t1 mapping IDs to Amount, and t2 mapping IDs to ItemName.",
					"ItemID= String that follows this specification 'ItemID-Metadata'", "Amount= Integer representing the total amount",
					"ItemName= A string that represents the name of the item" };
		default:
			return new Object[] {};
		}
	}

	@Override
	public boolean canAttachToSide(int side) {
		// Don't care
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		// Don't care
	}

	@Override
	public void detach(IComputerAccess computer) {
		// Don't care
	}

	@Override
	public void update() {
		// Don't need
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		// Don't care
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		// Don't care
	}
}
