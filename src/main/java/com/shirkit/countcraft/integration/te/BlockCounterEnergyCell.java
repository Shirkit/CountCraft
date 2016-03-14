package com.shirkit.countcraft.integration.te;

import com.shirkit.countcraft.block.BlockBaseCounter;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCounterEnergyCell extends BlockBaseCounter {

	private float blockYCompression = 0.2F;

	private IIcon topIcon, sideIcon;

	protected BlockCounterEnergyCell() {
		this.setBlockName("countcraft.te.energybuffer").setBlockTextureName("countcraft:blockBufferedCounter");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileCounterEnergyCell tile = new TileCounterEnergyCell();
		return tile;
	}
}
