package xyz.kohara.scarletlib.impl.client;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class EntityRenderCheck {

	private static final Set<Integer> HIDDEN_ENTITIES = new HashSet<>();

	public static void hideEntity(int id) {
		HIDDEN_ENTITIES.add(id);
	}

	public static void showEntity(int id) {
		HIDDEN_ENTITIES.remove(id);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityRender(RenderLivingEvent.Pre<?, ?> event) {
		var entity = event.getEntity();
		if (!HIDDEN_ENTITIES.isEmpty()) System.out.println(HIDDEN_ENTITIES);
		if (HIDDEN_ENTITIES.contains(entity.getId())) {
			event.cancel();
		}
	}
}
