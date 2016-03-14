package com.shirkit.countcraft.integration.te;

import com.shirkit.countcraft.api.integration.ICounterFinder;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.proxy.IIntegrationProxy;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ThermalExpansionHandler implements IIntegrationHandler {

	public static ThermalExpansionHandler instance;

	@SidedProxy(clientSide = "com.shirkit.countcraft.integration.te.ProxyClient", serverSide = "com.shirkit.countcraft.integration.te.Proxy")
	public static IIntegrationProxy proxy;

	public BlockCounterEnergyCell energycell;

	public ThermalExpansionHandler() {
		instance = this;
	}

	@Override
	public ICounterFinder getCounterFinder() {
		return null;
	}

	@Override
	public IGuiListener getGuiListener() {
		return null;
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		ItemStack itemStack = GameRegistry.findItemStack("ThermalExpansion", "cellBasicFrame", 8);
		if (itemStack == null) {
			Item item = GameRegistry.findItem("ThermalExpansion", "cellBasicFrame");
			if (item != null)
				itemStack = new ItemStack(item, 8);
		}

		if (itemStack != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energycell, 3), "iii", "ycy", "ibi", 'i', Items.iron_ingot, 'y', "dyeYellow", 'c', Items.comparator, 'b', itemStack));

		proxy.registerRender(event);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		energycell = new BlockCounterEnergyCell();

		GameRegistry.registerBlock(energycell, ItemBlockCounterEnergyCell.class, BlockCounterEnergyCell.class.getName());
		GameRegistry.registerTileEntity(TileCounterEnergyCell.class, TileCounterEnergyCell.class.getName());
	}

}
