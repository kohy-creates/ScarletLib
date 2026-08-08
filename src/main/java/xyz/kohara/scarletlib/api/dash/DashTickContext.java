package xyz.kohara.scarletlib.api.dash;

import net.minecraft.world.entity.LivingEntity;

public record DashTickContext(LivingEntity entity, int tick, int duration) {

	public double getProgress() {
		return (double) tick / duration;
	}
}