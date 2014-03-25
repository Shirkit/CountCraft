package com.shirkit.countcraft.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.shirkit.countcraft.CountCraft;

public class CountcraftTab extends CreativeTabs {

	public static CreativeTabs TAB = new CountcraftTab();

	public CountcraftTab() {
		super("CountCraft");
	}

	@Override
	public Item getTabIconItem() {
		return CountCraft.instance.chestItem;
	}

	@Override
	public String getTranslatedTabLabel() {
		return getTabLabel();
	}
}
