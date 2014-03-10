package com.shirkit.countcraft.proxy;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public interface IIntegrationProxy {

	public void registerRender(FMLPostInitializationEvent event);

}
