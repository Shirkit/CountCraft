package com.shirkit.utils;

import java.util.List;

import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TranslateUtils {
	
	public static void addTooltip(List toAdd, String unlocalized) {
		
		int tip = 1;
		String stip = unlocalized.concat(".tooltip.").concat(Integer.toString(tip));
		while (StatCollector.func_94522_b(stip)) {
			toAdd.add(StatCollector.translateToLocal(stip));
			tip++;
			stip = unlocalized.concat(".tooltip.").concat(Integer.toString(tip));
		}
	}

}
