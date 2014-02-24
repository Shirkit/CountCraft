package com.shirkit.countcraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.shirkit.countcraft.block.BlockBufferedFluidCounter;
import com.shirkit.countcraft.block.BlockBufferedItemCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedFluidCounter;
import com.shirkit.countcraft.block.ItemBlockBufferedItemCounter;
import com.shirkit.countcraft.data.Options;
import com.shirkit.countcraft.gui.GuiHandler;
import com.shirkit.countcraft.integration.IIntegrationHandler;
import com.shirkit.countcraft.network.PacketHandler;
import com.shirkit.countcraft.proxy.Proxy;
import com.shirkit.countcraft.render.BufferedRenderer;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "CountCraftModShirkit", name = "CountCraft", version = "0.1", dependencies = "after:BuildCraft|Transport")
@NetworkMod(channels = { CountCraft.CHANNEL }, packetHandler = PacketHandler.class)
public class CountCraft {

	public static final String CHANNEL = "COUNTCRAFT";

	/** Forge configuration **/
	@Instance
	public static CountCraft instance;

	/** Integration **/
	public List<IIntegrationHandler> integrations = new ArrayList<IIntegrationHandler>();

	/** Mod **/

	public BlockBufferedItemCounter chest;
	public BlockBufferedFluidCounter tank;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/** Options and integration loading **/
		Options.load(event);
		Proxy.proxy.searchForIntegration(event);

		/** Generic buffer **/
		chest = new BlockBufferedItemCounter(Options.BLOCK_BUFFEREDITEMCOUNTER);
		GameRegistry.registerTileEntity(TileBufferedItemCounter.class, TileBufferedItemCounter.class.getName());

		tank = new BlockBufferedFluidCounter(Options.BLOCK_BUFFEREDFLUIDCOUNTER);
		GameRegistry.registerTileEntity(TileBufferedFluidCounter.class, TileBufferedFluidCounter.class.getName());

		/** Registration **/
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerBlock(chest, ItemBlockBufferedItemCounter.class, "itemCounter." + BlockBufferedItemCounter.class.getName());
		GameRegistry.registerBlock(tank, ItemBlockBufferedFluidCounter.class, "itemCounter." + BlockBufferedFluidCounter.class.getName());

		/** Recipes **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chest, 8), "iii", "drd", "ici", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Item.comparator), 'c',
				new ItemStack(Block.chest), Character.valueOf('d'), "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(tank, 8), "iii", "drd", "ici", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Item.comparator), 'c',
				new ItemStack(Item.cauldron), Character.valueOf('d'), "dyeBlue"));

		/** Localization **/
		LanguageRegistry.addName(chest, "Buffered Item Counter");
		LanguageRegistry.addName(tank, "Buffered Fluid Counter");

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

		if (event.getSide().isClient()) {
			BufferedRenderer fluidRender = new BufferedRenderer(0.6f, 0.6f, 1.0f);
			BufferedRenderer itemRender = new BufferedRenderer(1.0f, 0.75f, 0.75f);
			ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedItemCounter.class, itemRender);
			ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedFluidCounter.class, fluidRender);
			MinecraftForgeClient.registerItemRenderer(chest.blockID, itemRender);
			MinecraftForgeClient.registerItemRenderer(tank.blockID, fluidRender);
		}
	}
}