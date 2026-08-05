package xyz.kohara.scarletlib.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import xyz.kohara.scarletlib.api.network.ScarletLibBasePacket;

public class AddEntityParticleEmitterS2CPacket extends ScarletLibBasePacket {

	private final int id;
	private final ParticleOptions particleOptions;

	public AddEntityParticleEmitterS2CPacket(Entity entity, ParticleOptions particleOptions) {
		this.id = entity.getId();
		this.particleOptions = particleOptions;
	}

	public AddEntityParticleEmitterS2CPacket(FriendlyByteBuf buf) {
		this.id = buf.readInt();
		this.particleOptions = buf.readJsonWithCodec(ParticleTypes.CODEC);
	}

	@Override
	protected void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeJsonWithCodec(ParticleTypes.CODEC, this.particleOptions);
	}

	@Override
	protected void handleOnClient() {
		super.handleOnClient();
		var mc = Minecraft.getInstance();
		var entity = mc.level.getEntity(this.id);
		if (entity != null) {
			mc.particleEngine.createTrackingEmitter(entity, particleOptions);
		}
	}
}
