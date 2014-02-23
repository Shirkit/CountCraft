package com.shirkit.itemcounter.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBufferedFluidCounter extends ItemBlock {

	public ItemBlockBufferedFluidCounter(int par1) {
		super(par1);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Counts the fluids that");
		par3List.add("passes through this block.");
		par3List.add("Sneak use to activate it.");
	}
}
