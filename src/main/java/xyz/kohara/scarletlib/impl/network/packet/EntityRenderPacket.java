package xyz.kohara.scarletlib.impl.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;
import xyz.kohara.scarletlib.impl.client.EntityRenderCheck;

public class EntityRenderPacket extends ScarletLibBasePacket {

	private final int entityId;
	private final boolean shouldHide;

	public EntityRenderPacket(Entity entity, boolean shouldHide) {
		this.entityId = entity.getId();
		this.shouldHide = shouldHide;
	}

	public EntityRenderPacket(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.shouldHide = buf.readBoolean();
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeBoolean(this.shouldHide);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		if (this.shouldHide) {
			EntityRenderCheck.hideEntity(this.entityId);
		}
		else {
			EntityRenderCheck.showEntity(this.entityId);
		}
	}
}
