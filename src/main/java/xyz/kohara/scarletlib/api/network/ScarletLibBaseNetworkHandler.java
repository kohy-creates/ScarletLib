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
	private int packetId = 0;

	private int nextId() {
		return packetId++;
	}

	public ScarletLibBaseNetworkHandler(String modId) {
		this(modId, "messages", "1.0");
	}

	public ScarletLibBaseNetworkHandler(String modId, String channelName) {
		this(modId, channelName, "1.0");
	}

	/**
	 * Creates a simple channel you can reference through a static instance field.
	 * Simplifies registration process of packets and comes with functions
	 * to send packets from and to the server.
	 *
	 * @param modId       ID of your mod.
	 * @param channelName Optional. Defaults to "messages"
	 * @param version     Optional. Defaults to "1.0"
	 */
	public ScarletLibBaseNetworkHandler(String modId, String channelName, String version) {
		this.channel = NetworkRegistry.ChannelBuilder
				.named(ResourceLocation.fromNamespaceAndPath(modId, channelName))
				.networkProtocolVersion(() -> version)
				.clientAcceptedVersions(version::equals)
				.serverAcceptedVersions(version::equals)
				.simpleChannel();
	}

	/**
	 * Registers a packet to your channel.
	 *
	 * @param clazz     Reference to the packet class.
	 * @param decoder   Lambda reference to your packet's 'new' method
	 *                  (the one that uses FriendlyByteBuf argument).
	 * @param direction The direction of the packet (S2C/C2S)
	 * @param <MSG>     Packet that extends the ScarletLibBasePacket class.
	 */
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

	/**
	 * Sends a packet from client to server.
	 *
	 * @param <MSG> Packet that extends the ScarletLibBasePacket class.
	 */
	public <MSG extends ScarletLibBasePacket> void sendToServer(MSG message) {
		channel.sendToServer(message);
	}

	/**
	 * Sends a packet from client to server.
	 *
	 * @param <MSG> Packet that extends the ScarletLibBasePacket class.
	 */
	public <MSG extends ScarletLibBasePacket> void sendToPlayer(MSG message, ServerPlayer player) {
		channel.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	/**
	 * Sends a packet from client to server.
	 *
	 * @param <MSG> Packet that extends the ScarletLibBasePacket class.
	 */
	public <MSG extends ScarletLibBasePacket> void sendToAllPlayers(MSG message) {
		channel.send(PacketDistributor.ALL.noArg(), message);
	}

	/**
	 * Sends a packet from client to server.
	 *
	 * @param <MSG> Packet that extends the ScarletLibBasePacket class.
	 */
	public <MSG> void sendToTracking(MSG message, Entity entity) {
		this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
	}

	/**
	 * Register packets through 'registerPacket' method in this void.
	 */
	protected abstract void registerPackets();
}
