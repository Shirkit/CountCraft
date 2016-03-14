package com.shirkit.countcraft.api.integration;

import com.shirkit.countcraft.api.ICounterContainer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * This should be implemented be {@link IIntegrationHandler}s that have custom
 * {@link TileEntity}s implemented, that may contain the real interfaces that
 * are implemented by this mod.
 *
 * This can be called by anyone that wants to find the counter's inside a
 * TileEntity.
 *
 * @author Shirkit
 *
 */
public interface ICounterFinder {

	/**
	 * From a given {@link TileEntity}, the listener should try to get the
	 * {@link ISyncCapable} that is contained inside this TE. This is called
	 * when the client receives an update from the server.
	 *
	 * @param world
	 *            object that represents the {@link World} on the client side.
	 *
	 * @param tileentity
	 *            the container that holds the desired {@link ISyncCapable}.
	 *
	 * @return the instance of the {@link ISyncCapable} that was contained
	 *         inside the TE, or {@code null} if failed.
	 */
	// public ISyncCapable getSyncCapableFrom(TileEntity tileentity);

	/**
	 * From a given {@link TileEntity}, the listener should try to get the
	 * {@link ICounterContainer} that is contained inside this TE. This is
	 * called when the server receives an update from the client.
	 *
	 * @param world
	 *            object that represents the {@link World} on the server side.
	 *
	 * @param tileentity
	 *            the container that holds the desired {@link ICounterContainer}
	 *            .
	 *
	 * @return the instance of the {@link ICounterContainer} that was contained
	 *         inside the TE, or {@code null} if failed.
	 */
	public ICounterContainer getCounterContainerFrom(TileEntity tileentity);
}
