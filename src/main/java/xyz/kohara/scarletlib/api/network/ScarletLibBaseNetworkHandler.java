package xyz.kohara.scarletlib.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public abstract class ScarletLibBaseNetworkHandler {

	private final SimpleChannel channel;

	private int packedId = 0;

	private int nextId() {
		return packedId++;
	}

	public ScarletLibBaseNetworkHandler(String modId) {
		this(modId, "messages", "1.0");
	}

	public ScarletLibBaseNetworkHandler(String modId, String channelName) {
		this(modId, channelName, "1.0");
	}

	public ScarletLibBaseNetworkHandler(String modId, String channelName, String version) {
		this.channel = NetworkRegistry.ChannelBuilder
				.named(ResourceLocation.fromNamespaceAndPath(modId, channelName))
				.networkProtocolVersion(() -> version)
				.clientAcceptedVersions(version::equals)
				.serverAcceptedVersions(version::equals)
				.simpleChannel();
	}

	protected <MSG extends ScarletLibBasePacket> void registerPacket(
			Class<MSG> clazz,
			Function<FriendlyByteBuf, MSG> decoder,
			NetworkDirection direction
	) {
		this.channel.messageBuilder(clazz, nextId(), direction)
				.decoder(decoder)
				.encoder(ScarletLibBasePacket::toBytes)
				.consumerMainThread(ScarletLibBasePacket::handle)
				.add();
	}

	public <MSG extends ScarletLibBasePacket> void sendToServer(MSG message) {
		channel.sendToServer(message);
	}

	public <MSG extends ScarletLibBasePacket> void sendToPlayer(MSG message, ServerPlayer player) {
		channel.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public <MSG extends ScarletLibBasePacket> void sendToAllPlayers(MSG message) {
		channel.send(PacketDistributor.ALL.noArg(), message);
	}

	public <MSG> void sendToTracking(MSG message, Entity entity) {
		this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
	}
}
