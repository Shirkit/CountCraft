package com.shirkit.countcraft.integration.te;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.utils.SyncUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCounterEnergyCell extends BlockContainer {

	private Icon topIcon, sideIcon;

	protected BlockCounterEnergyCell(int id) {
		super(id, Material.iron);

		this.setHardness(2.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("countcraft.te.energybuffer").setTextureName("itemcounter:blockBufferedCounter");
		setCreativeTab(CountcraftTab.TAB);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
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
				SyncUtils.sendCounterUpdatePacket(tile, entityPlayer);
				entityPlayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, world, x, y, z);

			}
		return true;
	}

	@Override
	public Icon getIcon(int side, int meta) {
		if (meta == 0)
			return this.blockIcon;
		else {
			switch (side) {
			case 0:
			case 1:
				return topIcon;
			case 2:
			case 3:
			case 4:
			case 5:
				return sideIcon;
			default:
				throw new IllegalArgumentException("Sides must vary from 0-5, received: " + side);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		this.blockIcon = register.registerIcon("itemcounter:blockBufferedCounter_1");
		this.topIcon = register.registerIcon("itemcounter:blockBufferedCounter_top");
		this.sideIcon = register.registerIcon("itemcounter:blockBufferedCounter_side");
	}
}
