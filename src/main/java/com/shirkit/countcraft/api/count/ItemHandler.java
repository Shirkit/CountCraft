package com.shirkit.countcraft.api.count;

import com.shirkit.countcraft.api.IStack;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

/**
 * Handles items.
 *
 * @author Shirkit
 *
 */
public class ItemHandler implements IStack {

	private long amount;

	private ItemStack myStack;

	public ItemHandler(ItemStack stack) {
		myStack = stack;
		amount = stack.stackSize;
	}

	public ItemHandler(ItemStack stack, long amount) {
		this(stack);
		this.amount = amount;
	}

	@Override
	public Long getAmount() {
		return amount;
	}

	@Override
	public String getId() {
		return GameRegistry.findUniqueIdentifierFor(myStack.getItem()).toString() + "-" + getMetadata();
	}

	@Override
	public String getIdentifier() {
		return itemID;
	}

	public Integer getMetadata() {
		return myStack.getItemDamage();
	}

	@Override
	public String getName() {
		return myStack.getDisplayName();
	}

	public ItemStack getStack() {
		return myStack;
	}
}