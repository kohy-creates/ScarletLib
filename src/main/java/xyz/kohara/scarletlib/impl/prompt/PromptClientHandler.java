package xyz.kohara.scarletlib.impl.prompt;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.scarletlib.ScarletLibClient;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;
import xyz.kohara.scarletlib.impl.network.ScarletLibPackets;
import xyz.kohara.scarletlib.impl.network.packet.prompt.InteractWithProximityPromptC2SPacket;
import xyz.kohara.scarletlib.impl.prompt.registry.ClientProximityPromptRegistry;

public class PromptClientHandler {

	public static ProximityPromptClientData ACTIVE_PROMPT = null;
	public static int HAND_SWAP_COOLDOWN = 0;

	public static void clientPromptHandler() {
		ClientProximityPromptRegistry.updateEntityPromptLocations();
		ACTIVE_PROMPT = ClientProximityPromptRegistry.getNearestPrompt();

		if (HAND_SWAP_COOLDOWN > 0) HAND_SWAP_COOLDOWN--;

		if (ACTIVE_PROMPT != null) {
			var key = ScarletLibClient.keybinds().INTERACT_WITH_PROMPT;
			if (ACTIVE_PROMPT.isInstantInteract() && key.consumeClick()) {
				handlePromptInteraction(ACTIVE_PROMPT);
			} else if (!ACTIVE_PROMPT.isInstantInteract()) {
				key.consumeClick();
				if (key.isDown()) {
					ACTIVE_PROMPT.holdingTicks++;
					if (ACTIVE_PROMPT.holdingTicks >= ACTIVE_PROMPT.holdTimeTicks()) {
						handlePromptInteraction(ACTIVE_PROMPT);
					}
				} else {
					ACTIVE_PROMPT.holdingTicks = 0;
				}
			}
		}
	}

	private static void handlePromptInteraction(ProximityPromptClientData promptClientData) {
		ScarletLibPackets.INSTANCE.sendToServer(
				new InteractWithProximityPromptC2SPacket(promptClientData)
		);
		HAND_SWAP_COOLDOWN = 10;
	}

	@SubscribeEvent // Forge bus
	public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
		if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;
		ProximityPromptRenderer.render(event.getGuiGraphics(), event.getPartialTick());
	}
}
