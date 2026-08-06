package xyz.kohara.scarletlib;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.kohara.scarletlib.api.DelayedTaskScheduler;
import xyz.kohara.scarletlib.api.prompt.ProximityPrompt;
import xyz.kohara.scarletlib.network.ScarletLibPackets;
import xyz.kohara.scarletlib.registry.ScarletLibAttributes;

@Mod(ScarletLib.MOD_ID)
public class ScarletLib {

	public static final String MOD_ID = "scarletlib";
	private static final Logger LOGGER = LogUtils.getLogger();

	public ScarletLib() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(ScarletLibAttributes.class);
		MinecraftForge.EVENT_BUS.register(DelayedTaskScheduler.class);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		ScarletLibRegistry.init(modEventBus);
		ScarletLibPackets.INSTANCE.registerPackets();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientSetup(modEventBus));
	}

	private void clientSetup(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(ScarletLibClient.class);

		modBus.register(ScarletLibClient.Keybinds.class);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
	}

	public static ResourceLocation of(String path) {
		return ResourceLocation.fromNamespaceAndPath(ScarletLib.MOD_ID, path);
	}

//	@SubscribeEvent
//	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//		if (!(event.getEntity() instanceof ServerPlayer player)) return;
//
//		var level = player.serverLevel();
//		var pos = player.blockPosition();
//
//		// 1. Create a Block Prompt at the player's feet getLocation (Instant press)
//		new ProximityPrompt.Builder("debug_block_prompt")
//				.setActionText(Component.literal("Inspect Block"))
//				.setObjectText(Component.literal(level.getBlockState(pos.below()).getBlock().getName().getString()))
//				.forBlock(level.getBlockState(pos.below()).getBlock(), pos.below(), level)
//				.interactionRange(5.0D)
//				.holdTickToProcess(0) // Instant
//				.build();
//
//		// 2. Create an Entity Prompt on the nearest entity within 10 blocks (Hold 20 ticks / 1 second)
//		Entity nearestEntity = level.getEntities(player, new AABB(pos).inflate(10.0D))
//				.stream()
//				.min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
//				.orElse(null);
//
//		if (nearestEntity != null) {
//			new ProximityPrompt.Builder("debug_entity_prompt")
//					.setActionText(Component.literal("Interact With"))
//					.setObjectText(nearestEntity.getDisplayName())
//					.forEntity(nearestEntity)
//					.interactionRange(4.0D)
//					.holdTickToProcess(20) // 1 second hold
//					.build();
//
//			player.sendSystemMessage(Component.literal("§a[ScarletLib Debug] §fCreated Block Prompt & Entity Prompt on §e" + nearestEntity.getName().getString()));
//		} else {
//			player.sendSystemMessage(Component.literal("§a[ScarletLib Debug] §fCreated Block Prompt at feet. (No entity nearby for Entity Prompt)"));
//		}
//	}

	// Register a /testprompt command to spawn prompts wherever you stand on demand
	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(
				Commands.literal("testprompt")
						.requires(source -> source.hasPermission(2))
						.executes(context -> {
							ServerPlayer player = context.getSource().getPlayerOrException();
							Vec3 pos = player.position();

							new ProximityPrompt.Builder("command_prompt_" + System.currentTimeMillis())
									.setActionText(Component.literal("Activate"))
									.setObjectText(Component.literal("Test Marker"))
									.forLocation(pos, player.serverLevel())
									.interactionRange(16.0D)
									.holdTickToProcess(30) // 1.5 second hold
									.build();

							context.getSource().sendSuccess(() -> Component.literal("§aCreated test prompt at " + pos), false);
							return 1;
						})
		);
	}
}
