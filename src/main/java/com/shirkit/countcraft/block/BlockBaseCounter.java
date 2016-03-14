package com.shirkit.countcraft.block;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBaseCounter extends BlockContainer {

	private float blockYCompression = 0.2F;

	private IIcon sideIcon;

	private IIcon topIcon;

	protected BlockBaseCounter() {
		super(Material.iron);

		this.setHardness(1.0F).setStepSound(soundTypeWood);
		setCreativeTab(CountcraftTab.TAB);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		ICounterContainer counter = (ICounterContainer) world.getTileEntity(x, y, z);

		if (counter != null) {
			world.notifyBlocksOfNeighborChange(x, y, z, block);
		}

		super.breakBlock(world, x, y, z, block, metadata);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public boolean canProvidePower() {
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

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side) {
		return 0;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side) {
		int metadata = access.getBlockMetadata(x, y, z);
		return metadata == 1 ? 15 : 0;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {

			ItemStack item = entityPlayer.getCurrentEquippedItem();
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof IUpgradeableTile && item != null && UpgradeManager.canUpgrade(item, (IUpgradeableTile) te)) {
				UpgradeManager.applyUpgrade(entityPlayer, (IUpgradeableTile) te);
			} else {
				ICounterContainer counter = (ICounterContainer) world.getTileEntity(x, y, z);
				SyncUtils.sendCounterUpdatePacket(counter, entityPlayer);
				entityPlayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, world, x, y, z);
			}

		}
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon("countcraft:blockBufferedCounter_1");
		this.topIcon = register.registerIcon("countcraft:blockBufferedCounter_top");
		this.sideIcon = register.registerIcon("countcraft:blockBufferedCounter_side");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds(0.0F, blockYCompression, 0.0F, 1.0F, 1.0F - blockYCompression, 1.0F);
	}

}
