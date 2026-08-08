package xyz.kohara.scarletlib.api.prompt;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Essentially a copy of a Proximity Prompt, but stored on the client.
 * From the client, you cannot directly interact with the prompt, only its data.
 *
 * @see ProximityPrompt
 */
@OnlyIn(Dist.CLIENT)
public final class ProximityPromptClientData {
	private final UUID uuid;
	private final String id;
	private final Component actionText;
	private final Component objectText;
	private Vec3 location;
	private final ResourceKey<Level> dimension;
	private final double maxDistance;
	private final int holdTimeTicks;
	private final boolean isDynamicLocation;
	private final int boundEntityId;

	public int holdingTicks = 0;

	public ProximityPromptClientData(
			UUID uuid,
			String id,
			Component actionText,
			Component objectText,
			Vec3 location,
			ResourceKey<Level> dimension,
			double maxDistance,
			int holdTimeTicks,
			boolean isDynamicLocation,
			int boundEntityId

	) {
		this.uuid = uuid;
		this.id = id;
		this.actionText = actionText;
		this.objectText = objectText;
		this.location = location;
		this.dimension = dimension;
		this.maxDistance = maxDistance;
		this.holdTimeTicks = holdTimeTicks;
		this.isDynamicLocation = isDynamicLocation;
		this.boundEntityId = boundEntityId;
	}

	/**
	 * Manually messing with the client prompt's location is NOT recommended.
	 */
	public void updateLocation(Vec3 newPos) {
		this.location = newPos;
	}

	public boolean isBeingHeld() {
		return this.holdingTicks > 0;
	}

	public static ProximityPromptClientData fromPrompt(ProximityPrompt prompt) {
		boolean dynamic = false;
		int entityId = 0;
		if (prompt.isBoundToEntity()) {
			dynamic = true;
			entityId = prompt.getEntity();
		}

		return new ProximityPromptClientData(
				prompt.getUuid(),
				prompt.getId(),
				prompt.getActionText(),
				prompt.getObjectText(),
				prompt.getLocation(),
				prompt.getLevel().dimension(),
				prompt.getMaxDistance(),
				prompt.getHoldTimeTicks(),
				dynamic,
				entityId
		);
	}


	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.uuid);
		buf.writeUtf(this.id);
		buf.writeComponent(this.actionText);
		buf.writeComponent(this.objectText);
		buf.writeDouble(this.location.x);
		buf.writeDouble(this.location.y);
		buf.writeDouble(this.location.z);
		buf.writeResourceLocation(this.dimension.location());
		buf.writeDouble(this.maxDistance);
		buf.writeInt(this.holdTimeTicks);
		buf.writeBoolean(this.isDynamicLocation);
		buf.writeInt(this.boundEntityId);
	}

	public static ProximityPromptClientData decode(FriendlyByteBuf buf) {
		UUID uuid = buf.readUUID();
		String id = buf.readUtf();
		Component actionText = buf.readComponent();
		Component objectText = buf.readComponent();
		Vec3 location = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

		ResourceLocation dimLocation = buf.readResourceLocation();
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimLocation);

		double maxDistance = buf.readDouble();
		int holdTimeTicks = buf.readInt();

		boolean dynamic = buf.readBoolean();
		int entityId = buf.readInt();

		return new ProximityPromptClientData(uuid, id, actionText, objectText, location, dimension, maxDistance, holdTimeTicks, dynamic, entityId);
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getId() {
		return id;
	}

	public Component getActionText() {
		return actionText;
	}

	public Component getObjectText() {
		return objectText;
	}

	public Vec3 getLocation() {
		return location;
	}

	public ResourceKey<Level> getDimension() {
		return dimension;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public int holdTimeTicks() {
		return holdTimeTicks;
	}

	public boolean isInstantInteract() {
		return this.holdTimeTicks == 0;
	}

	public boolean hasDynamicLocation() {
		return this.isDynamicLocation;
	}

	public @Nullable Entity getEntity() {
		if (hasDynamicLocation()) {
			return Minecraft.getInstance().player.level().getEntity(boundEntityId);
		}
		return null;
	}

}