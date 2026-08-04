package xyz.kohara.scarletlib.registry;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.scarletlib.ScarletLibRegistry;
import xyz.kohara.scarletlib.registry.lootmodifier.AddItemLootModifier;

public class ScarletLibLootModifiers {

	private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = ScarletLibRegistry.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

	public static final RegistryObject<Codec<AddItemLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", () -> AddItemLootModifier.CODEC);

	public static void init(IEventBus bus) {
		LOOT_MODIFIERS.register(bus);
	}
}
