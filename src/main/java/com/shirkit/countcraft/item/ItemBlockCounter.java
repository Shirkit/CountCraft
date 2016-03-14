package com.shirkit.countcraft.item;

import com.shirkit.countcraft.CountCraft;
import com.shirkit.countcraft.data.CountcraftTab;
import com.shirkit.countcraft.gui.GuiID;
import com.shirkit.utils.tasks.TaskFindSameBlocksNearby;
import com.shirkit.utils.tasks.TaskScheduler;

import cofh.api.item.IInventoryContainerItem;
import cofh.api.tileentity.IAugmentable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemBlockCounter extends Item {

	public ItemBlockCounter() {
		super();

		this.setUnlocalizedName("countcraft.blockcounter");
		setTextureName("countcraft:itemBlockCounter");
		this.maxStackSize = 1;
		this.setCreativeTab(CountcraftTab.TAB);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		// Only goes here if the item is used on air (can be on entity as well)

		if (world.isRemote) {
			// Client side
			System.out.println("Air click");
		} else {
			// Server side
		}

		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
		// Only goes here if onItemUseFirst returns false

		if (world.isRemote) {
			// Client side
			System.out.println("ItemUse Client");
		} else {
			// Server side
			System.out.println("ItemUse Server");
		}

		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		// Goes here whenever the player clicks on a block
		if (world.isRemote) {
			// Client side
			Block block = world.getBlock(x, y, z);
			int metadata = world.getBlockMetadata(x, y, z);

			if (!TaskScheduler.getInstance().hasTask(TaskFindSameBlocksNearby.class)) {
				TaskScheduler.getInstance().addTask(player, new TaskFindSameBlocksNearby(world, player.getUniqueID(), 1000, x, y, z, block, metadata, 1));
			} else
				player.addChatComponentMessage(new ChatComponentText("Wait a moment..."));

			return true;
		} else {
			return true;
			// Server side
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {

	}

}
