package com.shirkit.countcraft.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.Counter;
import com.shirkit.countcraft.api.ESideState;
import com.shirkit.countcraft.api.ICounterListener;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

import cofh.api.tileentity.IAugmentable;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.Constants;

public class TileBufferedItemCounter2 extends TileBufferedItemCounter implements IAugmentable, IReconfigurableSides, IReconfigurableFacing, ISidedTexture {

	// Persistent

	private ItemStack[] augments = new ItemStack[6];

	@Override
	public void installAugments() {
	}

	@Override
	public ItemStack[] getAugmentSlots() {
		return augments;
	}

	@Override
	public boolean[] getAugmentStatus() {
		ItemStack[] augmentSlots = getAugmentSlots();
		boolean[] status = new boolean[augmentSlots.length];
		Arrays.fill(status, false);
		for (int i = 0; i < augmentSlots.length; i++) {
			if (augmentSlots[i] != null)
				status[i] = true;
		}
		return status;
	}

	@Override
	public boolean decrSide(int side) {
		int config = sides.getState(side).ordinal()-1;
		if (config < 0) {
			config += ESideState.values().length;
		}
		if (!sides.canAnything())
			config--;
		sides.setState(side, ESideState.values()[config]);
		System.out.println(ESideState.values()[config]);	
		return true;
	}

	@Override
	public boolean incrSide(int side) {
		int config = sides.getState(side).ordinal()+1;
		if (config+1 > ESideState.values().length || (config == ESideState.Anything.ordinal() && !sides.canAnything())) {
			config = 0;
		}
		sides.setState(side, ESideState.values()[config]);
		System.out.println(ESideState.values()[config]);
		return true;
	}

	@Override
	public boolean setSide(int side, int config) {
		if (config == 0) {
			sides.setState(side, ESideState.values()[config]);
			return true;
		}
		return false;
	}

	@Override
	public boolean resetSides() {
		for (int i = 0; i < 6; i++)
			sides.setState(i, ESideState.Off);
		return true;
	}

	@Override
	public int getNumConfig(int side) {
		return sides.getState(side).ordinal();
	}

	@Override
	public int getFacing() {
		return 0;
	}

	@Override
	public boolean allowYAxisFacing() {
		return false;
	}

	@Override
	public boolean rotateBlock() {
		return false;
	}

	@Override
	public boolean setFacing(int side) {
		return false;
	}

	@Override
	public IIcon getTexture(int side, int pass) {
		return CountCraft.instance.newItemCounter.getIcon(side, 0);
	}

}
