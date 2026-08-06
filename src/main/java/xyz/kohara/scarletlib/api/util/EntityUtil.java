package xyz.kohara.scarletlib.api.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xyz.kohara.scarletlib.network.ScarletLibPackets;
import xyz.kohara.scarletlib.network.packet.AddEntityParticleEmitterS2CPacket;

public class EntityUtil {

	/**
	 * Checks if the entity is standing still or moving.
	 * @param entity The entity to check.
	 */
	public static boolean isStandingStill(Entity entity) {
		var delta = entity.getDeltaMovement();
		return delta.x + delta.y + delta.z < 0.001;
	}

	/**
	 * Adds a particle emitter (e.g. what enchanted hit particles rely on) to an entity.
	 */
	public static void addParticleEmitter(Entity entity, ParticleOptions particleOptions) {
		ScarletLibPackets.INSTANCE.sendToTracking(new AddEntityParticleEmitterS2CPacket(entity, particleOptions), entity);
	}

	/**
	 * Adds a particle emitter (e.g. what enchanted hit particles rely on) to an entity, but only for a specific player.
	 */
	public static void addParticleEmitter(Entity entity, ParticleOptions particleOptions, ServerPlayer player) {
		ScarletLibPackets.INSTANCE.sendToPlayer(new AddEntityParticleEmitterS2CPacket(entity, particleOptions), player);
	}
}
