package xyz.kohara.scarletlib;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.scarletlib.api.event.client.ProximityPromptVisibilityCheckEvent;
import xyz.kohara.scarletlib.impl.prompt.PromptClientHandler;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ScarletLibClient {

	@SubscribeEvent // Forge bus
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;

		PromptClientHandler.clientPromptHandler();
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

	@SubscribeEvent
	public static void promptVisibilityTest(ProximityPromptVisibilityCheckEvent event) {
		var player = event.getPlayer();
		var prompt = event.getPromptData();
		if (Objects.equals(prompt.getId(), "command_prompt")) {
			if (!player.isHolding(Items.NETHERITE_SWORD)) {
				event.hidePrompt();
			}
		}
	}
}
