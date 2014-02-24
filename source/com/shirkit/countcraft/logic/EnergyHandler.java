package com.shirkit.countcraft.logic;

/**
 * Handles energy
 * 
 * @author Shirkit
 * 
 */
public class EnergyHandler implements Stack {

	private int energy;
	private String type, direction, side;

	public EnergyHandler(String type, String direction, String side, int amount) {
		this.type = type;
		this.direction = direction;
		this.side = side;
		this.energy = amount;
	}

	public EnergyHandler(String type, String direction, int amount) {
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
		return type + "-" + direction + (side != null ? "-" + side : "");
	}

	@Override
	public String getName() {
		return type;
	}

	@Override
	public Object getStack() {
		return null;
	}
}