package com.shirkit.countcraft.data;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.ModInfos;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CountcraftTab extends CreativeTabs {

	public static CreativeTabs TAB = new CountcraftTab();

	public CountcraftTab() {
		super(ModInfos.NAME);
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(CountCraft.instance.itemCounter);
	}

	@Override
	public String getTranslatedTabLabel() {
		return getTabLabel();
	}
}
