package com.shirkit.utils;

import java.util.List;

import net.minecraft.util.StatCollector;

public class TranslateUtils {

	public static void addTooltip(List<String> toAdd, String unlocalized) {

		int tip = 1;
		String stip = unlocalized.concat(".tooltip.").concat(Integer.toString(tip));
		while (StatCollector.canTranslate(stip)) {
			toAdd.add(StatCollector.translateToLocal(stip));
			tip++;
			stip = unlocalized.concat(".tooltip.").concat(Integer.toString(tip));
		}
	}

}
