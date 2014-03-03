package com.shirkit.countcraft.gui;

import net.minecraft.client.gui.GuiButton;

public interface IGuiListener {

	/**
	 * Register additional buttons with
	 * {@link IGuiDrawer#addButtonToOptions(IGuiListener)} on this method body.
	 * 
	 */
	public void onGuiOpen(IGuiDrawer gui);

	/**
	 * Before drawing the screen this is called. Adjust any parameters here.
	 * 
	 */
	public void onScreenPreDraw(IGuiDrawer gui);

	public void onButtonPress(IGuiDrawer gui, GuiButton pressed);

}
