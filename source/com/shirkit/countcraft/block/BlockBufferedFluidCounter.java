package com.shirkit.countcraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.tile.TileBufferedFluidCounter;
import com.shirkit.utils.SyncUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBufferedFluidCounter extends BlockContainer {

	private Random random = new Random();
	private IIcon topIcon;
	private IIcon sideIcon;

	public BlockBufferedFluidCounter(int par1) {
		super(Material.iron);

		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		setBlockName("countcraft.fluidbuffer");
		setBlockTextureName("countcraft:blockBufferedCounter");
		setCreativeTab(CountcraftTab.TAB);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileBufferedFluidCounter buffer = new TileBufferedFluidCounter();

		return buffer;
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
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return super.canPlaceBlockAt(par1World, par2, par3, par4);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		TileBufferedFluidCounter buffer = (TileBufferedFluidCounter) world.getTileEntity(x, y, z);

		if (buffer != null) {
			// FIXME
			//world.func_96440_m(par2, par3, par4, par5);
		}

		super.breakBlock(world, x, y, z, block, metadata);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
		if (!world.isRemote)
			if (entityPlayer.isSneaking()) {

				TileBufferedFluidCounter tank = (TileBufferedFluidCounter) world.getTileEntity(x, y, z);
				SyncUtils.sendCounterUpdatePacket(tank, entityPlayer);
				entityPlayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, world, x, y, z);

			}
		return true;
	}

	/**
	 * 0 = -y, 1 = +y, 2 = -z, 3 = +z, 4 = -x, 5 = +x
	 */
	@Override
	public IIcon getIcon(int side, int meta) {
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
	public void registerIcons(IIconRegister register) {
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon("countcraft:blockBufferedCounter_1");
		this.topIcon = register.registerIcon("countcraft:blockBufferedCounter_top");
		this.sideIcon = register.registerIcon("countcraft:blockBufferedCounter_side");
	}

}
