package com.shirkit.itemcounter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import com.shirkit.itemcounter.block.BlockBufferedItemCounter;
import com.shirkit.itemcounter.gui.GuiHandler;
import com.shirkit.itemcounter.integration.IIntegrationHandler;
import com.shirkit.itemcounter.integration.buildcraft.IconProvider;
import com.shirkit.itemcounter.integration.buildcraft.PipeItemCounter;
import com.shirkit.itemcounter.network.PacketHandler;
import com.shirkit.itemcounter.tile.BufferedItemCounter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "ItemCounterModShirkit", name = "Item Counter", version = "0.1", dependencies = "after:BuildCraft|Transport")
@NetworkMod(channels = { ItemCounter.CHANNEL }, packetHandler = PacketHandler.class)
public class ItemCounter {

	public static final String CHANNEL = "COUNTER";

	/** Forge configuration **/
	@Instance
	public static ItemCounter instance;

	/** Integration **/
	public List<IIntegrationHandler> integrations = new ArrayList<IIntegrationHandler>();

	/** Mod **/

	public PipeItemCounter pipe;
	public Item builtPipe;
	public BlockBufferedItemCounter chest;
	public IconProvider iconProvider;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/** Generic buffer **/
		chest = new BlockBufferedItemCounter(3957);
		GameRegistry.registerTileEntity(BufferedItemCounter.class, "itemcounter.buffered.tile");

		/** Language **/
		GameRegistry.registerBlock(chest, "itemCounter.buffered");
		LanguageRegistry.addName(chest, "Buffered Item Counter");

		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.preInit(event);
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.init(event);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.postInit(event);
		}
	}
}
