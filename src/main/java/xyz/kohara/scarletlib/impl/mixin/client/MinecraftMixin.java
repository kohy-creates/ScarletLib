package xyz.kohara.scarletlib.impl.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.scarletlib.impl.prompt.PromptClientHandler;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@WrapWithCondition(
			method = "handleKeybinds",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
			)
	)
	private boolean prioritizePrompts(ClientPacketListener instance, Packet<?> pPacket) {
		return (PromptClientHandler.ACTIVE_PROMPT == null && PromptClientHandler.HAND_SWAP_COOLDOWN == 0);
	}
}
