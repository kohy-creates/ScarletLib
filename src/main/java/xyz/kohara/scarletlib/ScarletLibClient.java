package xyz.kohara.scarletlib;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;
import xyz.kohara.scarletlib.impl.prompt.ProximityPromptRenderer;
import xyz.kohara.scarletlib.impl.prompt.registry.ClientProximityPromptRegistry;

@OnlyIn(Dist.CLIENT)
public class ScarletLibClient {

	public static ProximityPromptClientData ACTIVE_PROMPT = null;

	@SubscribeEvent // Forge bus
	public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
		if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;
		if (ACTIVE_PROMPT != null) ProximityPromptRenderer.render(event.getGuiGraphics(), event.getPartialTick());
	}

	@SubscribeEvent // Forge bus
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;

		clientPromptHandler();
	}

	private static void clientPromptHandler() {
		ClientProximityPromptRegistry.updateEntityPromptLocations();
		ACTIVE_PROMPT = ClientProximityPromptRegistry.getNearestPrompt();

		if (ACTIVE_PROMPT != null) {

		}
	}

	@SubscribeEvent
	public static void onPlayerHandItemSwitch(LivingSwapItemsEvent event) {
		if (event.getEntity() instanceof LocalPlayer && ACTIVE_PROMPT != null) event.cancel();
	}

	public static Keybinds keybinds() {
		return Keybinds.INSTANCE;
	}

	public static class Keybinds {

		public static final Keybinds INSTANCE = new Keybinds();

		private Keybinds() {
		}

		private static final String CATEGORY = "key.categories." + ScarletLib.MOD_ID;

		public final KeyMapping INTERACT_WITH_PROMPT = new KeyMapping(
				"key." + ScarletLib.MOD_ID + ".interact_with_prompt",
				KeyConflictContext.IN_GAME,
				InputConstants.getKey(InputConstants.KEY_F, -1),
				CATEGORY
		);

		@SubscribeEvent // Mod bus
		public static void register(RegisterKeyMappingsEvent event) {
			event.register(INSTANCE.INTERACT_WITH_PROMPT);
		}
	}
}
