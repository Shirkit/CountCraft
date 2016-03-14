package com.shirkit.countcraft.block;

import java.util.List;

import com.shirkit.utils.TranslateUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBufferedItemCounter2 extends ItemBlock {

	public ItemBlockBufferedItemCounter2(Block block) {
		super(block);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tipList, boolean par4) {
		TranslateUtils.addTooltip(tipList, getUnlocalizedName());
	}
}
