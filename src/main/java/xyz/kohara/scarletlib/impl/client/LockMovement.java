package xyz.kohara.scarletlib.impl.client;

import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LockMovement {

	public static boolean isMovementLocked = false;

	@SubscribeEvent
	public static void onInputUpdate(MovementInputUpdateEvent event) {
		if (isMovementLocked) {
			event.getInput().leftImpulse = 0.0F;
			event.getInput().forwardImpulse = 0.0F;
		}
	}
}
