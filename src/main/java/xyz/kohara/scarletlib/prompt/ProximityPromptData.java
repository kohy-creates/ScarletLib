package xyz.kohara.scarletlib.prompt;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.api.prompt.ProximityPrompt;

import java.util.Objects;
import java.util.UUID;

public final class ProximityPromptData {
	private final UUID uuid;
	private final String id;
	private final Component actionText;
	private final Component objectText;
	private final Vec3 location;
	private final ResourceKey<Level> dimension;
	private final double maxDistance;
	private final int holdTimeTicks;

	public int holdingTicks = 0;

	public ProximityPromptData(
			UUID uuid,
			String id,
			Component actionText,
			Component objectText,
			Vec3 location,
			ResourceKey<Level> dimension,
			double maxDistance,
			int holdTimeTicks

	) {
		this.uuid = uuid;
		this.id = id;
		this.actionText = actionText;
		this.objectText = objectText;
		this.location = location;
		this.dimension = dimension;
		this.maxDistance = maxDistance;
		this.holdTimeTicks = holdTimeTicks;
	}


	public static ProximityPromptData fromPrompt(ProximityPrompt prompt) {
		return new ProximityPromptData(
				prompt.getUuid(),
				prompt.getId(),
				prompt.getActionText(),
				prompt.getObjectText(),
				prompt.getLocation(),
				prompt.getLevel().dimension(),
				prompt.getMaxDistance(),
				prompt.getHoldTimeTicks()
		);
	}


	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(uuid);
		buf.writeUtf(id);
		buf.writeComponent(actionText);
		buf.writeComponent(objectText);
		buf.writeDouble(location.x);
		buf.writeDouble(location.y);
		buf.writeDouble(location.z);
		buf.writeResourceLocation(dimension.location());
		buf.writeDouble(maxDistance);
		buf.writeInt(holdTimeTicks);
	}

	public static ProximityPromptData decode(FriendlyByteBuf buf) {
		UUID uuid = buf.readUUID();
		String id = buf.readUtf();
		Component actionText = buf.readComponent();
		Component objectText = buf.readComponent();
		Vec3 location = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

		ResourceLocation dimLocation = buf.readResourceLocation();
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimLocation);

		double maxDistance = buf.readDouble();
		int holdTimeTicks = buf.readInt();
		boolean isInstantInteract = buf.readBoolean();

		return new ProximityPromptData(
				uuid, id, actionText, objectText, location, dimension, maxDistance, holdTimeTicks
		);
	}

	public UUID uuid() {
		return uuid;
	}

	public String id() {
		return id;
	}

	public Component actionText() {
		return actionText;
	}

	public Component objectText() {
		return objectText;
	}

	public Vec3 location() {
		return location;
	}

	public ResourceKey<Level> dimension() {
		return dimension;
	}

	public double maxDistance() {
		return maxDistance;
	}

	public int holdTimeTicks() {
		return holdTimeTicks;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ProximityPromptData) obj;
		return Objects.equals(this.uuid, that.uuid) &&
				Objects.equals(this.id, that.id) &&
				Objects.equals(this.actionText, that.actionText) &&
				Objects.equals(this.objectText, that.objectText) &&
				Objects.equals(this.location, that.location) &&
				Objects.equals(this.dimension, that.dimension) &&
				Double.doubleToLongBits(this.maxDistance) == Double.doubleToLongBits(that.maxDistance) &&
				this.holdTimeTicks == that.holdTimeTicks;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, id, actionText, objectText, location, dimension, maxDistance, holdTimeTicks);
	}

	@Override
	public String toString() {
		return "ProximityPromptData[" +
				"uuid=" + uuid + ", " +
				"id=" + id + ", " +
				"actionText=" + actionText + ", " +
				"objectText=" + objectText + ", " +
				"location=" + location + ", " +
				"dimension=" + dimension + ", " +
				"maxDistance=" + maxDistance + ", " +
				"holdTimeTicks=" + holdTimeTicks + ']';
	}

}