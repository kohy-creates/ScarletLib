package xyz.kohara.scarletlib.network.packet.prompt;

import net.minecraft.network.FriendlyByteBuf;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.prompt.registry.ClientProximityPromptRegistry;

import java.util.UUID;

public class RemoveProximityPromptS2CPacket extends ScarletLibBasePacket {
	private final UUID promptUuid;

	public RemoveProximityPromptS2CPacket(UUID promptUuid) {
		this.promptUuid = promptUuid;
	}

	public RemoveProximityPromptS2CPacket(FriendlyByteBuf buf) {
		this.promptUuid = buf.readUUID();
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeUUID(promptUuid);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		ClientProximityPromptRegistry.removePrompt(this.promptUuid);
	}
}