package xyz.kohara.scarletlib.impl.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.impl.client.SmoothTeleport;

public class SmoothTeleportS2CPacket extends ScarletLibBasePacket {

	private final int entityId;
	private final double oldX;
	private final double oldY;
	private final double oldZ;
	private final double newX;
	private final double newY;
	private final double newZ;
	private final int duration;

	public SmoothTeleportS2CPacket(int entityId, Vec3 oldLoc, Vec3 newLoc, int duration) {
		this.entityId = entityId;
		this.oldX = oldLoc.x;
		this.oldY = oldLoc.y;
		this.oldZ = oldLoc.z;
		this.newX = newLoc.x;
		this.newY = newLoc.y;
		this.newZ = newLoc.z;
		this.duration = duration;
	}

	public SmoothTeleportS2CPacket(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.oldX = buf.readDouble();
		this.oldY = buf.readDouble();
		this.oldZ = buf.readDouble();
		this.newX = buf.readDouble();
		this.newY = buf.readDouble();
		this.newZ = buf.readDouble();
		this.duration = buf.readInt();
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeDouble(this.oldX);
		buf.writeDouble(this.oldY);
		buf.writeDouble(this.oldZ);
		buf.writeDouble(this.newX);
		buf.writeDouble(this.newY);
		buf.writeDouble(this.newZ);
		buf.writeInt(this.duration);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		SmoothTeleport.TELEPORTS.put(this.entityId, new SmoothTeleport(
				new Vec3(this.oldX, this.oldY, this.oldZ),
				new Vec3(this.newX, this.newY, this.newZ),
				this.duration
		));
	}
}
