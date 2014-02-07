package com.shirkit.itemcounter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.IIconProvider;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;

import com.shirkit.itemcounter.block.BlockBufferedItemCounter;
import com.shirkit.itemcounter.gui.GuiHandler;
import com.shirkit.itemcounter.integration.buildcraft.IconProvider;
import com.shirkit.itemcounter.integration.buildcraft.PipeItemCounter;
import com.shirkit.itemcounter.network.PacketHandler;
import com.shirkit.itemcounter.network.Proxy;
import com.shirkit.itemcounter.tile.BufferedItemCounter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "ItemCounterModShirkit", name = "Item Counter", version = "0.1", dependencies = "required-after:BuildCraft|Transport")
@NetworkMod(channels = { ItemCounter.CHANNEL }, packetHandler = PacketHandler.class)
public class ItemCounter {

	public static final String CHANNEL = "COUNTER";

	/** Forge configuration **/
	@Instance
	public static ItemCounter instance;

	/** Mod **/

	public PipeItemCounter pipe;
	public Item builtPipe;
	public BlockBufferedItemCounter chest;
	public IIconProvider iconProvider = new IconProvider();

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		if (event.getSide().isClient()) {
			Localization.addLocalization("/lang/itemcounter/", "en_US");
		}

		Proxy.proxy.initializeTileEntities();

		Proxy.proxy.initializeEntityRenders();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		pipe = new PipeItemCounter(5003);
		chest = new BlockBufferedItemCounter(3957);

		//builtPipe = BuildCraftTransport.buildPipe(pipe.itemID, PipeItemCounter.class, "Item Counter Transport Pipe", chest, Block.glass, chest);//

		String name = Character.toLowerCase(PipeItemCounter.class.getSimpleName().charAt(0)) + PipeItemCounter.class.getSimpleName().substring(1);

		int id = pipe.itemID;
		builtPipe = BlockGenericPipe.registerPipe(id, PipeItemCounter.class);
		builtPipe.setUnlocalizedName(PipeItemCounter.class.getSimpleName());
		LanguageRegistry.addName(builtPipe, "Item Counter Transport Pipe");

		GameRegistry.registerBlock(chest, "itemCounter.buffered");
		GameRegistry.registerTileEntity(BufferedItemCounter.class, "itemcounter.buffered.tile");

		LanguageRegistry.addName(chest, "Buffered Item Counter");

		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
	}
}
