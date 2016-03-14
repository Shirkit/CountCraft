package com.shirkit.countcraft.integration.te;

import java.util.List;

import com.shirkit.utils.TranslateUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCounterEnergyCell extends ItemBlock {

	public ItemBlockCounterEnergyCell(Block block) {
		super(block);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		TranslateUtils.addTooltip(par3List, getUnlocalizedName());
	}

}
