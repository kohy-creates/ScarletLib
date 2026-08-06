package xyz.kohara.scarletlib.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.network.ScarletLibPackets;
import xyz.kohara.scarletlib.network.packet.ShowRainbowActionBarMessageS2CPacket;

import java.util.Comparator;
import java.util.List;

public class PlayerUtil {

	public static boolean isStandingStill(Player player) {
		return EntityUtil.isStandingStill(player);
	}

	/**
	 * Sends a rainbow action bar message (e.g. like the ones sent when a Music Disc is inserted into a Jukebox)
	 */
	public static void sendRainbowActionbarMessage(ServerPlayer player, Component component) {
		ScarletLibPackets.INSTANCE.sendToPlayer(new ShowRainbowActionBarMessageS2CPacket(component), player);
	}

	/**
	 * Checks if a player is facing given coordinates.
	 * @param player Player to check
	 * @param targetPos Target position as a 3 directional vector
	 * @param dotThreshold Dot threshold. 1.0 is looking exactly in the direction of the point,
	 *                     0.0 is a maximum of a 90-degree angle.
	 */
	public static boolean isFacingLocation(Player player, Vec3 targetPos, double dotThreshold) {
		Vec3 eyePos = player.getEyePosition();
		Vec3 toTarget = targetPos.subtract(eyePos).normalize();
		Vec3 lookVec = player.getLookAngle();
		double dot = lookVec.dot(toTarget);
		return dot >= dotThreshold;
	}

	/**
	 * Returns all players in a radius around a getLocation
	 * @param location Center getLocation
	 * @param radius Radius in blocks
	 */
	public static List<Player> getPlayersInRadius(Level level, Vec3 location, double radius) {
		AABB box = new AABB(
				location.x - radius, location.y - radius, location.z - radius,
				location.x + radius, location.y + radius, location.z + radius
		);

		List<Player> players = level.getEntitiesOfClass(Player.class, box);

		players.removeIf(p -> p.distanceToSqr(location.x + 0.5, location.y + 0.5, location.z + 0.5) > radius * radius);
		return players;
	}

	/**
	 * Returns the nearest player in a radius around a given entity
	 * @param entity Target entity
	 * @param radius Radius in blocks
	 */
	public static Player getNearestPlayerWithinRadius(Entity entity, double radius) {
		return getNearestPlayerWithinRadius(entity.level(), entity.position(), radius);
	}

	/**
	 * Returns the nearest player in a radius around a set location
	 * @param location Location as a vector
	 * @param radius Radius in blocks
	 */
	public static Player getNearestPlayerWithinRadius(Level level, Vec3 location, double radius) {
		return getPlayersInRadius(level, location, radius)
				.stream()
				.min(Comparator.comparingDouble(p -> p.distanceToSqr(location)))
				.orElse(null);
	}
}
