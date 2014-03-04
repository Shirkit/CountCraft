package com.shirkit.countcraft.proxy;

import java.util.logging.Level;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.gui.GuiCounter;
import com.shirkit.countcraft.integration.buildcraft.BuildCraftHandler;
import com.shirkit.countcraft.integration.cc.ComputerCraftHandler;
import com.shirkit.countcraft.integration.nei.NEIHandler;
import com.shirkit.countcraft.integration.te.ThermalExpansionHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy {
	@SidedProxy(clientSide = "com.shirkit.countcraft.proxy.ProxyClient", serverSide = "com.shirkit.countcraft.proxy.Proxy")
	public static Proxy proxy;

	public void searchForIntegration(FMLPreInitializationEvent event) {

		// First we try to find all the mods that we have integration with
		if (Loader.isModLoaded("BuildCraft|Transport")) {
			try {
				CountCraft.instance.integrations.add(new BuildCraftHandler());
				event.getModLog().info("Buildcraft integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.SEVERE, "Buildcraft integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("NotEnoughItems")) {
			try {
				CountCraft.instance.integrations.add(new NEIHandler());
				event.getModLog().info("Not Enough Items integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.SEVERE, "Not Enough Items integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ThermalExpansion")) {
			try {
				CountCraft.instance.integrations.add(new ThermalExpansionHandler());
				event.getModLog().info("Thermal Expansion integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.SEVERE, "Thermal Expansion integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ComputerCraft")) {
			try {
				CountCraft.instance.integrations.add(new ComputerCraftHandler());
				event.getModLog().info("ComputerCraft integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.SEVERE, "ComputerCraft integration failed to load", e);
			}
		}

		// Then we search for any network/gui finders, if they have one
		for (IIntegrationHandler handler : CountCraft.instance.integrations) {
			ICounterFinder networkListener = handler.getCounterFinder();
			if (networkListener != null)
				CountCraft.instance.finders.add(networkListener);

			IGuiListener guiListener = handler.getGuiListener();
			if (guiListener != null)
				GuiCounter.listeners.add(guiListener);
		}
	}
}
