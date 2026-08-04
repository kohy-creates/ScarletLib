package xyz.kohara.scarletlib.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import xyz.kohara.scarletlib.ScarletLibRegistry;

public class ScarletLibAttributes {

	private static final DeferredRegister<Attribute> ATTRIBUTES = ScarletLibRegistry.create(Registries.ATTRIBUTE);

	public static void init(IEventBus bus) {
		ATTRIBUTES.register(bus);
	}
}
