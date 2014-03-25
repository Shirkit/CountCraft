package com.shirkit.countcraft.proxy;

import org.apache.logging.log4j.Level;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Proxy {

	public void searchForIntegration(FMLPreInitializationEvent event) {

		// First we try to find all the mods that we have integration with
		if (Loader.isModLoaded("BuildCraft|Transport")) {
			try {
				//CountCraft.instance.integrations.add(new BuildCraftHandler());
				event.getModLog().info("Buildcraft integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.ERROR, "Buildcraft integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("NotEnoughItems")) {
			try {
				//CountCraft.instance.integrations.add(new NEIHandler());
				event.getModLog().info("Not Enough Items integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.ERROR, "Not Enough Items integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ThermalExpansion")) {
			try {
				//CountCraft.instance.integrations.add(new ThermalExpansionHandler());
				event.getModLog().info("Thermal Expansion integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.ERROR, "Thermal Expansion integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ComputerCraft")) {
			try {
				//CountCraft.instance.integrations.add(new ComputerCraftHandler());
				event.getModLog().info("ComputerCraft integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.ERROR, "ComputerCraft integration failed to load", e);
			}
		}
		
		for (IIntegrationHandler handler : CountCraft.instance.integrations) {
			ICounterFinder networkListener = handler.getCounterFinder();
			if (networkListener != null)
				CountCraft.instance.finders.add(networkListener);
		}
	}

	public void registerRenderers(FMLPostInitializationEvent event) {
	}
}
