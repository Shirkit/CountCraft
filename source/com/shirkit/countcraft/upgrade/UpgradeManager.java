package com.shirkit.countcraft.upgrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.shirkit.countcraft.api.IUpgrade;
import com.shirkit.countcraft.api.IUpgradeableTile;

public class UpgradeManager {

	private static final HashMap<Upgrade, IUpgrade> upgrades;
	private static Upgrade temp;

	static {
		upgrades = new HashMap<Upgrade, IUpgrade>();
		temp = new Upgrade();
		temp.stack = new ItemStack(Item.appleGold);
		registerUpgrade(new ItemStack(Item.pocketSundial), new TimerUpgrade());
		registerUpgrade(new ItemStack(Block.lever), new RedstoneEmiterUpgrade());
	}

	public static void saveUpgrade(IUpgrade upgrade, NBTTagCompound tag) {
		tag.setString("class", upgrade.getClass().getName());
		upgrade.writeToNBT(tag);
	}

	public static void loadUpgrade(NBTTagCompound tag, IUpgradeableTile tile) {
		Iterator<IUpgrade> iterator = tile.getUpgrades().iterator();
		String string = tag.getString("class");
		if (string != null && !string.isEmpty()) {
			boolean exist = false;
			while (iterator.hasNext()) {
				IUpgrade next = iterator.next();
				if (next.getClass().getName().equals(string)) {
					next.readFromNBT(tag);
					exist = true;
				}
			}
			// TODO instead of instantiating here, we should send a packet when
			// an upgrade is created at the server side and send that to the
			// client
			if (!exist) {
				try {
					IUpgrade instance = getInstance(Class.forName(string));
					if (instance.canApply(tile)) {
						
						tile.registerUpgrade(instance);
						instance.onApply(tile);
						
						instance.readFromNBT(tag);
					}
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean canUpgrade(ItemStack item, IUpgradeableTile tile) {
		IUpgrade upgrade = get(item);
		if (upgrade != null) {
			return upgrade.canApply(tile);
		}
		return false;
	}

	private static IUpgrade getInstance(Class clazz) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Constructor<? extends IUpgrade> constructor = clazz.getConstructor(new Class[] {});
		IUpgrade iUpgrade = constructor.newInstance(new Object[] {});
		return iUpgrade;
	}

	public static void applyUpgrade(EntityPlayer entityPlayer, IUpgradeableTile tile) {
		ItemStack item = entityPlayer.getCurrentEquippedItem();

		IUpgrade upgrade = get(item);
		try {
			upgrade = getInstance(upgrade.getClass());
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
		tile.registerUpgrade(upgrade);
		upgrade.onApply(tile);

		item.stackSize--;
		if (item.stackSize <= 0)
			entityPlayer.destroyCurrentEquippedItem();
	}

	private static void registerUpgrade(ItemStack stack, IUpgrade upgrade) {
		upgrades.put(new Upgrade(stack), upgrade);
	}

	private static IUpgrade get(ItemStack item) {
		temp.stack.itemID = item.itemID;
		temp.stack.setItemDamage(item.getItemDamage());
		return upgrades.get(temp);
	}
}
