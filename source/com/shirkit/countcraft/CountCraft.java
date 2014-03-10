package com.shirkit.countcraft;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.block.BlockBufferedFluidCounter;
import com.shirkit.countcraft.block.BlockBufferedItemCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedFluidCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedItemCounter;
import com.shirkit.countcraft.data.Options;
import com.shirkit.countcraft.gui.GuiHandler;
import com.shirkit.countcraft.network.PacketHandler;
import com.shirkit.countcraft.proxy.Proxy;
import com.shirkit.countcraft.render.BufferedRenderer;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;
import com.shirkit.utils.FileUtils;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "CountCraft", name = "CountCraft", version = "0.1", dependencies = "after:BuildCraft|Transport,ThermalExpansion")
@NetworkMod(channels = { CountCraft.CHANNEL }, packetHandler = PacketHandler.class)
public class CountCraft {

	public static final String CHANNEL = "COUNTCRAFT";
	public static final String LOCALIZATIONS_FOLDER = "/lang/countcraft/";
	public static List<String> loadedLocalizations;

	/** Forge configuration **/
	@Instance
	public static CountCraft instance;
	
	@SidedProxy(clientSide = "com.shirkit.countcraft.proxy.ProxyClient", serverSide = "com.shirkit.countcraft.proxy.Proxy")
	public static Proxy proxy;

	/** Integration **/
	public List<IIntegrationHandler> integrations = new ArrayList<IIntegrationHandler>();
	public List<ICounterFinder> finders = new ArrayList<ICounterFinder>();

	/** Mod **/

	public BlockBufferedItemCounter chest;
	public BlockBufferedFluidCounter tank;

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
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerBlock(chest, ItemBlockBufferedItemCounter.class, "countcraft." + BlockBufferedItemCounter.class.getName());
		GameRegistry.registerBlock(tank, ItemBlockBufferedFluidCounter.class, "countcraft." + BlockBufferedFluidCounter.class.getName());

		/** Recipes **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chest, 8), "iii", "drd", "ici", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(
				Item.comparator), 'c', new ItemStack(Block.chest), Character.valueOf('d'), "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(tank, 8), "iii", "drd", "ici", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(
				Item.comparator), 'c', new ItemStack(Item.cauldron), Character.valueOf('d'), "dyeBlue"));

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
				event.getModLog().log(Level.SEVERE, "Couldn't load localizations", e);
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

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.init(event);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		proxy.registerRenderers(event);

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.postInit(event);
		}
	}
}
