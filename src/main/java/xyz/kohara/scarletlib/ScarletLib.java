package xyz.kohara.scarletlib;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.kohara.scarletlib.api.DelayedTaskScheduler;
import xyz.kohara.scarletlib.impl.ScarletLibRegistry;
import xyz.kohara.scarletlib.impl.network.ScarletLibPackets;
import xyz.kohara.scarletlib.impl.prompt.PromptClientHandler;
import xyz.kohara.scarletlib.impl.registry.ScarletLibAttributes;

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

//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		ScarletLibRegistry.init(modEventBus);
		ScarletLibPackets.INSTANCE.registerPackets();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientSetup(modEventBus));
	}

	private void clientSetup(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(ScarletLibClient.class);
		MinecraftForge.EVENT_BUS.register(PromptClientHandler.class);

		modBus.register(ScarletLibClient.Keybinds.class);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
	}

	public static ResourceLocation of(String path) {
		return ResourceLocation.fromNamespaceAndPath(ScarletLib.MOD_ID, path);
	}
}
