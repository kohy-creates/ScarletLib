package xyz.kohara.scarletlib.impl.network.packet.prompt;

import net.minecraft.network.FriendlyByteBuf;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.impl.client.LockMovement;

public class LockMovementS2CPacket extends ScarletLibBasePacket {

	private final boolean shouldLockMovement;

	public LockMovementS2CPacket(boolean shouldLockMovement) {
		this.shouldLockMovement = shouldLockMovement;
	}

	public  LockMovementS2CPacket(FriendlyByteBuf buf) {
		this.shouldLockMovement = buf.readBoolean();
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeBoolean(this.shouldLockMovement);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		LockMovement.isMovementLocked = this.shouldLockMovement;
	}
}
