package com.shirkit.countcraft.api;

import net.minecraft.tileentity.TileEntity;

import com.shirkit.countcraft.api.count.ICounter;
import com.shirkit.countcraft.api.side.SideController;

/**
 * Normally implemented by {@link TileEntity} that also implements a
 * {@link ICounter}. This is to indicate that this TE has control over it sides,
 * enabling/disabling certain functions on certain sides.
 * 
 * @author Shirkit
 * 
 */
public interface ISideAware {

	public SideController getSideController();

}
