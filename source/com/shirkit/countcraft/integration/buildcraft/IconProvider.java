package com.shirkit.countcraft.integration.buildcraft;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IconProvider implements IIconProvider {

	public static enum TYPE {
		PipeItemCounter("pipeItemCounter"),
		PipeFluidCounter("pipeFluidCounter");

		public static final TYPE[] VALUES = values();
		private final String iconTag;
		private final String iconTagColorBlind;
		private Icon icon;

		private TYPE(String iconTag, String IconTagColorBlind) {
			this.iconTag = iconTag;
			this.iconTagColorBlind = IconTagColorBlind;
		}

		private TYPE(String iconTag) {
			this(iconTag, iconTag);
		}

		private void registerIcon(IconRegister iconRegister) {
			icon = iconRegister.registerIcon("itemcounter:" + iconTag);
		}

		public Icon getIcon() {
			return icon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int pipeIconIndex) {
		if (pipeIconIndex == -1)
			return null;
		return TYPE.VALUES[pipeIconIndex].icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		for (TYPE type : TYPE.VALUES) {
			type.registerIcon(iconRegister);
		}
	}

}
