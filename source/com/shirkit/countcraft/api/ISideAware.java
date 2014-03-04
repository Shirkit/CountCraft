package com.shirkit.countcraft.api;

import com.shirkit.countcraft.api.count.Counter;
import com.shirkit.countcraft.api.side.SideController;

import net.minecraft.tileentity.TileEntity;

/**
 * Normally implemented by {@link TileEntity} that also implements a
 * {@link Counter}. This is to indicate that this TE has control over it sides,
 * enabling/disabling certain functions on certain sides.
 * 
 * @author Shirkit
 * 
 */
public interface ISideAware {

	public SideController getSideController();

}
