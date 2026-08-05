package xyz.kohara.scarletlib.prompt.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.scarletlib.api.util.PlayerUtil;
import xyz.kohara.scarletlib.prompt.ProximityPromptData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientProximityPromptRegistry {
	private static final Map<UUID, ProximityPromptData> CLIENT_PROMPTS = new ConcurrentHashMap<>();
	private static final Minecraft MC = Minecraft.getInstance();

	public static void updatePrompts(Collection<ProximityPromptData> prompts) {
		CLIENT_PROMPTS.clear();
		for (ProximityPromptData prompt : prompts) {
			CLIENT_PROMPTS.put(prompt.uuid(), prompt);
		}
	}

	public static void removePrompt(UUID uuid) {
		CLIENT_PROMPTS.remove(uuid);
	}

	public static void clear() {
		CLIENT_PROMPTS.clear();
	}

	/**
	 * Returns prompts that match the player's current dimension.
	 */
	public static List<ProximityPromptData> getPromptsForCurrentDimension() {
		if (MC.level == null) return List.of();
		ResourceKey<Level> currentDimension = Minecraft.getInstance().level.dimension();
		return CLIENT_PROMPTS.values().stream()
				.filter(prompt -> prompt.dimension().equals(currentDimension))
				.toList();
	}

	/**
	 * Returns prompts that match the player's dimension and can be seen
	 * (distance to them is smaller than max interaction range)
	 */
	public static List<ProximityPromptData> getNearbyVisiblePrompts() {
		var player = MC.player;
		final List<ProximityPromptData> prompts = new ArrayList<>();
		for (var promptData : getPromptsForCurrentDimension()) {
			if (promptData.location().closerThan(player.position(), promptData.maxDistance())) {
				prompts.add(promptData);
			}
		}
		return prompts;
	}

	public static @Nullable ProximityPromptData getNearestPrompt() {
		var player = MC.player;
		ProximityPromptData chosen = null;
		for (var promptData : getNearbyVisiblePrompts()) {
			if (!PlayerUtil.isFacingLocation(player, promptData.location(), 0.85)) continue;
			if (chosen == null) {
				chosen = promptData;
				continue;
			}
			var distance1 = player.distanceToSqr(chosen.location());
			var distance2 = player.distanceToSqr(promptData.location());
			if (distance2 < distance1) {
				chosen = promptData;
			}
		}
		return chosen;
	}
}