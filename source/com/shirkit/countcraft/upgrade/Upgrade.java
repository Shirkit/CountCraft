package com.shirkit.countcraft.upgrade;

import java.io.Serializable;

public class Upgrade implements Serializable {

	public int id;
	public int damage;

	public Upgrade() {
	}

	public Upgrade(int id, int damage) {
		this.id = id;
		this.damage = damage;
	}

	@Override
	public String toString() {
		return id + ":" + damage;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Upgrade) {
			Upgrade upgrade = (Upgrade) obj;
			return this.hashCode() == upgrade.hashCode();
		}
		return false;
	}
}
