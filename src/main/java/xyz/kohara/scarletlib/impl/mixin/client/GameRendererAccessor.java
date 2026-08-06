package xyz.kohara.scarletlib.impl.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Invoker("getFov")
	double scarletlib$getFov(Camera pActiveRenderInfo, float pPartialTicks, boolean pUseFOVSetting);
}
