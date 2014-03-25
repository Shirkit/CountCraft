package com.shirkit.countcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton {

	public String tooltip;

	public Button(int id, int x, int y, int width, int height, String text) {
		super(id, x, y, width, height, text);
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		super.drawButton(par1Minecraft, par2, par3);
	}

	public boolean isHover() {
		return this.enabled && getHoverState(field_146123_n) == 2;
	}

}
