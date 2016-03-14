package com.shirkit.countcraft.api.count;

import com.shirkit.countcraft.api.IStack;

/**
 * Handles energy
 *
 * @author Shirkit
 *
 */
public class EnergyHandler implements IStack {

	public static enum Kind {
		REDSTONE_FLUX("Redstone Flux");

		private final String name;

		private Kind(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private long energy;

	private Kind type;

	public EnergyHandler(Kind type, long amount) {
		this.type = type;
		this.energy = amount;
	}

	@Override
	public Long getAmount() {
		return energy;
	}

	@Override
	public String getId() {
		return type.name();
	}

	@Override
	public String getIdentifier() {
		return energyID;
	}

	@Override
	public String getName() {
		return type.getName();
	}

}