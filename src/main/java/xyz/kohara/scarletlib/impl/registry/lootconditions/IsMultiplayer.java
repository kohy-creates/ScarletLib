package xyz.kohara.scarletlib.impl.registry.lootconditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.scarletlib.impl.registry.ScarletLibLootConditions;

public class IsMultiplayer implements LootItemCondition {

	private static final IsMultiplayer INSTANCE = new IsMultiplayer();

	@Override
	public @NotNull LootItemConditionType getType() {
		return ScarletLibLootConditions.IS_MULTIPLAYER.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		var server = lootContext.getLevel().getServer();
		return server.isDedicatedServer() || server.isPublished();
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsMultiplayer> {
		public void serialize(@NotNull JsonObject jsonObject, @NotNull IsMultiplayer arg, @NotNull JsonSerializationContext jsonSerializationContext) {
		}

		public @NotNull IsMultiplayer deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
			return IsMultiplayer.INSTANCE;
		}
	}
}
