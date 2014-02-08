package com.shirkit.itemcounter.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCounter extends ModelBase {

	public ModelRenderer block;

	public ModelCounter() {
		block = (new ModelRenderer(this, 0, 0).setTextureSize(64, 64));
		block.setTextureOffset(0, 0);
		block.addBox(0.0F, 6.0F, 0.0F, 32, 20, 32);
	}

	public void renderAll() {
		block.render(0.0625F);
	}
}
