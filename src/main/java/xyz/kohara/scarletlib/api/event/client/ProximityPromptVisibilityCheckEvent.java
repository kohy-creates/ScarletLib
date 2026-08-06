package xyz.kohara.scarletlib.api.event.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.scarletlib.prompt.ProximityPromptClientData;

public class ProximityPromptVisibilityCheckEvent extends Event {

	private final ProximityPromptClientData data;
	private boolean canBeSeen = true;

	public ProximityPromptVisibilityCheckEvent(ProximityPromptClientData data) {
		this.data = data;
	}

	public ProximityPromptClientData getPromptData() {
		return this.data;
	}

	/**
	 * Hides the event prompt from the client.
	 */
	public void hidePrompt() {
		this.canBeSeen = false;
	}

	/**
	 * Utility so that Minecraft.getInstance() can be skipped in event code.
	 */
	public Minecraft mc() {
		return Minecraft.getInstance();
	}

	/**
	 * Utility so that Minecraft.getInstance().player can be skipped in event code.
	 */
	public LocalPlayer getPlayer() {
		return mc().player;
	}

	/**
	 * Whether the given Proximity Prompt can be seen by the client.
	 * I genuinly don't know if you will ever need to actually check this,
	 * but if I learned anything from a year of modding it's that
	 * there will ALWAYS be a niche scenario where the value of a private field is needed.
	 * Defaults to true for every prompt.
	 */
	public boolean canBeSeen() {
		return this.canBeSeen;
	}
}
