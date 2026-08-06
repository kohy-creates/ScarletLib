package xyz.kohara.scarletlib.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ScarletLibBasePacket {

	protected abstract void toBytes(FriendlyByteBuf buf);

	/**
	 * Logic executed for S2C packets when received on the client.
	 */
	protected void handleOnClient() {
	}

	/**
	 * Logic executed for C2S packets when received on the server,
	 * @param player Player (ServerPlayer) that sent the packet.
	 */
	protected void handleOnServer(ServerPlayer player) {
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			if (context.getDirection().getReceptionSide().isServer()) {
				ServerPlayer player = context.getSender();
				if (player != null) {
					handleOnServer(player);
				}
			} else {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleOnClient);
			}
		});
		return true;
	}

}
