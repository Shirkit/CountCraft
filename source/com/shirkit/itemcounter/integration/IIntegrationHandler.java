package com.shirkit.itemcounter.integration;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public interface IIntegrationHandler {

	public void init(FMLInitializationEvent event);

	public void preInit(FMLPreInitializationEvent event);

	public void postInit(FMLPostInitializationEvent event);

}
