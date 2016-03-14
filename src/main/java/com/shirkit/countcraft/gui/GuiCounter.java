package com.shirkit.countcraft.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.shirkit.countcraft.api.ESideState;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.IComplexCounter;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.api.integration.IGuiDrawer;
import com.shirkit.countcraft.api.integration.IGuiListener;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.network.PacketDispatcher;
import com.shirkit.countcraft.network.server.UpdateServerMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class GuiCounter extends GuiContainer implements IGuiDrawer {

	private enum Average {
		Hour, Minute, None, Percentage, Second, Tick
	}

	private static class IdComparer implements Comparator<IStack> {

		@Override
		public int compare(IStack o1, IStack o2) {
			return o1.getId().toString().compareTo(o2.getId().toString());
		}
	}

	private static class NameComparer implements Comparator<IStack> {

		@Override
		public int compare(IStack o1, IStack o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	private static class SizeComparer implements Comparator<IStack> {

		@Override
		public int compare(IStack o1, IStack o2) {
			return (int) (o2.getAmount() - o1.getAmount());
		}
	}

	private enum Sorting {
		ID, Name, Size
	}

	/** Control stuff **/
	private static boolean atOptions = false;

	private static Average currentAverage = Average.None;

	private static Sorting currentSort = Sorting.ID;

	public static List<IGuiListener> listeners = new ArrayList<IGuiListener>();

	private static RenderItem render = new RenderItem();

	static {
		render.zLevel = 100f;
	}

	private Comparator<IStack> comparer;

	/** Data **/
	private ICounter counter;

	private int currentPage = 0;

	private Map<Button, IGuiListener> extraButtons;

	private int freeId = 0;

	private int itemsPerPage = 8;

	private int lastOptButtonY = 0;

	private String nameFilter;

	/** Elements **/
	private Button nextPage, previousPage, active, sort, average, config, back, complex;

	private Button[] sideButton;

	private TileEntity tile;

	public GuiCounter(ICounter counter, TileEntity tileEntity) {
		super(new ContainerCounter(counter, tileEntity));
		this.counter = counter;
		this.tile = tileEntity;
		this.allowUserInput = false;
		sideButton = new Button[ForgeDirection.VALID_DIRECTIONS.length];
		extraButtons = new HashMap<Button, IGuiListener>();
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

			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int top = sr.getScaledHeight() / 7;
			updateLastOptButtonY(top);

			updateOptions();
		} else if (pressed == back) {
			atOptions = false;

			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int top = sr.getScaledHeight() / 7;
			updateLastOptButtonY(top);

			updateOptions();
		} else if (pressed == complex) {
			IComplexCounter counter2 = (IComplexCounter) counter;
			counter2.setComplex(!counter2.isComplex());
			sendUpdatePacketToServer();
			updateComplex();
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

	public Button addButtonToOptions(IGuiListener addingController) {
		Button button = new Button(freeId, average.xPosition, lastOptButtonY, 50, 20, "");
		button.xPosition = average.xPosition;
		button.yPosition = lastOptButtonY;
		freeId++;
		extraButtons.put(button, addingController);
		return button;
	}

	@Override
	public boolean containsButton(GuiButton button) {
		boolean contains = false;
		contains = contains || nextPage.equals(button);
		contains = contains || previousPage.equals(button);
		contains = contains || active.equals(button);
		contains = contains || sort.equals(button);
		contains = contains || average.equals(button);
		contains = contains || config.equals(button);
		contains = contains || back.equals(button);
		contains = contains || complex.equals(button);
		for (Button side : sideButton) {
			contains = contains || side.equals(button);
		}
		contains = contains || extraButtons.containsKey(button);
		return contains;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		int back = sr.getScaledWidth() / 5;
		int top = sr.getScaledHeight() / 7;
		int right = sr.getScaledWidth() / 3 * 2 + 5;
		int bottom = sr.getScaledHeight() / 8 * 7;

		int color = 32 << 24 | 255 << 16 | 255 << 8 | 255;
		int color2 = 64 << 24 | 255 << 16 | 255 << 8 | 255;
		drawRect(back - 7, top - 7, right + 7, bottom + 7, color);
		drawRect(back - 5, top - 5, right + 5, bottom + 5, color2);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (atOptions)
			drawOptions(mouseX, mouseY);
		else
			drawMain(mouseX, mouseY);

		// Draw tooltip

		Iterator<Button> iterator = buttonList.iterator();

		while (iterator.hasNext()) {
			Button btn = iterator.next();
			if (btn.isHover() && btn.tooltip != null) {
				List<String> text = new ArrayList<String>();
				text.add(btn.tooltip);
				drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
			}
		}
	}

	private void drawMain(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		int left = sr.getScaledWidth() / 5 - this.guiLeft;
		int top = sr.getScaledHeight() / 7 - this.guiTop;
		int right = sr.getScaledWidth() / 3 * 2 + 5 - this.guiLeft;
		int bottom = sr.getScaledHeight() / 8 * 7 - this.guiTop;
		int stepY = (bottom - top) / 9;
		int distNX = (right - 30);

		int lastY = top;

		int maxPages = Math.max((int) Math.ceil((double) counter.size() / (double) itemsPerPage), 1);

		List<IStack> set = counter.entrySet();
		Collections.sort(set, comparer);

		for (int i = currentPage * itemsPerPage; i < currentPage * itemsPerPage + itemsPerPage && i < set.size(); i++) {

			IStack stack = set.get(i);
			int numbercolor = 255 << 16 | 255 << 8 | 170;
			double floatSize = stack.getAmount();

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

			case Tick:
				floatSize /= counter.getTicksRun();
				size = String.format("%.3f", floatSize);
				suffix = "/t";
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

			int count = 0;
			/*
			 * while (floatSize > 1000) { floatSize = floatSize / 1000; count++;
			 * }
			 */
			switch (count) {

			case 0:
				break;
			case 1:
				suffix = "k" + suffix;
				numbercolor = 255 << 16 | 150 << 8 | 150;
				break;
			case 2:
				suffix = "M" + suffix;
				numbercolor = 150 << 16 | 255 << 8 | 150;
				break;
			case 3:
				suffix = " B" + suffix;
				numbercolor = 150 << 16 | 200 << 8 | 255;
				break;
			case 4:
				suffix = "T" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 255;
				break;
			case 5:
				suffix = " Quad" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 6:
				suffix = " Quint" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 7:
				suffix = " Sext" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 8:
				suffix = " Sept" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;

			default:

				suffix = "x10^" + (count * 3) + suffix;

				break;
			}

			if (count > 0)
				size = String.format("%.2f", floatSize);

			size = size.concat(suffix);

			if (name.length() - 5 > sr.getScaleFactor() * 6)
				name = name.substring(0, sr.getScaleFactor() * 6).concat("...");

			if (nameFilter == null || nameFilter.startsWith("@") || name.toLowerCase().contains(nameFilter)) {
				// Disable lighting for text
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				fontRendererObj.drawStringWithShadow(name, left + 20, lastY + 8, 16777215);
				drawCenteredString(fontRendererObj, size, distNX, lastY + 8, numbercolor);
				// Enable lighting for items
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor3f(1f, 1f, 1f);
				if (stack instanceof ItemHandler)
					render.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, (ItemStack) ((ItemHandler) stack).getStack(), left, lastY);
				else if (stack instanceof FluidHandler) {
					FluidStack fluid = (FluidStack) ((FluidHandler) stack).getStack();
					IIcon icon = fluid.getFluid().getIcon();
					if (icon != null) {
						mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(0));
						drawTexturedModelRectFromIcon(left, lastY, icon, 16, 16);
					}
				} else if (stack instanceof EnergyHandler) {
				}
			}

			lastY += stepY;
		}

		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.draw();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		String page = (currentPage + 1) + "/" + maxPages;
		drawCenteredString(mc.fontRenderer, page, distNX, bottom - 13, 16777215);
	}

	private void drawOptions(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		// int left = sr.getScaledWidth() / 5 - this.guiLeft;
		int top = sr.getScaledHeight() / 7 - this.guiTop;
		int right = sr.getScaledWidth() / 3 * 2 + 5 - this.guiLeft;
		// int bottom = sr.getScaledHeight() / 8 * 7 - this.guiTop;

		// int stepY = (bottom - top) / 9;
		// int distNX = (right - 30);

		int lastY = top + 7;
		int lastX = right - 100;

		// int lastYbtn = top + 90 + guiTop;
		// int lastXbtn = left + guiLeft;

		// int font = 255 << 16 | 255 << 8 | 255;
		// int color = 128 << 24 | 0 << 16 | 0 << 8 | 0;

		if (tile instanceof ISideAware) {
			// ISideAware iSideAware = (ISideAware) tile;
			// SideController controller = iSideAware.getSideController();
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				drawString(fontRendererObj, dir.name(), lastX, lastY, 16777215);
				lastY += 25;
			}

		}
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
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		int left = sr.getScaledWidth() / 5;
		int top = sr.getScaledHeight() / 7;
		int right = sr.getScaledWidth() / 3 * 2 + 5;
		int bottom = sr.getScaledHeight() / 8 * 7;

		nextPage = new Button(1, right - 10, bottom - 20, 10, 20, ">");
		previousPage = new Button(2, right - 60, bottom - 20, 10, 20, "<");

		active = new Button(3, left, top, 50, 20, "Enabled");
		sort = new Button(4, left, top + 25, 50, 20, "Sort");
		average = new Button(5, left, top + 50, 50, 20, "Average");

		complex = new Button(7, left, top + 75, 50, 20, "Upgrade");
		updateLastOptButtonY(top);

		back = new Button(5, right - 60, bottom - 20, 50, 20, "Back");

		config = new Button(6, left, bottom - 20, 50, 20, "Config");
		config.tooltip = "Configure the interface";

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			sideButton[dir.ordinal()] = new Button(8 + dir.ordinal(), right - 60, top + (dir.ordinal() * 25), 50, 20, "");
		}

		freeId = sideButton[sideButton.length - 1].id + 1;

		for (IGuiListener listener : listeners) {
			listener.onGuiOpen(this);
		}

		updateActive();
		updateAverage();
		updateSort();
		updateOptions();
		if (tile instanceof ISideAware)
			updateSides();
		if (counter instanceof IComplexCounter)
			updateComplex();
	}

	private void sendUpdatePacketToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(ICounter.ACTIVE_TAG, counter.isActive());
		if (counter instanceof IComplexCounter) {
			tag.setBoolean(IComplexCounter.COMPLEX_TAG, ((IComplexCounter) counter).isComplex());
		}
		if (tile instanceof ISideAware) {
			SideController controller = ((ISideAware) tile).getSideController();
			controller.writeToNBT(tag);
		}

		PacketDispatcher.sendToServer(new UpdateServerMessage(tile.xCoord, tile.yCoord, tile.zCoord, tag));
	}

	public void setNameFilter(String filter) {
		if (filter != null && !filter.isEmpty())
			this.nameFilter = filter.toLowerCase();
		else
			this.nameFilter = null;
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

		case Tick:
			average.tooltip = "Items per tick";
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

	private void updateComplex() {
		if (counter instanceof IComplexCounter) {
			IComplexCounter counter2 = (IComplexCounter) counter;

			if (counter2.isComplex())
				complex.tooltip = "Using installed upgrade";
			else
				complex.tooltip = "Ignoring installed upgrade";
		}
	}

	/**
	 * @param top
	 */
	private void updateLastOptButtonY(int top) {
		if (counter instanceof IComplexCounter) {
			lastOptButtonY = top + 100;
		} else
			lastOptButtonY = top + 75;
	}

	@SuppressWarnings("unchecked")
	private void updateOptions() {
		if (atOptions) {
			buttonList.clear();
			buttonList.add(average);
			buttonList.add(active);
			buttonList.add(sort);
			buttonList.add(back);
			if (counter instanceof IComplexCounter)
				buttonList.add(complex);
			if (tile instanceof ISideAware)
				for (Button sideBtn : sideButton) {
					buttonList.add(sideBtn);
				}
			for (Button btn : extraButtons.keySet()) {
				btn.xPosition = average.xPosition;
				btn.yPosition = lastOptButtonY;
				lastOptButtonY += 25;
				buttonList.add(btn);
			}
		} else {
			buttonList.clear();
			buttonList.add(previousPage);
			buttonList.add(nextPage);
			buttonList.add(config);
		}
	}

	private void updateSide(Button pressed) {
		ISideAware iSideAware = (ISideAware) tile;
		SideController controller = iSideAware.getSideController();
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			if (sideButton[side.ordinal()] == pressed) {
				ESideState curState = controller.getState(side);
				ESideState next = ESideState.values()[(curState.ordinal() + 1) % 4];
				if (next == ESideState.Anything && !controller.canAnything()) {
					next = ESideState.values()[(next.ordinal() + 1) % 4];
				}
				controller.setState(side, next);
				pressed.displayString = next.name();
				return;
			}
		}
	}

	private void updateSides() {
		ISideAware iSideAware = (ISideAware) tile;
		SideController controller = iSideAware.getSideController();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			ESideState state = controller.getState(dir);
			Button btn = sideButton[dir.ordinal()];
			btn.displayString = state.name();
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
}
