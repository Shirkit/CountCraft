package com.shirkit.countcraft.proxy;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.integration.IIntegrationHandler;
import com.shirkit.countcraft.gui.GuiCounter;
import com.shirkit.countcraft.gui.GuiIconProvider;
import com.shirkit.countcraft.render.BufferedRenderer;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter2;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ProxyClient extends Proxy {

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		// Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
		// your packets will not work because you will be getting a client
		// player even when you are on the server! Sounds absurd, but it's true.

		// Solution is to double-check side before returning the player:
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}

	@Override
	public void registerRenderers(FMLPostInitializationEvent event) {
		BufferedRenderer fluidRender = new BufferedRenderer(0.6f, 0.6f, 1.0f);
		BufferedRenderer itemRender = new BufferedRenderer(1.0f, 0.75f, 0.75f);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedItemCounter.class, itemRender);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedItemCounter2.class, itemRender);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBufferedFluidCounter.class, fluidRender);
		// MinecraftForgeClient.registerItemRenderer((Item)CountCraft.instance.blockCounter,
		// new BlockCounterRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CountCraft.instance.itemCounter), itemRender);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CountCraft.instance.newItemCounter), itemRender);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CountCraft.instance.fluidCounter), fluidRender);
	}

	@Override
	public void searchForIntegration(FMLPreInitializationEvent event) {
		super.searchForIntegration(event);

		// Then we search for any network/gui finders, if they have one
		for (IIntegrationHandler handler : CountCraft.instance.integrations) {

			IGuiListener guiListener = handler.getGuiListener();
			if (guiListener != null)
				GuiCounter.listeners.add(guiListener);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
		GuiIconProvider.getInstance().registerIcons(event.map);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {
	}

}