package com.shirkit.countcraft.api.integration;

import com.shirkit.countcraft.CountCraft;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Abstraction layer to handle integration with other mods. After the main mod
 * is done with it's processing, then the handlers will be called to do their
 * stuff. If you want to add a Handler to the mod, register yourself at
 * {@link CountCraft#integrations} and {@link CountCraft#listeners}
 * <strong>BEFORE</strong> the pre-initialization event.
 * 
 * @author Shirkit
 * 
 */
public interface IIntegrationHandler {

	public void init(FMLInitializationEvent event);

	public void preInit(FMLPreInitializationEvent event);

	public void postInit(FMLPostInitializationEvent event);

	/**
	 * 
	 * @return an instance to a {@link INetworkListener} that handles the
	 *         arriving of messages between client-server, or null if none.
	 */
	public INetworkListener getNetworkListener();

	/**
	 * 
	 * @return an instance to a {@link IGuiListener}, or null if none.
	 */
	public IGuiListener getGuiListener();

}
