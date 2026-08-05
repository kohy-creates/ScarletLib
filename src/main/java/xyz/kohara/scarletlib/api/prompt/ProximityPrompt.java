package xyz.kohara.scarletlib.api.prompt;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.scarletlib.prompt.registry.ServerProximityPromptRegistry;

import java.util.UUID;
import java.util.function.Supplier;

public class ProximityPrompt {

	private final String id;
	private final Component actionText;
	private final Component objectText;
	private final Pair<Supplier<Vec3>, Level> location;
	private final double maxDistance;
	private final int holdTimeTicks;
	private final UUID uuid;

	private final @Nullable Integer entity;
	private final @Nullable Block block;

	private int holdTime;

	public ProximityPrompt(Builder builder) {
		this.id = builder.id;
		this.actionText = builder.actionText;
		this.objectText = builder.objectText;
		this.location = builder.location;
		this.maxDistance = builder.maxDistance;
		this.holdTimeTicks = builder.holdTimeTicks;
		this.uuid = builder.uuid != null ? builder.uuid : UUID.randomUUID();

		this.entity = builder.entity;
		this.block = builder.block;

		ServerProximityPromptRegistry.register(this);
	}

	public String getId() {
		return this.id;
	}

	public Component getActionText() {
		return this.actionText;
	}

	public Component getObjectText() {
		return this.objectText;
	}

	public Vec3 getLocation() {
		return this.location.getFirst().get();
	}

	public Level getLevel() {
		return this.location.getSecond();
	}

	public double getMaxDistance() {
		return this.maxDistance;
	}

	public int getHoldTimeTicks() {
		return this.holdTimeTicks;
	}

	public boolean isInstantInteract() {
		return this.holdTimeTicks == 0;
	}

	public UUID getUuid() {
		return uuid;
	}

	public @Nullable Integer getEntity() {
		return entity;
	}

	public @Nullable Block getBlock() {
		return block;
	}

	public static class Builder {

		public String id;
		public Component actionText;
		public Component objectText;
		public Pair<Supplier<Vec3>, Level> location = null;
		public double maxDistance = 3d;
		public int holdTimeTicks = 0;
		public Integer entity = null;
		public Block block = null;
		public UUID uuid = null;

		public Builder(String id) {
			this.id = id;
		}

		/**
		 * Optional. If not present, randomizes the UUID.
		 * @param uuid The desired UUID.
		 */
		public Builder setUUID(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder setActionText(Component component) {
			this.actionText = component;
			return this;
		}

		public Builder setObjectText(Component component) {
			this.objectText = component;
			return this;
		}

		public Builder interactionRange(double range) {
			this.maxDistance = range;
			return this;
		}

		public Builder holdTickToProcess(int amount) {
			this.holdTimeTicks = amount;
			return this;
		}

		public Builder forLocation(Vec3 loc, Level level) {
			this.location = Pair.of(() -> loc, level);
			return this;
		}

		public Builder forBlock(Block block, BlockPos pos, Level level) {
			this.location = Pair.of(() -> pos.getCenter(), level);
			this.block = block;
			return this;
		}

		public Builder forEntity(Entity entity) {
			this.location = Pair.of(() -> entity.position(), entity.level());
			this.entity = entity.getId();
			return this;
		}

		public ProximityPrompt build() {
			if (this.location == null) {
				throw new RuntimeException("Proximity Prompt location cannot be null.");
			}
			return new ProximityPrompt(this);
		}
	}
}
