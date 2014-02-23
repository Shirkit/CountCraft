package com.shirkit.itemcounter.logic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface Stack {

	public static final String itemID = "item";
	public static final String fluidID = "fluid";

	public String getIdentifier();

	public Integer getAmount();

	public Integer getMetadata();

	public Integer getId();

	public String getName();

	public Object getStack();

	public class ItemHandler implements Stack {

		public ItemHandler(ItemStack stack) {
			myStack = stack;
		}

		private ItemStack myStack;

		@Override
		public Integer getAmount() {
			return myStack.stackSize;
		}

		@Override
		public Integer getMetadata() {
			return myStack.getItemDamage();
		}

		@Override
		public Integer getId() {
			return myStack.itemID;
		}

		@Override
		public String getIdentifier() {
			return itemID;
		}

		@Override
		public String getName() {
			return myStack.getDisplayName();
		}

		@Override
		public ItemStack getStack() {
			return myStack;
		}
	}

	public class FluidHandler implements Stack {

		private FluidStack myStack;
		private int filledAmount;
		
		public FluidHandler(FluidStack stack) {
			this(stack, stack.amount);
		}

		public FluidHandler(FluidStack stack, int filledAmount) {
			myStack = stack;
			this.filledAmount = filledAmount;
		}

		@Override
		public Integer getAmount() {
			return filledAmount;
		}

		@Override
		public Integer getMetadata() {
			return null;
		}

		@Override
		public Integer getId() {
			return myStack.fluidID;
		}

		@Override
		public String getIdentifier() {
			return fluidID;
		}

		@Override
		public String getName() {
			return myStack.getFluid().getLocalizedName();
		}

		@Override
		public FluidStack getStack() {
			return myStack;
		}
	}

}
