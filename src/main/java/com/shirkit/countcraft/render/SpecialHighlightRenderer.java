package com.shirkit.countcraft.render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex3d;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

public class SpecialHighlightRenderer {

	private static SpecialHighlightRenderer instance;
	public static SpecialHighlightRenderer getInstance() {
		if (instance == null)
			instance = new SpecialHighlightRenderer();
		return instance;
	}
	private long duration, originalTime;
	private double minx, miny, minz, maxx, maxy, maxz;

	private int[] search;

	protected SpecialHighlightRenderer() {
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onWorldRender(RenderWorldLastEvent evt) {
		if (System.currentTimeMillis() < originalTime + duration) {

			RenderBlocks blocks = new RenderBlocks(Minecraft.getMinecraft().theWorld);

			Tessellator tessellator = Tessellator.instance;

			float t = evt.partialTicks;
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

			double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * t;
			double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * t;
			double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * t;

			glPushMatrix();
			glTranslated(-dx, -dy, -dz);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glColor4f(1, 1, 1, 0.5f);

			for (int i = 0; i < search.length / 3; i++) {

				glPushMatrix();

				glTranslated(search[i * 3] + 0.5f, search[i * 3 + 1] + 0.5f, search[i * 3 + 2] + 0.5f);

				glBegin(GL_QUADS);

				glVertex3d(maxx, maxy, minz);
				glVertex3d(minx, maxy, minz);
				glVertex3d(minx, maxy, maxz);
				glVertex3d(maxx, maxy, maxz);

				glVertex3d(maxx, miny, maxz);
				glVertex3d(minx, miny, maxz);
				glVertex3d(minx, miny, minz);
				glVertex3d(maxx, miny, minz);

				glVertex3d(maxx, maxy, maxz);
				glVertex3d(minx, maxy, maxz);
				glVertex3d(minx, miny, maxz);
				glVertex3d(maxx, miny, maxz);

				glVertex3d(maxx, miny, minz);
				glVertex3d(minx, miny, minz);
				glVertex3d(minx, maxy, minz);
				glVertex3d(maxx, maxy, minz);

				glVertex3d(minx, maxy, maxz);
				glVertex3d(minx, maxy, minz);
				glVertex3d(minx, miny, minz);
				glVertex3d(minx, miny, maxz);

				glVertex3d(maxx, maxy, minz);
				glVertex3d(maxx, maxy, maxz);
				glVertex3d(maxx, miny, maxz);
				glVertex3d(maxx, miny, minz);

				glEnd();

				glPopMatrix();
			}

			glDisable(GL_BLEND);

			glPopMatrix();
		} else
			MinecraftForge.EVENT_BUS.unregister(this);
	}

	public SpecialHighlightRenderer setRenderer(int[] search, long duration) {
		this.search = search;
		this.duration = duration;
		this.originalTime = System.currentTimeMillis();

		maxx = maxy = maxz = minx = miny = minz = 0.5;

		maxx *= 1.01;
		maxy *= 1.01;
		maxz *= 1.01;
		minx *= -1.01;
		miny *= -1.01;
		minz *= -1.01;

		return this;
	}

}
