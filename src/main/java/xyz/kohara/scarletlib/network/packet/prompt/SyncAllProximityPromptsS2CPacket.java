package xyz.kohara.scarletlib.network.packet.prompt;

import net.minecraft.network.FriendlyByteBuf;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.prompt.ProximityPromptData;
import xyz.kohara.scarletlib.prompt.registry.ClientProximityPromptRegistry;

import java.util.ArrayList;
import java.util.List;

public class SyncAllProximityPromptsS2CPacket extends ScarletLibBasePacket {
	private final List<ProximityPromptData> prompts;

	public SyncAllProximityPromptsS2CPacket(List<ProximityPromptData> prompts) {
		this.prompts = prompts;
	}

	public SyncAllProximityPromptsS2CPacket(FriendlyByteBuf buf) {
		int count = buf.readVarInt();
		this.prompts = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			this.prompts.add(ProximityPromptData.decode(buf));
		}
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeVarInt(prompts.size());
		for (ProximityPromptData prompt : prompts) {
			prompt.encode(buf);
		}
	}

	@Override
	protected void handleOnClient() {
		ClientProximityPromptRegistry.updatePrompts(this.prompts);
	}
}