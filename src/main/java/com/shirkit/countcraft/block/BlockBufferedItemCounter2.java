package com.shirkit.countcraft.block;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.IUpgradeableTile;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.countcraft.tile.TileBufferedItemCounter;
import com.shirkit.countcraft.tile.TileBufferedItemCounter2;
import com.shirkit.countcraft.upgrade.UpgradeManager;
import com.shirkit.utils.SyncUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBufferedItemCounter2 extends BlockBufferedItemCounter {

	public BlockBufferedItemCounter2() {
		this.setBlockName("countcraft.itembuffer2").setBlockTextureName("countcraft:blockBufferedCounter");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileBufferedItemCounter buffer = new TileBufferedItemCounter2();

		return buffer;
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
				entityPlayer.openGui(CountCraft.instance, GuiID.TEST_GUI, world, x, y, z);
			}

		}
		return true;
	}

}
