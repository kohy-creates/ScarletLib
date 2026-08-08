package xyz.kohara.scarletlib.impl.client.prompt;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.scarletlib.api.event.client.ProximityPromptVisibilityCheckEvent;
import xyz.kohara.scarletlib.api.util.PlayerUtil;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientProximityPromptRegistry {
	private static final Map<UUID, ProximityPromptClientData> CLIENT_PROMPTS = new ConcurrentHashMap<>();
	private static final Minecraft MC = Minecraft.getInstance();

	public static void updatePrompts(Collection<ProximityPromptClientData> prompts) {
		CLIENT_PROMPTS.clear();
		for (ProximityPromptClientData prompt : prompts) {
			CLIENT_PROMPTS.put(prompt.getUuid(), prompt);
		}
	}

	public static void removePrompt(UUID uuid) {
		CLIENT_PROMPTS.remove(uuid);
	}

	public static void clear() {
		CLIENT_PROMPTS.clear();
	}

	public static void updateEntityPromptLocations() {
		var player = MC.player;
		if (player == null) return;
		var level = MC.player.level();
		for (var promptData : CLIENT_PROMPTS.values()
				.stream()
				.filter(data -> data.getDimension() == level.dimension())
				.toList()
		) {
			if (promptData.hasDynamicLocation()) {
				var entity = promptData.getEntity();
				if (entity != null) {
					var pos = entity.position();
					promptData.updateLocation(new Vec3(pos.x, pos.y + entity.getBbHeight() / 2f, pos.z));
				}
			}
		}
	}

	/**
	 * Returns prompts that match the player's current getDimension.
	 */
	public static List<ProximityPromptClientData> getPromptsForCurrentDimension() {
		if (MC.level == null) return List.of();
		ResourceKey<Level> currentDimension = Minecraft.getInstance().level.dimension();
		return CLIENT_PROMPTS.values().stream()
				.filter(prompt -> prompt.getDimension().equals(currentDimension))
				.toList();
	}

	/**
	 * Returns prompts that match the player's getDimension and can be seen
	 * (distance to them is smaller than max interaction range + passes the visibility event)
	 */
	public static List<ProximityPromptClientData> getNearbyVisiblePrompts() {
		var player = MC.player;
		final List<ProximityPromptClientData> prompts = new ArrayList<>();
		for (var promptData : getPromptsForCurrentDimension()) {
			if (promptData.getLocation().closerThan(player.position(), promptData.getMaxDistance())) {
				var eventHandler = new ProximityPromptVisibilityCheckEvent(promptData);
				MinecraftForge.EVENT_BUS.post(eventHandler);
				if (eventHandler.canBeSeen()) prompts.add(promptData);
			}
		}
		return prompts;
	}

	public static @Nullable ProximityPromptClientData getNearestPrompt() {
		var player = MC.player;
		ProximityPromptClientData chosen = null;
		for (var promptData : getNearbyVisiblePrompts()) {
			if (!PlayerUtil.isFacingLocation(player, promptData.getLocation(), 0.66)) continue;
			if (chosen == null) {
				chosen = promptData;
				continue;
			}
			var distance1 = player.distanceToSqr(chosen.getLocation());
			var distance2 = player.distanceToSqr(promptData.getLocation());
			if (distance2 < distance1) {
				chosen = promptData;
			}
		}
		return chosen;
	}
}