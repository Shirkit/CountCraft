package com.shirkit.countcraft.api.integration;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import com.shirkit.countcraft.gui.Button;

/**
 * The target GUI Screen that will do the actual drawing on the screen. This is
 * normally an extension of {@link GuiScreen}. This is the underlaying interface
 * that allows the integration to access the functions of the GUI Drawing.
 * 
 * @author Shirkit
 * 
 */
public interface IGuiDrawer {

	public void setNameFilter(String filter);

	public GuiButton addButtonToOptions(IGuiListener addingController);

}
