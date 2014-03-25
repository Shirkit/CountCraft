package com.shirkit.countcraft;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.Level;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.block.BlockBufferedFluidCounter;
import com.shirkit.countcraft.block.BlockBufferedItemCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedFluidCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedItemCounter;
import com.shirkit.countcraft.data.Options;
import com.shirkit.countcraft.gui.GuiHandler;
import com.shirkit.countcraft.network.PacketPipeline;
import com.shirkit.countcraft.network.UpdateCounterPacket;
import com.shirkit.countcraft.proxy.Proxy;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;
import com.shirkit.utils.FileUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "CountCraft", name = "CountCraft", version = "0.1", dependencies = "after:BuildCraft|Transport,ThermalExpansion")
public class CountCraft {

	public static final String CHANNEL = "COUNTCRAFT";
	public static final String LOCALIZATIONS_FOLDER = "/lang/countcraft/";
	public static List<String> loadedLocalizations;

	/** Forge configuration **/
	@Instance
	public static CountCraft instance;

	@SidedProxy(clientSide = "com.shirkit.countcraft.proxy.ProxyClient", serverSide = "com.shirkit.countcraft.proxy.Proxy")
	public static Proxy proxy;
	
	public static final PacketPipeline PACKET_PIPELINE = new PacketPipeline();

	/** Integration **/
	public List<IIntegrationHandler> integrations = new ArrayList<IIntegrationHandler>();
	public List<ICounterFinder> finders = new ArrayList<ICounterFinder>();

	/** Mod **/

	public BlockBufferedItemCounter chest;
	public BlockBufferedFluidCounter tank;
	public Item chestItem, tankItem;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/** Options and integration loading **/
		Options.load(event);
		proxy.searchForIntegration(event);

		/** Generic buffers **/
		chest = new BlockBufferedItemCounter(Options.BLOCK_BUFFEREDITEMCOUNTER);
		GameRegistry.registerTileEntity(TileBufferedItemCounter.class, TileBufferedItemCounter.class.getName());

		tank = new BlockBufferedFluidCounter(Options.BLOCK_BUFFEREDFLUIDCOUNTER);
		GameRegistry.registerTileEntity(TileBufferedFluidCounter.class, TileBufferedFluidCounter.class.getName());

		/** Registration **/
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerBlock(chest, ItemBlockBufferedItemCounter.class, chest.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(tank, ItemBlockBufferedFluidCounter.class, tank.getUnlocalizedName().replace("tile.", ""));
		
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(chest);
		chestItem = GameRegistry.findItem(id.modId, id.name);
		id = GameRegistry.findUniqueIdentifierFor(tank);
		tankItem = GameRegistry.findItem(id.modId, id.name);

		/** Recipes **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chest, 3), "iii", "drd", "ici", 'i', new ItemStack(Items.iron_ingot), 'r', new ItemStack(Items.comparator), 'c', new ItemStack(
				Blocks.chest), Character.valueOf('d'), "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(tank, 3), "iii", "drd", "ici", 'i', new ItemStack(Items.iron_ingot), 'r', new ItemStack(Items.comparator), 'c', new ItemStack(
				Items.cauldron), Character.valueOf('d'), "dyeBlue"));

		/** Localization **/
		URL localizations = this.getClass().getResource(LOCALIZATIONS_FOLDER);
		loadedLocalizations = new ArrayList<String>();
		if (localizations != null) {
			try {
				InputStream localizationsEntries = localizations.openStream();
				Properties languages = new Properties();
				languages.load(localizationsEntries);

				if (languages.isEmpty()) {
					// This is Runtime, we need another way of loading
					String[] listing = FileUtils.getResourceListing(getClass(), LOCALIZATIONS_FOLDER);
					ArrayList<String> files = new ArrayList<String>();
					for (String string : listing) {
						if (!string.startsWith("buildcraft") && string.endsWith(".properties")) {
							files.add(string);
						}
					}
					for (String string : files) {
						URL resource = getClass().getResource(LOCALIZATIONS_FOLDER + string);
						if (resource != null) {
							Properties lang = new Properties();
							lang.load(resource.openStream());
							loadedLocalizations.add(string);
							LanguageRegistry.instance().addStringLocalization(lang, string.replace(".properties", ""));
						}
					}
				} else {
					// Dev environment, piece of cake
					for (Entry<Object, Object> entry : languages.entrySet()) {
						String langauge = entry.getKey().toString();
						URL resource = this.getClass().getResource(LOCALIZATIONS_FOLDER + langauge);
						if (resource != null) {
							Properties lang = new Properties();
							lang.load(resource.openStream());
							loadedLocalizations.add(langauge);
							LanguageRegistry.instance().addStringLocalization(lang, langauge.replace(".properties", ""));
						}
					}
				}
			} catch (Exception e) {
				event.getModLog().log(Level.ERROR, "Couldn't load localizations", e);
			}
		}

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.preInit(event);
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		
		PACKET_PIPELINE.initialise();
		
		PACKET_PIPELINE.registerPacket(UpdateCounterPacket.class);

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.init(event);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		proxy.registerRenderers(event);
		
		PACKET_PIPELINE.postInitialise();

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.postInit(event);
		}
	}
}
