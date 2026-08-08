package xyz.kohara.scarletlib.impl.network.packet.prompt;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;
import xyz.kohara.scarletlib.impl.prompt.ServerProximityPromptRegistry;

import java.util.UUID;

public class InteractWithProximityPromptC2SPacket extends ScarletLibBasePacket {

	private final UUID uuid;

	public InteractWithProximityPromptC2SPacket(ProximityPromptClientData promptClientData) {
		this.uuid = promptClientData.getUuid();
	}

	public InteractWithProximityPromptC2SPacket(FriendlyByteBuf buf) {
		this.uuid = buf.readUUID();
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeUUID(this.uuid);
	}

	@Override
	public void handleOnServer(ServerPlayer player) {
		super.handleOnServer(player);
		ServerProximityPromptRegistry.handleInteraction(this.uuid, player);
	}
}
