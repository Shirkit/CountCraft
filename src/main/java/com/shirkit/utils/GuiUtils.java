package com.shirkit.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GuiUtils {
	public static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

	public static int getStringWidth(String s) {
		if (s == null || s.equals(""))
			return 0;
		return getStringWidthNoColours(fontRenderer, s);
	}

	public static int getStringWidthNoColours(FontRenderer fontRenderer, String s) {
		if (true)
			while (true) {
				int pos = s.indexOf('\247');
				if (pos == -1)
					break;
				s = s.substring(0, pos) + s.substring(pos + 2);
			}
		return fontRenderer.getStringWidth(s);
	}
}
