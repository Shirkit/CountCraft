package com.shirkit.itemcounter.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.shirkit.itemcounter.logic.Counter;

public class GuiCounter extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private static RenderItem render = new RenderItem();

	static {
		render.zLevel = 100f;
	}

	private Counter counter;
	private Button nextPage, previousPage;
	private int currentPage = 0;
	private int itemsPerPage = 8;

	public GuiCounter(Counter counter) {
		super(new ContainerCounter(counter));
		this.counter = counter;
		this.allowUserInput = false;

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
	}

	@Override
	public void initGui() {
		super.initGui();

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

		int back = sr.getScaledWidth() / 5;
		int top = sr.getScaledHeight() / 7;
		int right = sr.getScaledWidth() / 3 * 2 + 5;
		int bottom = sr.getScaledHeight() / 8 * 7;

		nextPage = new Button(1, right - 10, bottom - 20, 10, 20, ">");
		nextPage.tooltip = "Next page";
		buttonList.add(nextPage);

		previousPage = new Button(2, right - 60, bottom - 20, 10, 20, "<");
		previousPage.tooltip = "Previous page";
		buttonList.add(previousPage);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

		int back = sr.getScaledWidth() / 5;
		int top = sr.getScaledHeight() / 7;
		int right = sr.getScaledWidth() / 3 * 2 + 5;
		int bottom = sr.getScaledHeight() / 8 * 7;

		int color = 32 << 24 | 255 << 16 | 255 << 8 | 255;
		int color2 = 64 << 24 | 255 << 16 | 255 << 8 | 255;
		drawRect(back - 7, top - 7, right + 7, bottom + 7, color);
		drawRect(back - 5, top - 5, right + 5, bottom + 5, color2);
	}

	@Override
	protected void actionPerformed(GuiButton pressed) {
		if (pressed == nextPage) {
			if ((currentPage + 1) * itemsPerPage < counter.size())
				currentPage++;
		} else if (pressed == previousPage) {
			if (currentPage > 0)
				currentPage--;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

		int left = sr.getScaledWidth() / 5 - this.guiLeft;
		int top = sr.getScaledHeight() / 7 - this.guiTop;
		int right = sr.getScaledWidth() / 3 * 2 + 5 - this.guiLeft;
		int bottom = sr.getScaledHeight() / 8 * 7 - this.guiTop;
		int stepY = (bottom - top) / 9;
		int distNX = (right - 30);

		int lastY = top;

		int font = 255 << 16 | 255 << 8 | 255;
		int color = 128 << 24 | 0 << 16 | 0 << 8 | 0;
		int maxPages = counter.size() / itemsPerPage + 1;

		List<ItemStack> set = counter.entrySet();
		Collections.sort(set, new IdComparer());

		for (int i = currentPage * itemsPerPage; i < currentPage * itemsPerPage + itemsPerPage && i < set.size(); i++) {

			ItemStack stack = set.get(i);
			stack.stackSize = i * i * i * i * i * i * i * i * 9 + 17;
			String size = String.valueOf(stack.stackSize);
			int numbercolor = 255 << 16 | 255 << 8 | 170;

			float floatSize = stack.stackSize;
			if (floatSize > 1000) {
				String sufix = "";
				floatSize = floatSize / 1000;
				if (floatSize > 1000) {
					floatSize = floatSize / 1000;
					sufix = "M";
					numbercolor = 175 << 16 | 255 << 8 | 175;
				} else {
					sufix = "k";
					numbercolor = 200 << 16 | 200 << 8 | 255;
				}
				size = String.format("%.2f", floatSize) + sufix;
			}

			String name = stack.getDisplayName();
			if (name.length() > sr.getScaleFactor() * 6)
				name = name.substring(0, sr.getScaleFactor() * 6).concat("...");

			// Disable lighting for text
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			mc.fontRenderer.drawStringWithShadow(name, left + 20, lastY + 8, 16777215);
			// mc.fontRenderer.drawStringWithShadow(size, left + 21 + distNX,
			// lastY + 8, numbercolor);
			drawCenteredString(mc.fontRenderer, size, distNX, lastY + 8, numbercolor);
			// Enable lighting for items
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			render.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, left, lastY);

			lastY += stepY;
		}

		if (Tessellator.instance.isDrawing)
			Tessellator.instance.draw();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		String page = (currentPage + 1) + "/" + maxPages;
		// mc.fontRenderer.drawStringWithShadow(page, 134, 161, 16777215);
		drawCenteredString(mc.fontRenderer, page, distNX, bottom - 13, 16777215);

		// Draw tooltip
		/*
		 * Iterator iterator = buttonList.iterator();
		 * 
		 * while (iterator.hasNext()) { Button btn = (Button) iterator.next();
		 * if (btn.isHover()) { List<String> text = new ArrayList<String>();
		 * text.add(btn.tooltip); drawHoveringText(text, x - 155, y - 25,
		 * mc.fontRenderer); } }
		 */
	}

	private class IdComparer implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o1.itemID - o2.itemID;
		}

	}
}
