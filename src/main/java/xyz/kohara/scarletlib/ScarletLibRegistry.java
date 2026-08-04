package xyz.kohara.scarletlib;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import xyz.kohara.scarletlib.registry.ScarletLibAttributes;
import xyz.kohara.scarletlib.registry.ScarletLibLootModifiers;

public class ScarletLibRegistry {

	public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey) {
		return DeferredRegister.create(registryKey, ScarletLib.MOD_ID);
	}

	public static void init(IEventBus bus) {
		ScarletLibLootModifiers.init(bus);
		ScarletLibAttributes.init(bus);
	}
}
