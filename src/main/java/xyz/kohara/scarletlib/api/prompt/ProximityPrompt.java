package xyz.kohara.scarletlib.api.prompt;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.scarletlib.impl.prompt.ServerProximityPromptRegistry;

import java.util.UUID;

public class ProximityPrompt {

	private final String id;
	private final Component actionText;
	private final Component objectText;
	private final Pair<Vec3, Level> location;
	private final double maxDistance;
	private final int holdTimeTicks;
	private final UUID uuid;

	private final @Nullable Integer entity;
	private final @Nullable Block block;

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
		return this.location.getFirst();
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

	/**
	 * Returns the ID (not to be confused with EntityType/ResourceLocation)
	 * of the entity the prompt is bound to. Null if the prompt is not bound to an entity.
	 *
	 * @return ID of the entity the prompt is bound to.
	 */
	public @Nullable Integer getEntity() {
		return entity;
	}

	/**
	 * Returns the block object of the block the prompt is bound to.
	 * Null if the prompt is not bound to a block.
	 *
	 * @return Block object of the block the prompt is bound to.
	 */
	public @Nullable Block getBlock() {
		return block;
	}

	public boolean isBoundToEntity() {
		return this.entity != null;
	}

	public boolean isBoundToBlock() {
		return this.block != null;
	}

	public boolean isUnbound() {
		return this.entity == null && this.block == null;
	}

	public Builder recreate() {
		var builder = new ProximityPrompt.Builder(this.id);

		builder.actionText = this.actionText;
		builder.objectText = this.objectText;
		builder.location = Pair.of(this.location.getFirst(), this.location.getSecond());
		builder.maxDistance = this.maxDistance;
		builder.holdTimeTicks = this.holdTimeTicks;

		builder.entity = this.entity;
		builder.block = this.block;

		return builder;
	}

	public static class Builder {

		public String id;
		public Component actionText;
		public Component objectText;
		public Pair<Vec3, Level> location = null;
		public double maxDistance = 3d;
		public int holdTimeTicks = 0;
		public Integer entity = null;
		public Block block = null;
		public UUID uuid = null;

		/**
		 * Builder for Proximity Prompts
		 *
		 * @param id String id of this prompt.
		 *           This is used for handling visibility and interaction events.
		 *           Different prompts can use the same ID without conflicting with one another,
		 *           so you don't need to worry about that happening.
		 */
		public Builder(String id) {
			this.id = id;
		}

		/**
		 * Optional. If not present, randomizes the UUID.
		 * This is separate from prompt's ID for handling visibility
		 * and effects. You generally don't need to worry about UUID stuff.
		 *
		 * @param uuid The desired UUID.
		 */
		public Builder setUUID(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		/**
		 * Optional. Sets the prompt's action text.
		 * [OBJECT TEXT]
		 * [ACTION TEXT]
		 *
		 * @param component Text component to display
		 */
		public Builder setActionText(Component component) {
			this.actionText = component;
			return this;
		}

		/**
		 * Optional. Sets the prompt's object text.
		 * [OBJECT TEXT]
		 * [ACTION TEXT]
		 */
		public Builder setObjectText(Component component) {
			this.objectText = component;
			return this;
		}

		/**
		 * Sets the prompt's maximum interaction range.
		 * The prompt will only be visible to players closer than this value
		 * and therefore can only be interacted by them if so.
		 */
		public Builder interactionRange(double range) {
			this.maxDistance = range;
			return this;
		}

		/**
		 * Optional. If set to 0 (default), the prompt will be interacted with instantly
		 * upon pressing the interact key. If set to a value above 0,
		 * interactions will require holding the interact key for a set amount of ticks instead.
		 */
		public Builder holdTickToProcess(int amount) {
			this.holdTimeTicks = amount;
			return this;
		}

		/**
		 * Binds the builder to a location in a world.
		 *
		 * @param loc   Location represented as a vector
		 * @param level Target level. Has to be a level object.
		 */
		public Builder forLocation(Vec3 loc, Level level) {
			this.location = Pair.of(loc, level);
			return this;
		}

		/**
		 * Binds the builder to a block.
		 * The prompt will be removed automatically whenever
		 * the block it's bound to is broken.
		 *
		 * @param block Target block
		 * @param pos   Position of the target block
		 * @param level Target level. Has to be a level object.
		 */
		public Builder forBlock(Block block, BlockPos pos, Level level) {
			this.location = Pair.of(pos.getCenter(), level);
			this.block = block;
			return this;
		}

		/**
		 * Binds the builder to an entity.
		 * The prompt will dynamically update its location
		 * and 'follow' the target entity.
		 * THe prompt will be removed automatically whenever
		 * the entity dies.
		 *
		 * @param entity Target entity.
		 */
		public Builder forEntity(Entity entity) {
			this.location = Pair.of(entity.position(), entity.level());
			this.entity = entity.getId();
			return this;
		}

		public ProximityPrompt build() {
			if (this.location == null) {
				throw new RuntimeException("Proximity Prompt getLocation cannot be null.");
			}
			return new ProximityPrompt(this);
		}
	}
}
