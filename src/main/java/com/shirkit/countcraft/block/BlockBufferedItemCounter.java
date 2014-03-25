package com.shirkit.countcraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

public class BlockBufferedItemCounter extends BlockContainer {

	private Random random = new Random();
	private IIcon topIcon;
	private IIcon sideIcon;

	public BlockBufferedItemCounter(int par1) {
		super(Material.iron);

		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		setBlockName("countcraft.itembuffer");
		setCreativeTab(CountcraftTab.TAB);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileBufferedItemCounter buffer = new TileBufferedItemCounter();

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
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		TileBufferedItemCounter buffer = (TileBufferedItemCounter) world.getTileEntity(x, y, z);

		if (buffer != null) {
			for (int j1 = 0; j1 < buffer.getSizeInventory(); ++j1) {
				ItemStack itemstack = buffer.getStackInSlot(j1);

				if (itemstack != null) {
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
						int k1 = this.random.nextInt(21) + 10;

						if (k1 > itemstack.stackSize) {
							k1 = itemstack.stackSize;
						}

						itemstack.stackSize -= k1;
						entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2), new ItemStack(itemstack.getItem(), k1,
								itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}
					}
				}
			}

			// FIXME
			// world.func_96440_m(par2, par3, par4, par5);
		}

		super.breakBlock(world, x, y, z, block, metadata);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (entityPlayer.isSneaking()) {

				TileBufferedItemCounter chest = (TileBufferedItemCounter) te;
				SyncUtils.sendCounterUpdatePacket(chest, entityPlayer);
				entityPlayer.openGui(CountCraft.instance, GuiID.COUNTER_GUI, world, x, y, z);

			} else {

				ItemStack item = entityPlayer.getCurrentEquippedItem();
				if (te instanceof IUpgradeableTile && item != null && UpgradeManager.canUpgrade(item, (IUpgradeableTile) te)) {
					UpgradeManager.applyUpgrade(entityPlayer, (IUpgradeableTile) te);
				} else {
					IInventory iinventory = (IInventory) te;

					if (iinventory != null) {
						entityPlayer.displayGUIChest(iinventory);
					}
				}

			}
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

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon("countcraft:blockBufferedCounter_1");
		this.topIcon = register.registerIcon("countcraft:blockBufferedCounter_top");
		this.sideIcon = register.registerIcon("countcraft:blockBufferedCounter_side");
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side) {
		int metadata = access.getBlockMetadata(x, y, z);
		return metadata == 1 ? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side) {
		return 0;
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
}