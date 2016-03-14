package com.shirkit.countcraft.tile;

import com.shirkit.countcraft.ModInfos;
import com.shirkit.countcraft.api.ICounterContainer;
import com.shirkit.countcraft.api.ISideAware;
import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.side.SideController;
import com.shirkit.countcraft.network.ISyncCapable;

import cpw.mods.fml.common.Optional;
import net.minecraft.tileentity.TileEntity;

@Optional.InterfaceList({ @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = ModInfos.OPENCOMPUTERS_ID),
		@Optional.Interface(iface = "li.cil.oc.api.network.ManagedPeripheral", modid = ModInfos.OPENCOMPUTERS_ID) })
public abstract class AbstractTileEntityCounter extends TileEntity implements ICounterContainer, ISyncCapable, ISideAware {

	// Persistent
	protected ICounter counter;

	private String[] methods = new String[] { "getTotalCounted", "getTicksRun", "entrySet" };

	protected boolean needUpdate = false;

	protected SideController sides = new SideController();

	// Transient
	protected int ticksRun;

	// -------------- ICounterContainer

	@Override
	public ICounter getCounter() {
		return counter;
	}

	@Override
	public SideController getSideController() {
		return sides;
	}

	// -------------- ISyncCapable

	@Override
	public long getTicksRun() {
		return ticksRun;
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
	}

	@Override
	public boolean isDirty() {
		return needUpdate;
	}

	// -------------- ISideAware

	@Override
	public void setDirty(boolean dirty) {
		if (!needUpdate && dirty)
			markDirty();
		needUpdate = dirty;
	}

}
