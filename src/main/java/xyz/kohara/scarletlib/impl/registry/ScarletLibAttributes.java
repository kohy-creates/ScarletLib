package xyz.kohara.scarletlib.impl.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.scarletlib.impl.ScarletLibRegistry;

public class ScarletLibAttributes {

	private static final DeferredRegister<Attribute> ATTRIBUTES = ScarletLibRegistry.create(Registries.ATTRIBUTE);

	public static final RegistryObject<Attribute> DAMAGE_REDUCTION = ATTRIBUTES.register(
			"damage_reduction",
			() -> new RangedAttribute("generic.damage_reduction", 1d, -2048d, 2048d)
	);

	public static final RegistryObject<Attribute> EXTRA_JUMPS = ATTRIBUTES.register(
			"extra_jumps",
			() -> new RangedAttribute("player.extra_jumps", 0d, 0, 2048d)
	);

	public static final RegistryObject<Attribute> EXTRA_JUMPS_STRENGTH = ATTRIBUTES.register(
			"extra_jumps_strength",
			() -> new RangedAttribute("player.extra_jumps_strength", 1d, -2048, 2048d)
	);

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		var entity = event.getEntity();
		var amount = event.getAmount();

		var dr = entity.getAttribute(DAMAGE_REDUCTION.get());
		if (dr != null) {
			var value = dr.getValue() - 1d;
			event.setAmount((float) (amount - amount * value));
		}
	}

	public static void init(IEventBus bus) {
		ATTRIBUTES.register(bus);
	}
}
