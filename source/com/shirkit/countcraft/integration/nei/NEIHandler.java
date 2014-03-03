package com.shirkit.countcraft.integration.nei;

import net.minecraft.client.gui.GuiButton;
import codechicken.nei.LayoutManager;

import com.shirkit.countcraft.gui.Button;
import com.shirkit.countcraft.gui.GuiCounter;
import com.shirkit.countcraft.gui.IGuiDrawer;
import com.shirkit.countcraft.gui.IGuiListener;
import com.shirkit.countcraft.integration.IIntegrationHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class NEIHandler implements IIntegrationHandler, IGuiListener {

	private Button nei;
	private static boolean useNeiFilter = false;

	@Override
	public void init(FMLInitializationEvent event) {
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		GuiCounter.listeners.add(this);
	}

	private String lastStr;

	@Override
	public void onGuiOpen(IGuiDrawer gui) {
		String field = LayoutManager.searchField.text();
		if (field != null) {
			gui.setNameFilter(field);
			lastStr = field;
		}

		nei = gui.addButtonToOptions(this);
		updateButtonInfo(gui);
	}

	@Override
	public void onScreenPreDraw(IGuiDrawer gui) {
		String field = LayoutManager.searchField.text();
		if (field != null && !field.equals(lastStr)) {
			gui.setNameFilter(field);
			lastStr = field;
		}
	}

	@Override
	public void onButtonPress(IGuiDrawer gui, GuiButton pressed) {
		if (pressed == nei) {
			useNeiFilter = !useNeiFilter; // invert
			updateButtonInfo(gui);
		}
	}

	private void updateButtonInfo(IGuiDrawer gui) {
		if (!useNeiFilter) {
			gui.setNameFilter(null);
			nei.displayString = "NEI off";
			nei.tooltip = "Ignoring NEI's search box";
		} else {
			gui.setNameFilter(lastStr);
			nei.displayString = "NEI on";
			nei.tooltip = "Using NEI's search box to filter things";
		}
	}

}
