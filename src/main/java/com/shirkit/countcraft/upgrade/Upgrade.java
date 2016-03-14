package com.shirkit.countcraft.upgrade;

import java.io.Serializable;

import net.minecraft.item.ItemStack;

public class Upgrade implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2574249348258602706L;

	public ItemStack stack;

	public Upgrade() {
	}

	public Upgrade(ItemStack stack) {
		this.stack = stack;
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

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return stack.getUnlocalizedName() + ":" + stack.getItemDamage();
	}
}
