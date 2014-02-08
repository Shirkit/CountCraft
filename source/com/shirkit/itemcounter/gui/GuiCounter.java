package com.shirkit.itemcounter.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.shirkit.itemcounter.logic.Counter;
import com.shirkit.itemcounter.network.UpdateServerPacket;

import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiCounter extends GuiContainer {

	private static RenderItem render = new RenderItem();

	private enum Sorting {
		ID, Size, Name
	}

	private enum Average {
		None, Percentage, Second, Minute, Hour
	}

	static {
		render.zLevel = 100f;
	}

	/** Data **/
	private Counter counter;

	/** Elements **/
	private Button nextPage, previousPage, active, sort, average;

	/** Control stuff **/
	private int currentPage = 0;
	private int itemsPerPage = 8;
	private Comparator<ItemStack> comparer;
	private static Sorting currentSort = Sorting.ID;
	private static Average currentAverage = Average.None;
	private TileEntity tile;

	public GuiCounter(Counter counter, TileEntity tileEntity) {
		super(new ContainerCounter(counter, tileEntity));
		this.counter = counter;
		this.tile = tileEntity;
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

		int left = sr.getScaledWidth() / 5;
		int top = sr.getScaledHeight() / 7;
		int right = sr.getScaledWidth() / 3 * 2 + 5;
		int bottom = sr.getScaledHeight() / 8 * 7;

		nextPage = new Button(1, right - 10, bottom - 20, 10, 20, ">");
		buttonList.add(nextPage);

		previousPage = new Button(2, right - 60, bottom - 20, 10, 20, "<");
		buttonList.add(previousPage);

		active = new Button(3, left, bottom - 20, 50, 20, "Enabled");
		buttonList.add(active);

		sort = new Button(4, left + 50, bottom - 20, 30, 20, "Sort");
		buttonList.add(sort);

		average = new Button(5, left + 80, bottom - 20, 50, 20, "Average");
		buttonList.add(average);

		updateActive();
		updateAverage();
		updateSort();
	}

	private void updateActive() {
		if (counter.isActive()) {
			active.displayString = "Enabled";
			active.tooltip = "Currently counting items";
		} else {
			active.displayString = "Disabled";
			active.tooltip = "Not counting items";
		}
	}

	private void updateAverage() {
		switch (currentAverage) {
		case None:
			average.tooltip = "No average";
			break;

		case Percentage:
			average.tooltip = "Percentage average";
			break;

		case Second:
			average.tooltip = "Items per second average";
			break;

		case Minute:
			average.tooltip = "Items per minute average";
			break;

		case Hour:
			average.tooltip = "Items per hour average";
			break;
		}
	}

	private void updateSort() {
		switch (currentSort) {
		case ID:
			sort.tooltip = "Sorting by item ID";
			comparer = new IdComparer();
			break;

		case Name:
			sort.tooltip = "Sorting by item Name";
			comparer = new NameComparer();
			break;

		case Size:
			sort.tooltip = "Sorting by item Amount";
			comparer = new SizeComparer();
			break;
		}
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
		} else if (pressed == active) {
			counter.setActive(!counter.isActive());
			UpdateServerPacket toServer = new UpdateServerPacket(tile.xCoord, tile.yCoord, tile.zCoord, counter.isActive());
			try {
				PacketDispatcher.sendPacketToServer(toServer.getPacket());
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateActive();
		} else if (pressed == sort) {
			currentSort = Sorting.values()[(currentSort.ordinal() + 1) % Sorting.values().length];
			updateSort();
		} else if (pressed == average) {
			currentAverage = Average.values()[(currentAverage.ordinal() + 1) % Average.values().length];
			updateAverage();
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
		Collections.sort(set, comparer);

		for (int i = currentPage * itemsPerPage; i < currentPage * itemsPerPage + itemsPerPage && i < set.size(); i++) {

			ItemStack stack = set.get(i);
			int numbercolor = 255 << 16 | 255 << 8 | 170;
			float floatSize = stack.stackSize;

			String size = null;

			switch (currentAverage) {
			case None:
				size = String.format("%.0f", floatSize);
				break;

			case Percentage:
				floatSize = (floatSize / counter.getTotalItems()) * 100f;
				size = String.format("%.1f", floatSize).concat("%");
				break;

			case Second:
				floatSize /= (counter.getTicksRun() / 20f);
				size = String.format("%.2f", floatSize);
				break;

			case Minute:
				floatSize /= (counter.getTicksRun() / (20f * 60f));
				size = String.format("%.2f", floatSize);
				break;

			case Hour:
				floatSize /= (counter.getTicksRun() / (20f * 60f * 60f));
				size = String.format("%.2f", floatSize);
				break;
			}

			if (currentAverage == Average.None && floatSize > 1000) {
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
		drawCenteredString(mc.fontRenderer, page, distNX, bottom - 13, 16777215);

		// Draw tooltip

		Iterator iterator = buttonList.iterator();

		while (iterator.hasNext()) {
			Button btn = (Button) iterator.next();
			if (btn.isHover() && btn.tooltip != null) {
				List<String> text = new ArrayList<String>();
				text.add(btn.tooltip);
				drawHoveringText(text, mouseX - 155, mouseY - 25, mc.fontRenderer);
			}
		}

	}

	private class IdComparer implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o1.itemID - o2.itemID;
		}
	}

	private class NameComparer implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	}

	private class SizeComparer implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return o2.stackSize - o1.stackSize;
		}
	}
}
