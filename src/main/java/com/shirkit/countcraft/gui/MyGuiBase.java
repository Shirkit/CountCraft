package com.shirkit.countcraft.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.IStack;
import com.shirkit.countcraft.api.count.EnergyHandler;
import com.shirkit.countcraft.api.count.FluidHandler;
import com.shirkit.countcraft.api.count.ItemHandler;
import com.shirkit.countcraft.gui.elements.MyElementTextField;

import codechicken.lib.math.MathHelper;
import cofh.core.gui.element.TabAugment;
import cofh.core.gui.element.TabConfiguration;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class MyGuiBase extends GuiBase {

	public static final int LINES_PER_PAGE = 8;

	public static final class Buttons {
		public static final int size = 16;
		public static final int u = 0;
		public static final int v = 240;
	}

	public static final class Lines {
		public static final int offset = 6;
		public static final int u = 0;
		public static final int v = 222;
		public static final int xsize = 245;
		public static final int ysize = 17;
	}

	private static final String TEXTURE = "countcraft:textures/gui.png";
	private ICounterContainer counterProvider;
	private int size, totalPages, currentPage;
	private ElementButton previousPage, nextPage;
	private RenderItem render;

	public MyGuiBase(Container container) {
		super(container, new ResourceLocation(TEXTURE));
		counterProvider = ((MyGuiBaseContainer) container).entity;
	}

	private void update() {
		size = counterProvider.getCounter().size();
		totalPages = (size / LINES_PER_PAGE);
		totalPages++;

		if (currentPage > 0)
			previousPage.setEnabled(true);
		else
			previousPage.setEnabled(false);

		if (currentPage + 1 == totalPages)
			nextPage.setEnabled(false);
		else
			nextPage.setEnabled(true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);

		update();

		drawLines();
		
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		List<IStack> stacks = counterProvider.getCounter().entrySet();

		int left = 8;
		int lastY = 7;
		int nameDistanceFromIconLeft = 25;
		int nameDistanceFromIconTop = 5;
		int maxLineWidth = 150;

		for (int i = LINES_PER_PAGE * currentPage; i < LINES_PER_PAGE * currentPage + LINES_PER_PAGE && i < stacks.size(); i++) {
			IStack stack = counterProvider.getCounter().entrySet().get(i);
			
			String trueName = stack.getName();
			String displayName = fontRendererObj.trimStringToWidth(trueName, maxLineWidth);
			if (!trueName.equals(displayName))
				displayName = displayName.trim().concat("...");
			
			
			float stackSize = stack.getAmount();
			
			int count = 0;
			while (stackSize > 1000) {
				stackSize /= 1000;
				count++;
			}
			
			String suffix = "";
			int numbercolor = 0;
			
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
				suffix = "B" + suffix;
				numbercolor = 150 << 16 | 200 << 8 | 255;
				break;
			case 4:
				suffix = "T" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 255;
				break;
			case 5:
				suffix = "P" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 6:
				suffix = "E" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 7:
				suffix = "Z" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;
			case 8:
				suffix = "Y" + suffix;
				numbercolor = 255 << 16 | 180 << 8 | 150;
				break;

			default:
				suffix = "^" + (count * 3) + suffix;
				break;
			}
			
			String number = String.valueOf(stackSize);
			
			if (count > 0) {
				number = String.format("%.2f", stackSize);
			} else
				number = String.format("%.0f", stackSize);
			
			number = number.concat(suffix);
			
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			fontRendererObj.drawStringWithShadow(displayName, left + nameDistanceFromIconLeft, lastY + nameDistanceFromIconTop, 0xffffff);
			fontRendererObj.drawStringWithShadow(number, left + nameDistanceFromIconLeft + maxLineWidth + 14, lastY + nameDistanceFromIconTop, 0xffffff);
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

			lastY = lastY + 20;
		}
	}

	protected void drawLines() {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);

		int lines = currentPage + 1 == totalPages ? size - LINES_PER_PAGE * currentPage : LINES_PER_PAGE;

		for (int i = 0; i < lines; i++)
			drawTexturedModalRect(Lines.offset, Lines.offset + (Lines.ysize * i) + (i * Lines.offset / 2), Lines.u, Lines.v, Lines.xsize, Lines.ysize);
	}

	@Override
	public IIcon getIcon(String name) {
		return GuiIconProvider.getInstance().getIcon(name);
	}

	@Override
	public void initGui() {
		xSize = 256;
		ySize = 200;
		drawInventory = false;
		drawTitle = false;
		render = new RenderItem();
		render.zLevel = 100;

		super.initGui();

		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));

		addTab(new TabConfiguration(this, ((MyGuiBaseContainer) this.inventorySlots).entity));

		previousPage = new ElementButton(this, 5, ySize - 16 - 5, "Previous", Buttons.u + Buttons.size * 2, Buttons.v, Buttons.u + Buttons.size, Buttons.v, Buttons.u, Buttons.v, Buttons.size,
				Buttons.size, TEXTURE) {
			@Override
			public void onClick() {
				currentPage--;
				update();
			}
		};
		previousPage.setToolTip("Previous page");
		previousPage.setEnabled(false);

		nextPage = new ElementButton(this, xSize - 16 - 5, ySize - 16 - 5, "Next", Buttons.u + Buttons.size * 5, Buttons.v, Buttons.u + Buttons.size * 4, Buttons.v, Buttons.u + Buttons.size * 3,
				Buttons.v, Buttons.size, Buttons.size, TEXTURE) {
			@Override
			public void onClick() {
				currentPage++;
				update();
			}
		};
		nextPage.setToolTip("Next page");

		nextPage.setGuiManagedClicks(false);
		previousPage.setGuiManagedClicks(false);
		
		ElementTextField currentPageField = new MyElementTextField(this, xSize / 2 - 22, ySize - 19, 46, 15) {
			@Override
			public void addTooltip(List<String> list) {
				list.add("Current Page");
			}
		};
		currentPageField.setText("135/79");
		currentPageField.setTextColor(0xffffff, 0xffffff);

		addElement(previousPage);
		addElement(nextPage);
		addElement(currentPageField);

		update();
	}

}
