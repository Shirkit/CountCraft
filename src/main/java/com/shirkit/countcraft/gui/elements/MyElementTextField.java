package com.shirkit.countcraft.gui.elements;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementTextField;
import net.minecraft.client.gui.FontRenderer;

public class MyElementTextField extends ElementTextField {

	protected boolean centerVertically, centerHorinzotally;

	public MyElementTextField(GuiBase gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
	}

	public MyElementTextField(GuiBase gui, int posX, int posY, int width, int height, short limit) {
		super(gui, posX, posY, width, height, limit);
	}

	@Override
	protected void findRenderStart() {
		super.findRenderStart();
 
		int width = 0;
		FontRenderer font = getFontRenderer();
		for (int i = 0; i < textLength; i++)
			width += font.getCharWidth(text[i]);

		offsetX = (sizeX - width) / 2;
		
		offsetY = (sizeY - font.FONT_HEIGHT - 1) / 2;
	}

	public boolean isCenterHorinzotally() {
		return centerHorinzotally;
	}

	public boolean isCenterVertically() {
		return centerVertically;
	}

	public void setCenterHorinzotally(boolean centerHorinzotally) {
		this.centerHorinzotally = centerHorinzotally;
	}

	public void setCenterVertically(boolean centerVertically) {
		this.centerVertically = centerVertically;
	}

}
