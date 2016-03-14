package com.shirkit.countcraft.api.integration;

import net.minecraft.client.gui.GuiButton;

/**
 * Listener that is called on certain GUI events happens.
 *
 * @author Shirkit
 *
 */
public interface IGuiListener {

	public void onButtonPress(IGuiDrawer gui, GuiButton pressed);

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

}
