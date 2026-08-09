package xyz.kohara.scarletlib.impl;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import xyz.kohara.scarletlib.ScarletLib;
import xyz.kohara.scarletlib.impl.registry.ScarletLibAttributes;
import xyz.kohara.scarletlib.impl.registry.ScarletLibLootConditions;
import xyz.kohara.scarletlib.impl.registry.ScarletLibLootModifiers;

public class ScarletLibRegistry {

	public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey) {
		return DeferredRegister.create(registryKey, ScarletLib.MOD_ID);
	}

	public static void init(IEventBus bus) {
		ScarletLibLootModifiers.init(bus);
		ScarletLibAttributes.init(bus);
		ScarletLibLootConditions.init(bus);
	}
}
