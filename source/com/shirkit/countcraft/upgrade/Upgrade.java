package com.shirkit.countcraft.upgrade;

import java.io.Serializable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Upgrade implements Serializable {

	public ItemStack stack;

	public Upgrade() {
	}

	public Upgrade(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public String toString() {
		return stack.itemID + ":" + stack.getItemDamage();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Upgrade) {
			Upgrade upgrade = (Upgrade) obj;
			return this.hashCode() == upgrade.hashCode();
		}
		return false;
	}
}
