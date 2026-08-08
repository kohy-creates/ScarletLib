package xyz.kohara.scarletlib.api;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared delayed task scheduler that executes a task on the server after a specified amount of ticks.
 * Tasks are executed in the END phase of the tick.
 */
public class ScarletLibScheduler {
	private static final Map<Integer, List<Runnable>> TASKS = new HashMap<>();
	private static int tickCount = 0;

	private static void tick() {
		tickCount++;
		List<Runnable> runnables = TASKS.remove(tickCount);
		if (runnables != null) {
			runnables.forEach(Runnable::run);
		}
	}

	/**
	 * Schedules a runnable (task) on the server to execute after a set delay in ticks
	 */
	public static void schedule(Runnable task, int delay) {
		int executeTick = tickCount + delay;
		TASKS.computeIfAbsent(executeTick, k -> new ArrayList<>()).add(task);
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) tick();
	}
}
