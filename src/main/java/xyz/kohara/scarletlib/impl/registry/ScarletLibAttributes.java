package xyz.kohara.scarletlib.impl.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.scarletlib.impl.ScarletLibRegistry;

public class ScarletLibAttributes {

	public static final String DOUBLE_JUMP_TAG = "scarletlib.double_jumps";
	private static final DeferredRegister<Attribute> ATTRIBUTES = ScarletLibRegistry.create(Registries.ATTRIBUTE);

	public static final RegistryObject<Attribute> DAMAGE_REDUCTION = ATTRIBUTES.register(
			"damage_reduction",
			() -> new RangedAttribute("generic.damage_reduction", 1d, -2048d, 2048d).setSyncable(true)
	);

//	public static final RegistryObject<Attribute> EXTRA_JUMPS = ATTRIBUTES.init(
//			"extra_jumps",
//			() -> new RangedAttribute("player.extra_jumps", 0d, 0, 2048d).setSyncable(true)
//	);
//
//	public static final RegistryObject<Attribute> EXTRA_JUMPS_STRENGTH = ATTRIBUTES.init(
//			"extra_jumps_strength",
//			() -> new RangedAttribute("player.extra_jumps_strength", 1d, -2048, 2048d).setSyncable(true)
//	);

	@SubscribeEvent
	public static void addEntityAttributes(EntityAttributeModificationEvent event) {
//		// Player-specific
//		event.add(EntityType.PLAYER, EXTRA_JUMPS.get());
//		event.add(EntityType.PLAYER, EXTRA_JUMPS_STRENGTH.get());

		// Global
		for (var type : event.getTypes()) {
			event.add(type, DAMAGE_REDUCTION.get());
		}
	}

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

//	@SubscribeEvent
//	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//		var player = event.player;
//		if (player instanceof ServerPlayer) {
//			var extraJumps = player.getAttribute(EXTRA_JUMPS.get()).getValue();
//
//			var data = player.getPersistentData();
//			if (!data.getAllKeys().contains(DOUBLE_JUMP_TAG)) {
//				var tag = new CompoundTag();
//				tag.putInt("amount", (int) extraJumps);
//				tag.putInt("used", 0);
//				data.put(DOUBLE_JUMP_TAG, tag);
//			}
//		}
//	}

	public static void init(IEventBus bus) {
		ATTRIBUTES.register(bus);
	}
}
