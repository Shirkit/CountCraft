package com.shirkit.utils.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.shirkit.countcraft.render.SpecialHighlightRenderer;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TaskFindSameBlocksNearby implements ITask {

	protected int blocksPerTick;
	private HashMap<String, Boolean> closedMap;
	protected int dimension;

	protected int maxDistance;
	private LinkedList<String> openMap;
	protected Block originBlock;

	protected int originX, originY, originZ, originMetadata;
	protected UUID playerUUID;

	public TaskFindSameBlocksNearby(World world, UUID playerUUID, int blocksPerTick, int originX, int originY, int originZ, Block originBlock, int originMetadata, int maxDistance) {
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.originBlock = originBlock;
		this.originMetadata = originMetadata;
		this.maxDistance = maxDistance;
		this.dimension = world.provider.dimensionId;
		this.playerUUID = playerUUID;
		this.blocksPerTick = blocksPerTick;
	}

	@Override
	public boolean canExecute(Event event) {
		if (event instanceof TickEvent && ((TickEvent) event).side == Side.CLIENT && ((TickEvent) event).type == Type.PLAYER && ((TickEvent) event).phase == Phase.END) {
			return ((PlayerTickEvent) event).player.worldObj.provider.dimensionId == this.dimension && ((PlayerTickEvent) event).player.getUniqueID().equals(this.playerUUID);
		}

		return false;
	}

	@Override
	public boolean execute(Event event) {
		int tick = 0;
		World world = ((PlayerTickEvent) event).player.worldObj;

		while (!openMap.isEmpty()) {

			String id = openMap.remove(0);
			String[] split = id.split(" ");

			int x = Integer.parseInt(split[0]);
			int y = Integer.parseInt(split[1]);
			int z = Integer.parseInt(split[2]);

			Block block = world.getBlock(x, y, z);
			int metadata = world.getBlockMetadata(x, y, z);

			if (block.equals(originBlock) && originMetadata == metadata) {

				closedMap.put(id, true);

				for (int ix = -maxDistance; ix <= maxDistance; ix++) {
					for (int iy = -maxDistance; iy <= maxDistance; iy++) {
						for (int iz = -maxDistance; iz <= maxDistance; iz++) {

							int zero = 0;
							if (ix == 0)
								zero++;
							if (iy == 0)
								zero++;
							if (iz == 0)
								zero++;

							if (zero < 2)
								continue;

							int dx = Math.abs(originX - (x + ix));
							int dy = Math.abs(originY - (y + iy));
							int dz = Math.abs(originZ - (z + iz));

							int distance = Math.max(Math.max(dx, dy), dz);

							String dId = (x + ix) + " " + (y + iy) + " " + (z + iz);

							if (distance < 64 && !closedMap.containsKey(dId) && !openMap.contains(dId))
								openMap.add(dId);
						}
					}
				}
			} else
				closedMap.put(id, false);

			tick++;
			if (tick > blocksPerTick)
				return false;
		}

		return true;
	}

	@Override
	public void halt() {
	}

	@Override
	public void init() {
		closedMap = new HashMap<>();
		openMap = new LinkedList<>();

		openMap.add(originX + " " + originY + " " + originZ);
	}

	@Override
	public void stop(Event event) {
		if (openMap.isEmpty()) {

			List<int[]> foundBlocks = new ArrayList<>();
			for (Entry<String, Boolean> entry : closedMap.entrySet()) {
				if (entry.getValue()) {

					String[] split = entry.getKey().split(" ");

					foundBlocks.add(new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]) });
				}
			}

			int[] coords = new int[foundBlocks.size() * 3];

			for (int i = 0; i < foundBlocks.size(); i++) {
				coords[i * 3 + 0] = foundBlocks.get(i)[0];
				coords[i * 3 + 1] = foundBlocks.get(i)[1];
				coords[i * 3 + 2] = foundBlocks.get(i)[2];
			}

			ItemStack stack = new ItemStack(originBlock, 1, originMetadata);

			try {
				// This should work for every block that has an ItemBlock
				((TickEvent.PlayerTickEvent) event).player.addChatMessage(new ChatComponentText("Found " + foundBlocks.size() + " " + stack.getDisplayName() + " near it."));
			} catch (Exception e) {
				// Some things doesn't, like Redstone
				try {
					stack = originBlock.getPickBlock(null, ((TickEvent.PlayerTickEvent) event).player.worldObj, originX, originY, originZ, null);
					((TickEvent.PlayerTickEvent) event).player.addChatMessage(new ChatComponentText("Found " + foundBlocks.size() + " " + stack.getDisplayName() + " near it."));
				} catch (Exception ex) {
					// And things may go bad going that route since we are just
					// nulling arguments
					((TickEvent.PlayerTickEvent) event).player.addChatMessage(new ChatComponentText("Found " + foundBlocks.size() + " blocks near it."));
				}
			}

			if (foundBlocks.size() < 1000) {
				MinecraftForge.EVENT_BUS.register(SpecialHighlightRenderer.getInstance().setRenderer(coords, 5000));
			}
		}
	}
}