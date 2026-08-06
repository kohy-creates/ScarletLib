package xyz.kohara.scarletlib.impl.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;

public class ShowRainbowActionBarMessageS2CPacket extends ScarletLibBasePacket {

	private final Component message;

	public ShowRainbowActionBarMessageS2CPacket(Component message) {
		this.message = message;
	}

	public ShowRainbowActionBarMessageS2CPacket(FriendlyByteBuf buf) {
		this.message = buf.readComponent();
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeComponent(this.message);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		var mc = Minecraft.getInstance();
		mc.gui.setOverlayMessage(this.message, true);
		mc.getNarrator().sayNow(this.message);
	}
}
