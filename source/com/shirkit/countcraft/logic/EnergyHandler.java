package com.shirkit.countcraft.logic;

import net.minecraftforge.common.ForgeDirection;

/**
 * Handles energy
 * 
 * @author Shirkit
 * 
 */
public class EnergyHandler implements Stack {

	public static enum Kind {
		REDSTONE_FLUX("Redstone Flux");

		private final String name;

		private Kind(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static enum Direction {
		IN, OUT
	}

	private int energy;
	private Kind type;
	private Direction direction;
	private ForgeDirection side;

	public EnergyHandler(Kind type, Direction direction, ForgeDirection side, int amount) {
		this.type = type;
		this.direction = direction;
		this.side = side;
		this.energy = amount;
	}

	public EnergyHandler(Kind type, Direction direction, int amount) {
		this.type = type;
		this.direction = direction;
		this.energy = amount;
	}

	@Override
	public String getIdentifier() {
		return energyID;
	}

	@Override
	public Integer getAmount() {
		return energy;
	}

	@Override
	public String getId() {
		return type.toString() + "-" + direction.toString() + (side != null ? "-" + side.toString() : "");
	}

	@Override
	public String getName() {
		return type.toString() + " " + direction.toString() + (side != null ? " " + side.toString() : "");
	}

}