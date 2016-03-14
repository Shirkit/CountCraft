package com.shirkit.countcraft.block;

import java.util.Random;

import com.shirkit.countcraft.tile.TileBufferedItemCounter;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBufferedItemCounter extends BlockBaseCounter {

	private Random random = new Random();

	public BlockBufferedItemCounter() {
		this.setBlockName("countcraft.itembuffer").setBlockTextureName("countcraft:blockBufferedCounter");
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
						entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2),
								new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
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
		}

		super.breakBlock(world, x, y, z, block, metadata);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileBufferedItemCounter buffer = new TileBufferedItemCounter();

		return buffer;
	}

}
