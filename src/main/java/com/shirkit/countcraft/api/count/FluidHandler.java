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

	private long filledAmount;

	private FluidStack myStack;

	public FluidHandler(FluidStack stack) {
		this(stack, stack.amount);
	}

	public FluidHandler(FluidStack stack, long filledAmount) {
		myStack = stack;
		this.filledAmount = filledAmount;
	}

	@Override
	public Long getAmount() {
		return filledAmount;
	}

	@Override
	public Integer getId() {
		return myStack.getFluidID();
	}

	@Override
	public String getIdentifier() {
		return fluidID;
	}

	@Override
	public String getName() {
		return myStack.getFluid().getLocalizedName(myStack);
	}

	public FluidStack getStack() {
		return myStack;
	}
}