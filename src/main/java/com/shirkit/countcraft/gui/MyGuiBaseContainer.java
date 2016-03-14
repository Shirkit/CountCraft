package com.shirkit.countcraft.gui;

import com.shirkit.countcraft.tile.TileBufferedItemCounter2;

import cofh.core.gui.slot.SlotAugment;
import cofh.lib.gui.container.ContainerBase;
import cofh.lib.gui.container.ContainerInventoryItem;
import cofh.lib.gui.container.IAugmentableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class MyGuiBaseContainer extends ContainerBase implements IAugmentableContainer {

	protected TileBufferedItemCounter2 entity;
	private SlotAugment[] slots;

	public MyGuiBaseContainer(TileEntity entity, InventoryPlayer player) {
		this.entity = (TileBufferedItemCounter2) entity;
		slots = new SlotAugment[this.entity.getAugmentSlots().length];
		int x = 0, y = 0;
		for (int i = 0; i < slots.length; i++) {
			slots[i] = new SlotAugment(this.entity, this.entity, i, x, y);
			x++;
			if (x > 3) {
				x = 0;
				y++;
			}
			
			addSlotToContainer(slots[i]);
		}
	}

	@Override
	public void setAugmentLock(boolean lock) {
	}

	@Override
	public Slot[] getAugmentSlots() {
		return slots;
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {
		return 84;
	}
	
	@Override
	protected int getPlayerInventoryHorizontalOffset() {
		return super.getPlayerInventoryHorizontalOffset();
	}

	@Override
	protected int getSizeInventory() {
		return slots.length;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
