package com.shirkit.countcraft.integration;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Abstraction layer to handle integration with other mods. After the main mod
 * is done with it's processing, then the handlers will be called.
 * 
 * @author Shirkit
 * 
 */
public interface IIntegrationHandler {

	public void init(FMLInitializationEvent event);

	public void preInit(FMLPreInitializationEvent event);

	public void postInit(FMLPostInitializationEvent event);

}
