package com.shirkit.countcraft.block;

import com.shirkit.countcraft.tile.TileBufferedFluidCounter;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBufferedFluidCounter extends BlockBaseCounter {

	public BlockBufferedFluidCounter() {
		this.setBlockName("countcraft.fluidbuffer").setBlockTextureName("countcraft:blockBufferedCounter");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileBufferedFluidCounter buffer = new TileBufferedFluidCounter();

		return buffer;
	}

}
