package com.shirkit.countcraft.data;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class Options {

	/** Static **/
	public static Options instance;

	/** Interface **/
	// public static int ITEM_BC_PIPEITEMCOUNTER = 4880 - 256;
	// public static int ITEM_BC_PIPEFLUIDCOUNTER = 4881 - 256;
	// public static int BLOCK_BUFFEREDITEMCOUNTER = 3880;
	// public static int BLOCK_BUFFEREDFLUIDCOUNTER = 3881;
	// public static int BLOCK_TE_BUFFEREDENERGYCOUNTER = 3882;

	public static void load(FMLPreInitializationEvent event) {
		instance = new Options();
		instance.config = new Configuration(new File(event.getModConfigurationDirectory(), "countcraft.cfg"));

		// instance.config.addCustomCategoryComment("BlockID",
		// "IDs for blocks");
		// BLOCK_BUFFEREDITEMCOUNTER = instance.config.get("BlockID",
		// "block.buffered_item_counter", BLOCK_BUFFEREDITEMCOUNTER).getInt();
		// BLOCK_BUFFEREDFLUIDCOUNTER = instance.config.get("BlockID",
		// "block.buffered_fluid_counter", BLOCK_BUFFEREDFLUIDCOUNTER).getInt();
		// BLOCK_TE_BUFFEREDENERGYCOUNTER = instance.config.get("BlockID",
		// "block.buffered_te_energy_counter",
		// BLOCK_TE_BUFFEREDENERGYCOUNTER).getInt();

		// instance.config.addCustomCategoryComment("ItemID",
		// "IDs for items (remember that they get shifted by 256 when loaded)");
		// ITEM_BC_PIPEITEMCOUNTER = instance.config.get("ItemID",
		// "item.pipe_item_counter", ITEM_BC_PIPEITEMCOUNTER).getInt();
		// ITEM_BC_PIPEFLUIDCOUNTER = instance.config.get("ItemID",
		// "item.pipe_fluid_counter", ITEM_BC_PIPEFLUIDCOUNTER).getInt();

		instance.config.save();
	}

	/** Instance **/
	private Configuration config;

}
