package com.shirkit.itemcounter.integration.nei;

import codechicken.nei.LayoutManager;

import com.shirkit.itemcounter.gui.GuiCounter;
import com.shirkit.itemcounter.gui.IGuiListener;
import com.shirkit.itemcounter.integration.IIntegrationHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class NEIHandler implements IIntegrationHandler, IGuiListener {

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

	private void work() {
	}

	@Override
	public void onScreenPreDraw(GuiCounter guiCounter) {
		String field = LayoutManager.searchField.text();
		if (field != null && !field.equals(lastStr)) {
			guiCounter.setNameFilter(field);
			lastStr = field;
		}
	}
}
