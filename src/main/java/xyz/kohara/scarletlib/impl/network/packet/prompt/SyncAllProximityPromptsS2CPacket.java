package xyz.kohara.scarletlib.impl.network.packet.prompt;

import net.minecraft.network.FriendlyByteBuf;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;
import xyz.kohara.scarletlib.impl.client.prompt.ClientProximityPromptRegistry;

import java.util.ArrayList;
import java.util.List;

public class SyncAllProximityPromptsS2CPacket extends ScarletLibBasePacket {
	private final List<ProximityPromptClientData> prompts;

	public SyncAllProximityPromptsS2CPacket(List<ProximityPromptClientData> prompts) {
		this.prompts = prompts;
	}

	public SyncAllProximityPromptsS2CPacket(FriendlyByteBuf buf) {
		int count = buf.readVarInt();
		this.prompts = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			this.prompts.add(ProximityPromptClientData.decode(buf));
		}
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeVarInt(prompts.size());
		for (ProximityPromptClientData prompt : prompts) {
			prompt.encode(buf);
		}
	}

	@Override
	protected void handleOnClient() {
		ClientProximityPromptRegistry.updatePrompts(this.prompts);
	}
}