package xyz.kohara.scarletlib.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.scarletlib.api.ScarletLibScheduler;
import xyz.kohara.scarletlib.api.prompt.ProximityPrompt;

/**
 * Fired whenever a player finishes interacting with a prompt (single click or held button for long enough).
 * Cancelable, has no result.
 * Canceling the event makes the prompt stay, but not do any effects.If you want to make the prompt stay
 * but for effects to execute, use `event.respawnEventPrompt()`.
 */
@Cancelable
public class ProximityPromptInteractEvent extends Event {

	private final ProximityPrompt prompt;
	private final ServerPlayer player;
	private final ServerLevel level;

	public ProximityPromptInteractEvent(ProximityPrompt prompt, ServerPlayer player, ServerLevel level) {
		this.prompt = prompt;
		this.player = player;
		this.level = level;
	}

	public ProximityPrompt getPrompt() {
		return this.prompt;
	}

	public ServerPlayer getPlayer() {
		return this.player;
	}

	public ServerLevel getLevel() {
		return this.level;
	}

	/**
	 * Respawns a copy of the prompt, making it not go away when interacted with.
	 */
	public void respawnEventPrompt() {
		var builder = this.prompt.recreate();
		ScarletLibScheduler.schedule(builder::build, 1);
	}
}

