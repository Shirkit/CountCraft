package com.shirkit.countcraft.integration.nei;

import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiDrawer;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.gui.Button;

import codechicken.nei.LayoutManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.gui.GuiButton;

public class NEIHandler implements IIntegrationHandler, IGuiListener {

	private static boolean useNeiFilter = false;

	private String lastStr;

	private Button nei;

	@Override
	public ICounterFinder getCounterFinder() {
		return null;
	}

	@Override
	public IGuiListener getGuiListener() {
		return this;
	}

	@Override
	public void init(FMLInitializationEvent event) {
	}

	@Override
	public void onButtonPress(IGuiDrawer gui, GuiButton pressed) {
		if (pressed == nei) {
			useNeiFilter = !useNeiFilter; // invert
			updateButtonInfo(gui);
		}
	}

	@Override
	public void onGuiOpen(IGuiDrawer gui) {
		String field = LayoutManager.searchField.text();
		if (field != null) {
			gui.setNameFilter(field);
			lastStr = field;
		}

		if (nei == null || !gui.containsButton(nei)) {
			nei = (Button) gui.addButtonToOptions(this);
		}
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
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
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
