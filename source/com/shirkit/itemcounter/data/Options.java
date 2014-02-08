package com.shirkit.itemcounter.data;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Options {

	/** Static **/
	public static Options instance;

	/** Instance **/
	private Configuration config;

	/** Interface **/
	public static int ITEM_PIPEITEMCOUNTER = 5003;
	public static int BLOCK_BUFFEREDCOUNTER = 3957;

	public static void load(FMLPreInitializationEvent event) {
		instance = new Options();
		instance.config = new Configuration(new File(event.getModConfigurationDirectory(), "itemcounter.cfg"));

		instance.config.addCustomCategoryComment("BlockID", "IDs for blocks");
		BLOCK_BUFFEREDCOUNTER = instance.config.get("BlockID", "block.buffered_item_counter", BLOCK_BUFFEREDCOUNTER).getInt();

		instance.config.addCustomCategoryComment("ItemID", "IDs for items (remember that they get shifted by 256)");
		ITEM_PIPEITEMCOUNTER = instance.config.get("ItemID", "item.pipe_item_counter", ITEM_PIPEITEMCOUNTER).getInt();

		instance.config.save();
	}

}
