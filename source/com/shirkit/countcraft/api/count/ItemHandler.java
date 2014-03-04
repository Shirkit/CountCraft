package com.shirkit.countcraft.api.count;

import com.shirkit.countcraft.api.IStack;

import net.minecraft.item.ItemStack;

/**
 * Handles items.
 * 
 * @author Shirkit
 * 
 */
public class ItemHandler implements IStack {

	public ItemHandler(ItemStack stack) {
		myStack = stack;
		amount = stack.stackSize;
	}

	public ItemHandler(ItemStack stack, int amount) {
		this(stack);
		this.amount = amount;
	}

	private ItemStack myStack;
	private int amount;

	@Override
	public Integer getAmount() {
		return amount;
	}

	public Integer getMetadata() {
		return myStack.getItemDamage();
	}

	@Override
	public String getId() {
		return myStack.itemID + "-" + getMetadata();
	}

	@Override
	public String getIdentifier() {
		return itemID;
	}

	@Override
	public String getName() {
		return myStack.getDisplayName();
	}

	public ItemStack getStack() {
		return myStack;
	}
}