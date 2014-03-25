package com.shirkit.countcraft.upgrade;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;

public class UpgradeManager {

	private static final HashMap<Upgrade, IUpgrade> upgrades;
	private static Upgrade temp;

	static {
		upgrades = new HashMap<Upgrade, IUpgrade>();
		temp = new Upgrade();
		registerUpgrade(new ItemStack(Items.clock), new TimerUpgrade());
	}

	public static void loadUpgrade(Upgrade upgrade, IUpgradeableTile tile) {
		IUpgrade iUpgrade = upgrades.get(upgrade);
		iUpgrade.onLoad(tile);
	}

	public static boolean canUpgrade(ItemStack item, IUpgradeableTile tile) {
		IUpgrade upgrade = get(item);
		if (upgrade != null) {
			return upgrade.canApply(tile);
		}
		return false;
	}

	public static void applyUpgrade(EntityPlayer entityPlayer, IUpgradeableTile tile) {
		ItemStack item = entityPlayer.getCurrentEquippedItem();

		IUpgrade upgrade = get(item);
		tile.registerUpgrade(upgrade, item);
		upgrade.onApply(tile);

		item.stackSize--;
		if (item.stackSize <= 0)
			entityPlayer.destroyCurrentEquippedItem();
	}

	private static void registerUpgrade(ItemStack stack, IUpgrade upgrade) {
		upgrades.put(new Upgrade(stack), upgrade);
	}

	private static IUpgrade get(ItemStack item) {
		temp.stack = item;
		return upgrades.get(temp);
	}
}
