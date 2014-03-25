package com.shirkit.countcraft.api.count;

import com.shirkit.countcraft.api.IStack;

import net.minecraftforge.fluids.FluidStack;

/**
 * Handles fluits
 * 
 * @author Shirkit
 * 
 */
public class FluidHandler implements IStack {

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

	public FluidStack getStack() {
		return myStack;
	}
}