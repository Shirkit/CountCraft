package com.shirkit.countcraft.block;

import java.util.List;

import com.shirkit.utils.TranslateUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBufferedFluidCounter extends ItemBlock {

	public ItemBlockBufferedFluidCounter(Block block) {
		super(block);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		TranslateUtils.addTooltip(par3List, getUnlocalizedName());
	}
}
