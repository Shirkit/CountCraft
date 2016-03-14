package com.shirkit.countcraft;

import java.util.ArrayList;
import java.util.List;

import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.block.BlockBufferedFluidCounter;
import com.shirkit.countcraft.block.BlockBufferedItemCounter;
import com.shirkit.countcraft.block.BlockBufferedItemCounter2;
import com.shirkit.countcraft.block.ItemBlockBufferedFluidCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedItemCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedItemCounter2;
import com.shirkit.countcraft.data.Options;
import com.shirkit.countcraft.gui.GuiHandler;
import com.shirkit.countcraft.integration.te.TileCounterEnergyCell;
import com.shirkit.countcraft.item.ItemBlockCounter;
import com.shirkit.countcraft.network.PacketDispatcher;
import com.shirkit.countcraft.proxy.Proxy;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Main class of the mod
 *
 * @author Pyeroh
 *
 */
@Mod(modid = ModInfos.ID, name = ModInfos.NAME, version = ModInfos.VERSION, dependencies = "after:CoFHCore;after:ThermalExpansion;after:" + ModInfos.OPENCOMPUTERS_ID)
public class CountCraft {

	public static final String CHANNEL = "COUNTCRAFT";

	/** Forge configuration **/
	@Instance(ModInfos.ID)
	public static CountCraft instance;

	public static List<String> loadedLocalizations;

	@SidedProxy(clientSide = "com.shirkit.countcraft.proxy.ProxyClient", serverSide = "com.shirkit.countcraft.proxy.Proxy")
	public static Proxy proxy;

	public List<ICounterFinder> finders = new ArrayList<ICounterFinder>();

	/** Integration **/
	public List<IIntegrationHandler> integrations = new ArrayList<IIntegrationHandler>();
	/** Mod **/

	public ItemBlockCounter blockCounter;
	public BlockBufferedItemCounter itemCounter;
	public BlockBufferedFluidCounter fluidCounter;
	public BlockBufferedItemCounter2 newItemCounter;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.init(event);
		}
	}

	@Mod.EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
		for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worldServers) {
			for (Object te : world.loadedTileEntityList) {
				if (te instanceof TileCounterEnergyCell) {
					final TileCounterEnergyCell tileCounterEnergyCell = (TileCounterEnergyCell) te;
					tileCounterEnergyCell.setDirty(true);
					tileCounterEnergyCell.updateEntity();
				}
			}
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

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/** Options and integration loading **/
		Options.load(event);
		proxy.searchForIntegration(event);

		/** Generic buffers **/
		itemCounter = new BlockBufferedItemCounter();
		GameRegistry.registerTileEntity(TileBufferedItemCounter.class, TileBufferedItemCounter.class.getName());

		fluidCounter = new BlockBufferedFluidCounter();
		GameRegistry.registerTileEntity(TileBufferedFluidCounter.class, TileBufferedFluidCounter.class.getName());

		blockCounter = new ItemBlockCounter();

		newItemCounter = new BlockBufferedItemCounter2();

		/** Registration **/
		PacketDispatcher.registerPackets();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerBlock(itemCounter, ItemBlockBufferedItemCounter.class, BlockBufferedItemCounter.class.getName());
		GameRegistry.registerBlock(fluidCounter, ItemBlockBufferedFluidCounter.class, BlockBufferedFluidCounter.class.getName());

		GameRegistry.registerItem(blockCounter, ItemBlockCounter.class.getName());
		
		GameRegistry.registerBlock(newItemCounter, ItemBlockBufferedItemCounter2.class, BlockBufferedItemCounter2.class.getName());

		/** Recipes **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCounter, 3), "iii", "drd", "ici", 'i', new ItemStack(Items.iron_ingot), 'r', new ItemStack(Items.comparator), 'c',
				new ItemStack(Blocks.chest), Character.valueOf('d'), "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fluidCounter, 3), "iii", "drd", "ici", 'i', new ItemStack(Items.iron_ingot), 'r', new ItemStack(Items.comparator), 'c',
				new ItemStack(Items.cauldron), Character.valueOf('d'), "dyeBlue"));

		/** Integration **/
		for (IIntegrationHandler mod : integrations) {
			mod.preInit(event);
		}
		
		MinecraftForge.EVENT_BUS.register(proxy);
	}

}
