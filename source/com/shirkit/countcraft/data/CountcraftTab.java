package com.shirkit.countcraft.data;

import net.minecraft.creativetab.CreativeTabs;

import com.shirkit.countcraft.CountCraft;

public class CountcraftTab extends CreativeTabs {

	public static CreativeTabs TAB = new CountcraftTab();

	public CountcraftTab() {
		super("CountCraft");
	}

	@Override
	public int getTabIconItemIndex() {
		return CountCraft.instance.chest.blockID;
	}

	@Override
	public String getTranslatedTabLabel() {
		return getTabLabel();
	}
}
