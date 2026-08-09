package xyz.kohara.scarletlib.impl.network;

import net.minecraftforge.network.NetworkDirection;
import xyz.kohara.scarletlib.ScarletLib;
import xyz.kohara.scarletlib.api.network.ScarletLibBaseNetworkHandler;
import xyz.kohara.scarletlib.impl.network.packet.*;
import xyz.kohara.scarletlib.impl.network.packet.prompt.*;

public class ScarletLibPackets extends ScarletLibBaseNetworkHandler {

	public static final ScarletLibPackets INSTANCE = new ScarletLibPackets();

	public ScarletLibPackets() {
		super(ScarletLib.MOD_ID);
	}

	@Override
	public void registerPackets() {
		registerPacket(AddEntityParticleEmitterS2CPacket.class, AddEntityParticleEmitterS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(SyncAllProximityPromptsS2CPacket.class, SyncAllProximityPromptsS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(InteractWithProximityPromptC2SPacket.class, InteractWithProximityPromptC2SPacket::new, NetworkDirection.PLAY_TO_SERVER);
		registerPacket(RemoveProximityPromptS2CPacket.class, RemoveProximityPromptS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(ShowRainbowActionBarMessageS2CPacket.class, ShowRainbowActionBarMessageS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(EntityRenderS2CPacket.class, EntityRenderS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(SmoothTeleportS2CPacket.class, SmoothTeleportS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
		registerPacket(LockMovementS2CPacket.class, LockMovementS2CPacket::new, NetworkDirection.PLAY_TO_CLIENT);
	}
}
