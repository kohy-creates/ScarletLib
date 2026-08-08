package xyz.kohara.scarletlib;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.kohara.scarletlib.api.ScarletLibScheduler;
import xyz.kohara.scarletlib.api.dash.Dash;
import xyz.kohara.scarletlib.api.util.EntityUtil;
import xyz.kohara.scarletlib.impl.ScarletLibRegistry;
import xyz.kohara.scarletlib.impl.network.ScarletLibPackets;
import xyz.kohara.scarletlib.impl.client.prompt.PromptClientHandler;
import xyz.kohara.scarletlib.impl.registry.ScarletLibAttributes;

@Mod(ScarletLib.MOD_ID)
public class ScarletLib {

	public static final String MOD_ID = "scarletlib";
	private static final Logger LOGGER = LogUtils.getLogger();

	public ScarletLib() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(ScarletLib.class);
		MinecraftForge.EVENT_BUS.register(ScarletLibAttributes.class);
		MinecraftForge.EVENT_BUS.register(ScarletLibScheduler.class);

//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		ScarletLibRegistry.init(modEventBus);
		ScarletLibPackets.INSTANCE.registerPackets();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ScarletLibClient.clientSetup(modEventBus));
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
	}

	@SubscribeEvent
	public static void dashTest(PlayerInteractEvent.RightClickItem event) {
		var item = event.getItemStack();
		var entity = event.getEntity();
		if (item.getItem() == Items.GLOW_INK_SAC) {
			System.out.println("glow ink sac");
			var dashBuilder = new Dash.Builder(6.0, 5, entity.getLookAngle())
					.disablesGravity(true)
					.onTick(ctx -> {
						if (ctx.entity() instanceof Player player) {
						}
					})
					.onStart(EntityUtil::makeInvisible)
					.onEnd(EntityUtil::makeVisible);
			EntityUtil.performDash(entity, dashBuilder.build());
		}
		if (item.getItem() == Items.NETHERITE_AXE) {
			System.out.println("netherite axe");
			var dashBuilder = new Dash.Builder(12.0, 20, entity.getLookAngle())
					.disablesGravity(true)
					.onTick(ctx -> {
						if (ctx.entity() instanceof Player player) {
						}
					})
					.onEntityHit(1.5d, (dasher, victim) -> {
						victim.hurt(new DamageSource(dasher.level().damageSources().onFire().typeHolder(), dasher), 5f);
					})
					.onWallHit(living -> {
						living.setGlowingTag(false);
					})
					.onStart(living -> living.setGlowingTag(true));
			EntityUtil.performDash(entity, dashBuilder.build());
		}
	}

	public static ResourceLocation of(String path) {
		return ResourceLocation.fromNamespaceAndPath(ScarletLib.MOD_ID, path);
	}
}
