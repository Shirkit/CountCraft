package com.shirkit.countcraft.api.side;

import static com.shirkit.countcraft.api.ESideState.Anything;
import static com.shirkit.countcraft.api.ESideState.Input;
import static com.shirkit.countcraft.api.ESideState.Off;
import static com.shirkit.countcraft.api.ESideState.Output;
import static com.shirkit.countcraft.api.ESideState.values;

import com.shirkit.countcraft.api.ESideState;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class SideController {

	public static final String SIDES_TAG = "sidesStates";

	protected ESideState states[] = new ESideState[ForgeDirection.VALID_DIRECTIONS.length];
	protected boolean canAnything = true;

	public SideController() {
		this(Anything, true);
	}

	public SideController(ESideState initialState, boolean canAnything) {
		if (!canAnything && initialState == Anything)
			throw new IllegalArgumentException("Can't set anything in a non-'canAnything' controller");

		for (int i = 0; i < states.length; i++)
			states[i] = initialState;

		this.canAnything = canAnything;
	}

	public boolean isOutput(ForgeDirection side) {
		return this.isOutput(side.ordinal());
	}

	public boolean isOutput(int side) {
		return states[side] == Anything || states[side] == Output;
	}

	public boolean isInput(ForgeDirection side) {
		return this.isInput(side.ordinal());
	}

	public boolean isInput(int side) {
		return states[side] == Anything || states[side] == Input;
	}

	public boolean isDisabled(ForgeDirection side) {
		return this.isDisabled(side.ordinal());
	}

	public boolean isDisabled(int side) {
		return states[side] == Off;
	}

	public ESideState getState(ForgeDirection side) {
		return getState(side.ordinal());
	}

	public ESideState getState(int side) {
		return states[side];
	}

	public void setState(ForgeDirection side, ESideState state) {
		setState(side.ordinal(), state);
	}

	public void setState(int side, ESideState state) {
		if (!canAnything && state == Anything)
			throw new IllegalArgumentException("Can't set anything in a non-'canAnything' controller");

		states[side] = state;
	}

	public boolean canAnything() {
		return canAnything;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		int[] arr = nbt.getIntArray(SIDES_TAG);
		for (int i = 0; i < arr.length; i++)
			states[i] = values()[arr[i]];
		canAnything = nbt.getBoolean("canAnything");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		int[] arr = new int[states.length];
		for (int i = 0; i < states.length; i++)
			arr[i] = states[i].ordinal();
		nbt.setIntArray(SIDES_TAG, arr);
		nbt.setBoolean("canAnything", canAnything);
	}
}
