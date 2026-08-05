package xyz.kohara.scarletlib.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.network.ScarletLibPackets;
import xyz.kohara.scarletlib.network.packet.ShowRainbowActionBarMessageS2CPacket;

public class PlayerUtil {

	public static boolean isStandingStill(Player player) {
		return EntityUtil.isStandingStill(player);
	}

	public static void sendRainbowActionbarMessage(ServerPlayer player, Component component) {
		ScarletLibPackets.INSTANCE.sendToPlayer(new ShowRainbowActionBarMessageS2CPacket(component), player);
	}

	/**
	 *
	 * @param player Player to check
	 * @param targetPos Target position as a 3 directional vector
	 * @param dotThreshold Dot threshold. 1.0 is looking exactly in the direction of the point,
	 *                     0.0 is a maximum of a 90 degree angle.
	 * @return
	 */
	public static boolean isFacingLocation(Player player, Vec3 targetPos, double dotThreshold) {
		Vec3 eyePos = player.getEyePosition();
		Vec3 toTarget = targetPos.subtract(eyePos).normalize();
		Vec3 lookVec = player.getLookAngle();
		double dot = lookVec.dot(toTarget);
		return dot >= dotThreshold;
	}
}
