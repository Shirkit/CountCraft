package com.shirkit.itemcounter.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBufferedItemCounter extends ItemBlock {

	public ItemBlockBufferedItemCounter(int par1) {
		super(par1);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Counts the items that");
		par3List.add("are placed in this block.");
		par3List.add("Sneak use to activate it..");
	}
}
