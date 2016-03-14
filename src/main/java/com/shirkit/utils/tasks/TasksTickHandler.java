package com.shirkit.utils.tasks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TasksTickHandler {

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		TaskScheduler.getInstance().runTasks(event);
	}
}
