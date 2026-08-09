package xyz.kohara.scarletlib.impl.registry.lootconditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.scarletlib.impl.registry.ScarletLibLootConditions;

public class IsHardcore implements LootItemCondition {

	private static final IsHardcore INSTANCE = new IsHardcore();

	@Override
	public @NotNull LootItemConditionType getType() {
		return ScarletLibLootConditions.IS_HARDCORE.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		return lootContext.getLevel().getLevelData().isHardcore();
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsHardcore> {
		public void serialize(@NotNull JsonObject jsonObject, @NotNull IsHardcore arg, @NotNull JsonSerializationContext jsonSerializationContext) {
		}

		public @NotNull IsHardcore deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
			return IsHardcore.INSTANCE;
		}
	}
}
