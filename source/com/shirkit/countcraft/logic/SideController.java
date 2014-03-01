package com.shirkit.countcraft.logic;

import static com.shirkit.countcraft.logic.SideState.Anything;
import static com.shirkit.countcraft.logic.SideState.Input;
import static com.shirkit.countcraft.logic.SideState.Off;
import static com.shirkit.countcraft.logic.SideState.Output;
import static com.shirkit.countcraft.logic.SideState.values;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.core.utils.INBTTagable;

public class SideController implements INBTTagable {

	public static final String SIDES_TAG = "sidesStates";

	protected SideState states[] = new SideState[ForgeDirection.VALID_DIRECTIONS.length];
	protected boolean canAnything = true;

	public SideController() {
		this(Anything, true);
	}

	public SideController(SideState initialState, boolean canAnything) {
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

	public SideState getState(ForgeDirection side) {
		return getState(side.ordinal());
	}

	public SideState getState(int side) {
		return states[side];
	}

	public void setState(ForgeDirection side, SideState state) {
		setState(side.ordinal(), state);
	}

	public void setState(int side, SideState state) {
		if (!canAnything && state == Anything)
			throw new IllegalArgumentException("Can't set anything in a non-'canAnything' controller");

		states[side] = state;
	}

	public boolean canAnything() {
		return canAnything;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		int[] arr = nbt.getIntArray(SIDES_TAG);
		for (int i = 0; i < arr.length; i++)
			states[i] = values()[arr[i]];
		canAnything = nbt.getBoolean("canAnything");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		int[] arr = new int[states.length];
		for (int i = 0; i < states.length; i++)
			arr[i] = states[i].ordinal();
		nbt.setIntArray(SIDES_TAG, arr);
		nbt.setBoolean("canAnything", canAnything);
	}
}
