package com.shirkit.countcraft.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.network.ISyncCapable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BufferedRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

	private static final int TICKS_TO_CHANGE = 5;
	private static final int NUMBER_OF_TEXTURES = 5;
	private static final int TOTAL = TICKS_TO_CHANGE * NUMBER_OF_TEXTURES;

	private static final ResourceLocation[] TEXTURES;
	private static final String BASE = "countcraft:textures/blocks/blockBufferedCounter_";
	private static final String EXTENSION = ".png";

	static {
		ResourceLocation[] helper = new ResourceLocation[NUMBER_OF_TEXTURES];
		TEXTURES = new ResourceLocation[NUMBER_OF_TEXTURES * TICKS_TO_CHANGE];
		for (int i = 0; i < helper.length; i++) {
			helper[i] = new ResourceLocation(BASE.concat(String.valueOf(i + 1)).concat(EXTENSION));
		}

		for (int i = 0, j = 0; i < TEXTURES.length; i += TICKS_TO_CHANGE, j++) {
			for (int k = i; k < i + TICKS_TO_CHANGE; k++) {
				TEXTURES[k] = helper[j];
			}
		}
	}

	/** The normal small chest model. */
	private ModelCounter counter = new ModelCounter();
	private int last = 0;
	private Block block;
	private float red;
	private float green;
	private float blue;

	public BufferedRenderer(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		block = CountCraft.instance.chest;
	}

	/**
	 * Renders the TileEntity for the chest at a position.
	 */
	public void renderTileEntityChestAt(ISyncCapable buffer, double par2, double par4, double par6, float par8) {

		this.bindTexture(TEXTURES[(int) (buffer.getTicksRun() % TOTAL)]);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) par2, (float) par4 + 1.0F, (float) par6 + 1.0F);
		GL11.glScalef(0.5F, -0.5F, -0.5F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		GL11.glColor3f(red, green, blue);
		counter.renderAll();

		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void renderTileEntityAt(TileEntity buffer, double par2, double par4, double par6, float par8) {
		this.renderTileEntityChestAt((ISyncCapable) buffer, par2, par4, par6, par8);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
		case ENTITY:
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		// this.bindTexture(TEXTURES[0]);

		RenderBlocks render = (RenderBlocks) data[0];
		render.setRenderBoundsFromBlock(block);

		Tessellator tessellator = Tessellator.instance;

		/**
		 * 0 = -y, 1 = +y, 2 = -z, 3 = +z, 4 = -x, 5 = +x
		 */

		GL11.glTranslatef(0f, -0.2f, 0f);
		GL11.glColor3f(red, green, blue);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		render.renderFaceYNeg(block, 0.0D, 0.5D, 0.0D, block.getIcon(0, 1));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		render.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, 1));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		render.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, 1));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		render.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, 1));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		render.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, 1));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		render.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, 1));
		tessellator.draw();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
