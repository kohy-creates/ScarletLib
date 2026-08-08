package xyz.kohara.scarletlib.api.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.api.ScarletLibScheduler;
import xyz.kohara.scarletlib.api.dash.Dash;
import xyz.kohara.scarletlib.api.dash.DashTickContext;
import xyz.kohara.scarletlib.impl.mixin.EntityAccessor;
import xyz.kohara.scarletlib.impl.network.ScarletLibPackets;
import xyz.kohara.scarletlib.impl.network.packet.AddEntityParticleEmitterS2CPacket;
import xyz.kohara.scarletlib.impl.network.packet.EntityRenderPacket;
import xyz.kohara.scarletlib.impl.prompt.ServerProximityPromptRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityUtil {

	/**
	 * Checks if the entity is standing still or moving.
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

	/**
	 * Checks if an entity has a Proximity Prompt with a specific ID attached to itself.
	 * Server-side only.
	 */
	public static boolean hasAProximityPromptAttached(Entity entity, String id) {
		if (entity.level().isClientSide)
			throw new RuntimeException("Attempted to call hasAProximityPromptAttached from the client side!");
		return ServerProximityPromptRegistry.getAllPrompts().stream()
				.anyMatch(proximityPrompt -> proximityPrompt.getId().equals(id)
						&& proximityPrompt.isBoundToEntity()
						&& proximityPrompt.getEntity() == entity.getId()
				);
	}

	/**
	 * Makes the entity COMPLETELY invisible.
	 * Pretty much just makes it not render at all on the clients.
	 */
	public static void makeInvisible(LivingEntity entity) {
		ScarletLibPackets.INSTANCE.sendToAllPlayers(new EntityRenderPacket(entity, true));
	}

	/**
	 * Makes the entity visible again when made invisible with the {@link #makeInvisible(LivingEntity)} method.
	 */
	public static void makeVisible(LivingEntity entity) {
		ScarletLibPackets.INSTANCE.sendToAllPlayers(new EntityRenderPacket(entity, false));
	}

	public static void performDash(LivingEntity entity, Dash dash) {
		performDash(entity, true, true, dash);
	}

	public static void performDash(
			LivingEntity entity,
			boolean isOmnidirectional,
			boolean ignoreDownwardAngleOnGround,
			Dash dash
	) {
		Vec3 dir = dash.getDirection().normalize();

		if (entity.isShiftKeyDown()) {
			dir = dir.scale(-1);
		}

		double angleY = Math.toDegrees(Math.asin(dir.y));

		if (!isOmnidirectional) {
			dir = new Vec3(dir.x, 0, dir.z).normalize();
		}
		else if (
				Math.abs(angleY) <= 10 ||
						(ignoreDownwardAngleOnGround && entity.onGround() && angleY < 0)
		) {
			dir = new Vec3(dir.x, 0, dir.z).normalize();
		}

		Vec3 dashVelocity = dir.scale(dash.getDistance() / dash.getDuration());

		Level level = entity.level();

		if (dash.getOnStart() != null) {
			dash.getOnStart().accept(entity);
		}

		if (dash.isInvulnerableWhileDashing()) {
			entity.invulnerableTime = dash.getDuration();
		}

		if (dash.shouldDisableGravity()) {
			entity.setNoGravity(true);
			entity.hurtMarked = true;
		}

		Set<UUID> hitEntities = new HashSet<>();

		AtomicBoolean wasWallHit = new AtomicBoolean(false);
		for (int tick = 0; tick < dash.getDuration(); tick++) {

			int currentTick = tick;
			ScarletLibScheduler.schedule(() -> {
				if (!entity.isAlive() || wasWallHit.get()) return;

				Vec3 actualMovement = ((EntityAccessor) entity).scarletLib$collide(dashVelocity);

				if (actualMovement.lengthSqr() < dashVelocity.lengthSqr()) {
					wasWallHit.set(true);
					stopDash(entity, dash, true);
					return;
				}

				entity.setDeltaMovement(actualMovement);
				entity.hurtMarked = true;
				updateServerPlayerPos(entity);

				if (dash.getOnTick() != null) {
					DashTickContext context = new DashTickContext(entity, currentTick, dash.getDuration());
					dash.getOnTick().accept(context);
				}

				if (dash.allowsEntityHits()) {
					List<LivingEntity> entities = level.getEntitiesOfClass(
							LivingEntity.class,
							entity.getBoundingBox().inflate(dash.getEntityHitRadius()),
							candidateEntity -> candidateEntity != entity && candidateEntity.isAlive()
					);

					for (LivingEntity hitEntity : entities) {
						if (hitEntities.contains(hitEntity.getUUID())) continue;
						hitEntities.add(hitEntity.getUUID());
						dash.getOnEntityHit().accept(entity, hitEntity);
					}
				}

			}, tick);
		}

		ScarletLibScheduler.schedule(() -> {
			if (!entity.isAlive() || wasWallHit.get()) {
				return;
			}
			stopDash(entity, dash, false);
		}, dash.getDuration());
	}

	private static void updateServerPlayerPos(LivingEntity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
		}
	}

	private static void stopDash(LivingEntity entity, Dash dash, boolean wasWallHit) {
		entity.setDeltaMovement(Vec3.ZERO);
		updateServerPlayerPos(entity);
		entity.hurtMarked = true;
		if (dash.shouldDisableGravity()) entity.setNoGravity(false);
		if (wasWallHit && dash.getOnWallHit() != null) dash.getOnWallHit().accept(entity);
		if (dash.getOnEnd() != null) dash.getOnEnd().accept(entity);
	}
}
