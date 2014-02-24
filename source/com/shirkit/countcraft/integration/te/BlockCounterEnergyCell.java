package com.shirkit.countcraft.integration.te;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.gui.GuiID;

public class BlockCounterEnergyCell extends BlockContainer {

	protected BlockCounterEnergyCell(int id) {
		super(id, Material.wood);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		TileCounterEnergyCell tile = new TileCounterEnergyCell();
		return tile;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
		if (!world.isRemote)
			if (entityPlayer.isSneaking()) {

				TileCounterEnergyCell tile = (TileCounterEnergyCell) world.getBlockTileEntity(x, y, z);
				tile.sendContents(world, entityPlayer);
				entityPlayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, world, x, y, z);

			}
		return true;
	}
}
