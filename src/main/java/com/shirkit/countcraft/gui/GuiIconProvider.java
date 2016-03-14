package com.shirkit.countcraft.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public class GuiIconProvider {

	private static final String BASE = "cofh:icons/";
	private static final String EXTENSION = ".png";

	private static GuiIconProvider instance;

	private static Map<String, String> strings = new HashMap<>();

	static {
		strings.put("IconAugment", BASE + "Icon_Augment");
		strings.put("IconConfig", BASE + "Icon_Config");
	}

	public static GuiIconProvider getInstance() {
		if (instance == null)
			instance = new GuiIconProvider();
		return instance;
	}

	private Map<String, IIcon> iconMap;

	private GuiIconProvider() {
		iconMap = new HashMap<>();
	}

	public IIcon getIcon(String key) {
		return iconMap.get(key);
	}

	public void registerIcons(TextureMap map) {
		if (map.getTextureType() == 1) // items 
			for (Entry<String, String> entry : strings.entrySet()) {
				iconMap.put(entry.getKey(), map.registerIcon(entry.getValue()));
			}
	}

}
