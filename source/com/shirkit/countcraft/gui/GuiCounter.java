package com.shirkit.countcraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.shirkit.countcraft.count.Counter;
import com.shirkit.countcraft.count.EnergyHandler;
import com.shirkit.countcraft.count.FluidHandler;
import com.shirkit.countcraft.count.ItemHandler;
import com.shirkit.countcraft.count.Stack;
import com.shirkit.countcraft.logic.ISideAware;
import com.shirkit.countcraft.logic.SideController;
import com.shirkit.countcraft.logic.SideState;
import com.shirkit.countcraft.network.UpdateServerPacket;

import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiCounter extends GuiContainer implements IGuiDrawer {

	private static RenderItem render = new RenderItem();
	public static List<IGuiListener> listeners = new ArrayList<IGuiListener>();

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
	private Button nextPage, previousPage, active, sort, average, config, back;
	private Button[] sideButton;
	private Map<Button, IGuiListener> extraButtons;

	/** Control stuff **/
	private static boolean atOptions = false;

	private static Sorting currentSort = Sorting.ID;
	private static Average currentAverage = Average.None;

	private int currentPage = 0;
	private int itemsPerPage = 8;
	private int lastOptButtonY = 0;
	private int freeId = 0;

	private Comparator<Stack> comparer;
	private String nameFilter;

	private TileEntity tile;

	public GuiCounter(Counter counter, TileEntity tileEntity) {
		super(new ContainerCounter(counter, tileEntity));
		this.counter = counter;
		this.tile = tileEntity;
		this.allowUserInput = false;
		sideButton = new Button[ForgeDirection.VALID_DIRECTIONS.length];
		extraButtons = new HashMap<Button, IGuiListener>();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		for (IGuiListener listener : listeners) {
			listener.onScreenPreDraw(this);
		}
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
		previousPage = new Button(2, right - 60, bottom - 20, 10, 20, "<");

		active = new Button(3, left, top, 50, 20, "Enabled");
		sort = new Button(4, left, top + 25, 50, 20, "Sort");
		average = new Button(5, left, top + 50, 50, 20, "Average");
		back = new Button(5, right - 60, bottom - 20, 50, 20, "Back");

		config = new Button(6, left, bottom - 20, 50, 20, "Config");
		config.tooltip = "Configure the interface";

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			sideButton[dir.ordinal()] = new Button(7 + dir.ordinal(), right - 60, top + (dir.ordinal() * 25), 50, 20, "");
		}

		freeId = sideButton[sideButton.length - 1].id + 1;
		lastOptButtonY = top + 75;

		for (IGuiListener listener : listeners) {
			listener.onGuiOpen(this);
		}

		updateActive();
		updateAverage();
		updateSort();
		updateOptions();
		if (tile instanceof ISideAware)
			updateSides();
	}

	public void setNameFilter(String filter) {
		if (filter != null && !filter.isEmpty())
			this.nameFilter = filter.toLowerCase();
		else
			this.nameFilter = null;
	}

	public Button addButtonToOptions(IGuiListener addingController) {
		Button button = new Button(freeId, average.xPosition, lastOptButtonY, 50, 20, "");
		button.xPosition = average.xPosition;
		button.yPosition = lastOptButtonY;
		lastOptButtonY += 25;
		freeId++;
		extraButtons.put(button, addingController);
		return button;
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

	private void updateOptions() {
		if (atOptions) {
			buttonList.clear();
			buttonList.add(average);
			buttonList.add(active);
			buttonList.add(sort);
			buttonList.add(back);
			if (tile instanceof ISideAware)
				for (Button sideBtn : sideButton) {
					buttonList.add(sideBtn);
				}
			for (Button btn : extraButtons.keySet()) {
				buttonList.add(btn);
			}
		} else {
			buttonList.clear();
			buttonList.add(previousPage);
			buttonList.add(nextPage);
			buttonList.add(config);
		}
	}

	private void updateSides() {
		ISideAware iSideAware = (ISideAware) tile;
		SideController controller = iSideAware.getSideController();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			SideState state = controller.getState(dir);
			Button btn = sideButton[dir.ordinal()];
			btn.displayString = state.name();
		}
	}

	private void updateSide(Button pressed) {
		ISideAware iSideAware = (ISideAware) tile;
		SideController controller = iSideAware.getSideController();
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			if (sideButton[side.ordinal()] == pressed) {
				SideState curState = controller.getState(side);
				SideState next = SideState.values()[(curState.ordinal() + 1) % 4];
				if (next == SideState.Anything && !controller.canAnything()) {
					next = SideState.values()[(next.ordinal() + 1) % 4];
				}
				controller.setState(side, next);
				pressed.displayString = next.name();
				return;
			}
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
			sendUpdatePacketToServer();
			updateActive();
		} else if (pressed == sort) {
			currentSort = Sorting.values()[(currentSort.ordinal() + 1) % Sorting.values().length];
			updateSort();
		} else if (pressed == average) {
			currentAverage = Average.values()[(currentAverage.ordinal() + 1) % Average.values().length];
			updateAverage();
		} else if (pressed == config) {
			atOptions = true;
			updateOptions();
		} else if (pressed == back) {
			atOptions = false;
			updateOptions();
		} else {
			for (Button btn : sideButton) {
				if (pressed == btn) {
					updateSide(btn);
					sendUpdatePacketToServer();
					return;
				}
			}
			for (Entry<Button, IGuiListener> entry : extraButtons.entrySet()) {
				if (entry.getKey() == pressed)
					entry.getValue().onButtonPress(this, pressed);
			}
		}
	}

	private void sendUpdatePacketToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(Counter.ACTIVE_TAG, counter.isActive());
		if (tile instanceof ISideAware) {
			SideController controller = ((ISideAware) tile).getSideController();
			controller.writeToNBT(tag);
		}

		UpdateServerPacket toServer = new UpdateServerPacket(tile.xCoord, tile.yCoord, tile.zCoord, tag);
		try {
			PacketDispatcher.sendPacketToServer(toServer.getPacket());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (atOptions)
			drawOptions(mouseX, mouseY);
		else
			drawMain(mouseX, mouseY);

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

	private void drawOptions(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

		int left = sr.getScaledWidth() / 5 - this.guiLeft;
		int top = sr.getScaledHeight() / 7 - this.guiTop;
		int right = sr.getScaledWidth() / 3 * 2 + 5 - this.guiLeft;
		int bottom = sr.getScaledHeight() / 8 * 7 - this.guiTop;

		int stepY = (bottom - top) / 9;
		int distNX = (right - 30);

		int lastY = top + 7;
		int lastX = right - 100;

		int lastYbtn = top + 90 + guiTop;
		int lastXbtn = left + guiLeft;

		int font = 255 << 16 | 255 << 8 | 255;
		int color = 128 << 24 | 0 << 16 | 0 << 8 | 0;

		if (tile instanceof ISideAware) {
			ISideAware iSideAware = (ISideAware) tile;
			SideController controller = iSideAware.getSideController();
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				drawString(fontRenderer, dir.name(), lastX, lastY, 16777215);
				lastY += 25;
			}

		}
	}

	private void drawMain(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

		int left = sr.getScaledWidth() / 5 - this.guiLeft;
		int top = sr.getScaledHeight() / 7 - this.guiTop;
		int right = sr.getScaledWidth() / 3 * 2 + 5 - this.guiLeft;
		int bottom = sr.getScaledHeight() / 8 * 7 - this.guiTop;
		int stepY = (bottom - top) / 9;
		int distNX = (right - 30);

		int lastY = top;

		int maxPages = Math.max(counter.size() / itemsPerPage, 1);

		List<Stack> set = counter.entrySet();
		Collections.sort(set, comparer);

		for (int i = currentPage * itemsPerPage; i < currentPage * itemsPerPage + itemsPerPage && i < set.size(); i++) {

			Stack stack = set.get(i);
			int numbercolor = 255 << 16 | 255 << 8 | 170;
			float floatSize = stack.getAmount();

			String name = stack.getName();

			String size = null;
			String suffix = "";

			switch (currentAverage) {
			case None:
				size = String.format("%.0f", floatSize);
				break;

			case Percentage:
				floatSize = (floatSize / counter.getTotalCounted()) * 100f;
				size = String.format("%.1f", floatSize);
				suffix = "%";
				break;

			case Second:
				floatSize /= (counter.getTicksRun() / 20f);
				size = String.format("%.3f", floatSize);
				suffix = "/s";
				break;

			case Minute:
				floatSize /= (counter.getTicksRun() / (20f * 60f));
				size = String.format("%.2f", floatSize);
				suffix = "/m";
				break;

			case Hour:
				floatSize /= (counter.getTicksRun() / (20f * 60f * 60f));
				size = String.format("%.1f", floatSize);
				suffix = "/h";
				break;
			}

			if (floatSize > 1000) {
				floatSize = floatSize / 1000;
				if (floatSize > 1000) {
					floatSize = floatSize / 1000;
					suffix = "M" + suffix;
					numbercolor = 175 << 16 | 255 << 8 | 175;
				} else {
					suffix = "k" + suffix;
					numbercolor = 200 << 16 | 200 << 8 | 255;
				}
				size = String.format("%.2f", floatSize);
			}
			size = size.concat(suffix);

			if (name.length() > sr.getScaleFactor() * 6)
				name = name.substring(0, sr.getScaleFactor() * 6).concat("...");

			if (nameFilter == null || nameFilter.startsWith("@") || name.toLowerCase().contains(nameFilter)) {
				// Disable lighting for text
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				fontRenderer.drawStringWithShadow(name, left + 20, lastY + 8, 16777215);
				drawCenteredString(fontRenderer, size, distNX, lastY + 8, numbercolor);
				// Enable lighting for items
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor3f(1f, 1f, 1f);
				if (stack instanceof ItemHandler)
					render.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, (ItemStack) ((ItemHandler) stack).getStack(), left, lastY);
				else if (stack instanceof FluidHandler) {
					FluidStack fluid = (FluidStack) ((FluidHandler) stack).getStack();
					Icon icon = fluid.getFluid().getIcon();
					if (icon != null) {
						mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(0));
						drawTexturedModelRectFromIcon(left, lastY, icon, 16, 16);
					}
				} else if (stack instanceof EnergyHandler) {
				}
			}

			lastY += stepY;
		}

		if (Tessellator.instance.isDrawing)
			Tessellator.instance.draw();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		String page = (currentPage + 1) + "/" + maxPages;
		drawCenteredString(mc.fontRenderer, page, distNX, bottom - 13, 16777215);
	}

	private static class IdComparer implements Comparator<Stack> {

		@Override
		public int compare(Stack o1, Stack o2) {
			return o1.getId().toString().compareTo(o2.getId().toString());
		}
	}

	private static class NameComparer implements Comparator<Stack> {

		@Override
		public int compare(Stack o1, Stack o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	private static class SizeComparer implements Comparator<Stack> {

		@Override
		public int compare(Stack o1, Stack o2) {
			return o2.getAmount() - o1.getAmount();
		}
	}
}
