package xyz.kohara.scarletlib.api.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import xyz.kohara.scarletlib.network.ScarletLibPackets;
import xyz.kohara.scarletlib.network.packet.AddEntityParticleEmitterS2CPacket;

public class EntityUtil {

	public static boolean isStandingStill(Entity entity) {
		var delta = entity.getDeltaMovement();
		return delta.x + delta.y + delta.z < 0.001;
	}

	public static void addParticleEmitter(Entity entity, ParticleOptions particleOptions) {
		ScarletLibPackets.INSTANCE.sendToTracking(new AddEntityParticleEmitterS2CPacket(entity, particleOptions), entity);
	}
}
