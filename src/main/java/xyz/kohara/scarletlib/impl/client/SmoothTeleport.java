package xyz.kohara.scarletlib.impl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class SmoothTeleport {

		public static final Map<Integer, SmoothTeleport> TELEPORTS = new HashMap<>();

		public final Vec3 start;
		public final Vec3 end;
		public final int duration;

		public int age;

		public SmoothTeleport(Vec3 start, Vec3 end, int duration) {
			this.start = start;
			this.end = end;
			this.duration = duration;
			this.age = 0;
		}

		public boolean finished() {
			return age >= duration;
		}

		public Vec3 getPosition(float partialTick) {
			double t = (age + partialTick) / (double) duration;
			t = Mth.clamp(t, 0.0, 1.0);

			return start.lerp(end, t);
		}

	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
		LivingEntity entity = event.getEntity();

		var teleport = TELEPORTS.get(entity.getId());
		if (teleport == null) return;

		if (entity != Minecraft.getInstance().player) {
			teleport.age++;
			if (teleport.finished()) {
				TELEPORTS.remove(entity.getId());
				return;
			}
		}

		Vec3 visual = teleport.getPosition(event.getPartialTick());
		double vanillaX = Mth.lerp(event.getPartialTick(),entity.xo,entity.getX());
		double vanillaY = Mth.lerp(event.getPartialTick(), entity.yo, entity.getY());
		double vanillaZ = Mth.lerp(event.getPartialTick(), entity.zo, entity.getZ());

		event.getPoseStack().translate(visual.x - vanillaX, visual.y - vanillaY, visual.z - vanillaZ);
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		SmoothTeleport teleport = TELEPORTS.get(mc.player.getId());
		if (teleport == null) return;

		Vec3 previous = teleport.getPosition(0.0F);

		teleport.age++;
		if (teleport.finished()) {
			Vec3 end = teleport.end;

			mc.player.setPosRaw(end.x, end.y, end.z);
			mc.player.xOld = end.x;
			mc.player.yOld = end.y;
			mc.player.zOld = end.z;
			mc.player.xo = end.x;
			mc.player.yo = end.y;
			mc.player.zo = end.z;

			TELEPORTS.remove(mc.player.getId());
			return;
		}

		Vec3 current = teleport.getPosition(0.0F);

		mc.player.xOld = previous.x;
		mc.player.yOld = previous.y;
		mc.player.zOld = previous.z;

		mc.player.xo = previous.x;
		mc.player.yo = previous.y;
		mc.player.zo = previous.z;

		mc.player.setPosRaw(current.x, current.y, current.z);
	}
}
