package xyz.kohara.scarletlib.impl.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.scarletlib.impl.ScarletLibRegistry;
import xyz.kohara.scarletlib.impl.registry.lootconditions.IsHardcore;
import xyz.kohara.scarletlib.impl.registry.lootconditions.IsMultiplayer;

public class ScarletLibLootConditions {
	public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = ScarletLibRegistry.create(Registries.LOOT_CONDITION_TYPE);

	public static final RegistryObject<LootItemConditionType> IS_HARDCORE =
			LOOT_CONDITIONS.register("is_hardcore", () -> new LootItemConditionType(new IsHardcore.Serializer()));

	public static final RegistryObject<LootItemConditionType> IS_MULTIPLAYER =
			LOOT_CONDITIONS.register("is_multiplayer", () -> new LootItemConditionType(new IsMultiplayer.Serializer()));

	public static void init(IEventBus eventBus) {
		LOOT_CONDITIONS.register(eventBus);
	}
}
