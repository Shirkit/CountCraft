package com.shirkit.countcraft.integration.cc;

import java.util.HashMap;
import java.util.List;

import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.count.ItemHandler;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class CounterPeripheralWrapper implements IPeripheral {

	private ICounterContainer container;

	private String[] methods = new String[] { "getTotalCounted", "getTicksRun", "entrySet", "help" };

	public CounterPeripheralWrapper(ICounterContainer container) {
		this.container = container;
	}

	@Override
	public void attach(IComputerAccess computer) {
		// Don't care
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		return callMethod(method, arguments);
	}

	public Object[] callMethod(int method, Object[] arguments) {
		switch (method) {
		case 0:
			return new Object[] { container.getCounter().getTotalCounted() };

		case 1:
			return new Object[] { container.getCounter().getTicksRun() };

		case 2:
			List<IStack> set = container.getCounter().entrySet();
			HashMap<Object, Object> map1 = new HashMap<Object, Object>();
			HashMap<Object, Object> map2 = new HashMap<Object, Object>();
			for (IStack stack : set) {

				Object id = stack.getId();

				if (stack instanceof ItemHandler) {
					String[] split = ((String) id).split("-");
					int metadata = Integer.parseInt(split[1]);

					if (metadata != 0)
						id = split[0] + "_" + metadata;
				}

				map1.put(id, stack.getAmount());
				map2.put(id, stack.getName());
			}
			return new Object[] { map1, map2 };

		case 3:
			return new Object[] { "The entrySet() is the main method in question. Currently it returns two tables (t1/t2), with t1 mapping IDs to Amount, and t2 mapping IDs to ItemName.",
					"ItemID= String that follows this specification 'ItemID_Metadata'", "Amount= Integer representing the total amount", "ItemName= A string that represents the name of the item" };
		default:
			return new Object[] {};
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		// Don't care
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(IPeripheral obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CounterPeripheralWrapper other = (CounterPeripheralWrapper) obj;
		if (container == null) {
			if (other.container != null) {
				return false;
			}
		} else if (!container.equals(other.container)) {
			return false;
		}
		return true;
	}

	@Override
	public String[] getMethodNames() {
		return methods;
	}

	@Override
	public String getType() {
		return "counter";
	}

}
