package xyz.kohara.scarletlib.api.dash;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Dash {

	private final double distance;
	private final int duration;
	private final boolean invulnerable;
	private final boolean disableGravity;
	private final Vec3 direction;

	private final double entityHitRadius;

	private final @Nullable Consumer<LivingEntity> onStart;
	private final @Nullable Consumer<DashTickContext> onTick;
	private final @Nullable BiConsumer<LivingEntity, LivingEntity> onEntityHit;
	private final @Nullable Consumer<LivingEntity> onEnd;
	private final @Nullable Consumer<LivingEntity> onWallHit;

	public Dash(Builder builder) {
		this.distance = builder.distance;
		this.duration = builder.duration;
		this.invulnerable = builder.invulnerable;
		this.disableGravity = builder.disableGravity;
		this.direction = builder.direction;

		this.onStart = builder.onStart;
		this.onTick = builder.onTick;
		this.onEnd = builder.onEnd;

		this.entityHitRadius = builder.entityHitRadius;
		this.onEntityHit = builder.onEntityHit;

		this.onWallHit = builder.onWallHit;
	}

	public double getDistance() {
		return this.distance;
	}

	public int getDuration() {
		return this.duration;
	}

	public boolean isInvulnerableWhileDashing() {
		return this.invulnerable;
	}

	public boolean shouldDisableGravity() {
		return this.disableGravity;
	}

	public boolean allowsEntityHits() {
		return this.entityHitRadius > 0;
	}

	public double getEntityHitRadius() {
		return this.entityHitRadius;
	}

	public Vec3 getDirection() {
		return this.direction;
	}

	public @Nullable Consumer<LivingEntity> getOnStart() {
		return this.onStart;
	}

	public @Nullable Consumer<DashTickContext> getOnTick() {
		return this.onTick;
	}

	public @Nullable BiConsumer<LivingEntity, LivingEntity> getOnEntityHit() {
		return this.onEntityHit;
	}

	public @Nullable Consumer<LivingEntity> getOnEnd() {
		return this.onEnd;
	}

	public @Nullable Consumer<LivingEntity> getOnWallHit() {
		return this.onWallHit;
	}

	public static class Builder {

		public double distance;
		public int duration;
		public boolean invulnerable = false;
		public boolean disableGravity = true;
		public Vec3 direction;

		public double entityHitRadius = 0d;

		public @Nullable Consumer<LivingEntity> onStart = null;
		public @Nullable Consumer<DashTickContext> onTick = null;
		public @Nullable BiConsumer<LivingEntity, LivingEntity> onEntityHit = null;
		public @Nullable Consumer<LivingEntity> onEnd = null;
		public @Nullable Consumer<LivingEntity> onWallHit = null;

		public Builder(double distance, int duration, Vec3 direction) {
			this.distance = distance;
			this.duration = duration;
			this.direction = direction;
		}

		public Builder onStart(Consumer<LivingEntity> callback) {
			this.onStart = callback;
			return this;
		}

		public Builder onTick(Consumer<DashTickContext> callback) {
			this.onTick = callback;
			return this;
		}

		public Builder onEntityHit(double hitRadius, BiConsumer<LivingEntity, LivingEntity> callback) {
			this.onEntityHit = callback;
			this.entityHitRadius = hitRadius;
			return this;
		}

		public Builder onEnd(Consumer<LivingEntity> callback) {
			this.onEnd = callback;
			return this;
		}

		public Builder disablesGravity(boolean value) {
			this.disableGravity = value;
			return this;
		}

		public Builder invulnerableDuringDash(boolean value) {
			this.invulnerable = value;
			return this;
		}

		public Builder onWallHit(Consumer<LivingEntity> callback) {
			this.onWallHit = callback;
			return this;
		}

		public Dash build() {
			return new Dash(this);
		}
	}
}