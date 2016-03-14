package com.shirkit.countcraft.proxy;

import org.apache.logging.log4j.Level;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.ModInfos;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.integration.cc.ComputerCraftHandler;
import com.shirkit.countcraft.integration.oc.OpenComputersHandler;
import com.shirkit.countcraft.integration.te.ThermalExpansionHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.TextureStitchEvent;

public class Proxy {

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	@SideOnly(Side.CLIENT)
	public void registerRenderers(FMLPostInitializationEvent event) {
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {
	}

	public void searchForIntegration(FMLPreInitializationEvent event) {

		// First we try to find all the mods that we have integration with
		if (Loader.isModLoaded("NotEnoughItems")) {
			try {
				CountCraft.instance.integrations.add(new com.shirkit.countcraft.integration.nei.NEIHandler());
				event.getModLog().info("Not Enough Items integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.WARN, "Not Enough Items integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ThermalDynamics")) {
			try {
				CountCraft.instance.integrations.add(new ThermalExpansionHandler());
				event.getModLog().info("Thermal Dynamics integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.WARN, "Thermal Dynamics integration failed to load", e);
			}
		}

		if (Loader.isModLoaded("ComputerCraft")) {
			try {
				CountCraft.instance.integrations.add(new ComputerCraftHandler());
				event.getModLog().info("ComputerCraft integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.WARN, "ComputerCraft integration failed to load", e);
			}
		}

		if (Loader.isModLoaded(ModInfos.OPENCOMPUTERS_ID)) {
			try {
				CountCraft.instance.integrations.add(new OpenComputersHandler());
				event.getModLog().info("OpenComputers integration was loaded");
			} catch (Exception e) {
				event.getModLog().log(Level.WARN, "OpenComputers integration failed to load", e);
			}
		}

		for (IIntegrationHandler handler : CountCraft.instance.integrations) {
			ICounterFinder networkListener = handler.getCounterFinder();
			if (networkListener != null)
				CountCraft.instance.finders.add(networkListener);
		}
	}
}
