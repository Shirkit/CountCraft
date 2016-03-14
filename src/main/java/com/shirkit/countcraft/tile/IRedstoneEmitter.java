package com.shirkit.countcraft.tile;

/**
 * Implemented on tile entities.
 *
 * @author Shirkit
 *
 */
public interface IRedstoneEmitter {

	public boolean getSignal();

	public void setSignal(boolean signal);

}

/*
 * Block implementation
 *
 * public boolean canProvidePower();
 *
 * public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int
 * par3, int par4, int par5);
 *
 * public int isProvidingStrongPower(IBlockAccess par1iBlockAccess, int par2,
 * int par3, int par4, int par5);
 *
 * public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z,
 * int side);
 */