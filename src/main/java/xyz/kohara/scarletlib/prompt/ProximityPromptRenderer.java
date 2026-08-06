package xyz.kohara.scarletlib.prompt;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import xyz.kohara.scarletlib.ScarletLib;
import xyz.kohara.scarletlib.ScarletLibClient;
import xyz.kohara.scarletlib.mixin.client.GameRendererAccessor;

import java.util.function.Supplier;

public class ProximityPromptRenderer {

	private static final ResourceLocation BACKGROUND_TEXTURE = ScarletLib.of("textures/gui/prompt/background.png");
	private static final ResourceLocation KEY_BUTTON_TEXTURE = ScarletLib.of("textures/gui/prompt/button.png");
	private static final Supplier<Component> INTERACTION_KEY = () -> ScarletLibClient.keybinds().INTERACT_WITH_PROMPT.getKey().getDisplayName();

	public static void render(GuiGraphics guiGraphics, float partialTicks) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || Minecraft.getInstance().screen != null) return;

		Vec2 screenPos = projectToScreen(ScarletLibClient.ACTIVE_PROMPT.getLocation(), partialTicks);

		renderPromptUI(guiGraphics, (guiGraphics.guiWidth() + (int) screenPos.x) / 2, (guiGraphics.guiHeight() + (int) screenPos.y) / 2, ScarletLibClient.ACTIVE_PROMPT);
	}

	private static void renderPromptUI(GuiGraphics graphics, int x, int y, ProximityPromptClientData prompt) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;

		Component keyText = INTERACTION_KEY.get();
		Component objectText = prompt.getObjectText();
		Component actionText = prompt.getActionText();

		int keyWidth = font.width(keyText);
		int objectWidth = font.width(objectText);
		int actionWidth = font.width(actionText);

		int textBlockWidth = Math.max(objectWidth, actionWidth);
		int padding = 6;
		int keyBoxSize = 12;
		int spacing = 6;

		int contentWidth = keyBoxSize + spacing + textBlockWidth;
		int contentHeight = Math.max(keyBoxSize, font.lineHeight * 2 + 2);

		int boxWidth = contentWidth + (padding * 2);
		int boxHeight = contentHeight + (padding * 2);

		int drawX = x - (boxWidth / 2);
		int drawY = y - (boxHeight / 2);

		graphics.blitNineSlicedSized(BACKGROUND_TEXTURE, drawX, drawY, boxWidth, boxHeight, 3, boxWidth, boxHeight, 0, 0, 9, 9);

		if (prompt.holdingTicks > 0 && prompt.holdTimeTicks() > 0) {
			float progressFactor = Math.min(1.0f, (float) prompt.holdingTicks / prompt.holdTimeTicks());
			int progressBarWidth = (int) ((boxWidth - 6) * progressFactor);
			graphics.fill(drawX, drawY + boxHeight - 3, drawX + progressBarWidth, drawY + boxHeight, 0xFF55FF55); // Green progress
		}

		int keyX = drawX + padding;
		int keyY = drawY + padding + ((contentHeight - keyBoxSize) / 2);
		graphics.blit(KEY_BUTTON_TEXTURE, keyX, keyY, 0, 0, keyBoxSize, keyBoxSize);

		int keyTextX = keyX + (keyBoxSize - keyWidth) / 2;
		int keyTextY = keyY + (keyBoxSize - font.lineHeight) / 2 + 1;
		graphics.drawString(font, keyText, keyTextX, keyTextY, 0xFFFFFF, false);

		int textX = keyX + keyBoxSize + spacing;
		int textY = drawY + padding;

		graphics.drawString(font, objectText, textX, textY, 0xAAAAAA, true);

		graphics.drawString(font, actionText, textX, textY + font.lineHeight + 2, 0xFFFFFF, true);
	}

	private static Vec2 projectToScreen(Vec3 worldPos, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.getMainCamera();

		Vec3 cameraPos = camera.getPosition();
		Vec3 relative = worldPos.subtract(cameraPos);

		Vec3 look = new Vec3(camera.getLookVector());
		Vec3 up = new Vec3(camera.getUpVector());
		Vec3 left = new Vec3(camera.getLeftVector());

		double cameraX = -relative.dot(left);
		double cameraY = relative.dot(up);
		double cameraZ = relative.dot(look);

		if (cameraZ <= 0.01) {
			return new Vec2(Float.NaN, Float.NaN);
		}

		double fov = Math.toRadians(((GameRendererAccessor) mc.gameRenderer).scarletlib$getFov(camera, partialTicks, true));
		double scale = 1.0 / Math.tan(fov / 2.0);
		double aspect = (double) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getGuiScaledHeight();

		double normalizedX = (cameraX / cameraZ) * scale / aspect;
		double normalizedY = (cameraY / cameraZ) * scale;
		float screenX = (float) (normalizedX * mc.getWindow().getGuiScaledWidth() / 2.0);
		float screenY = (float) (-normalizedY * mc.getWindow().getGuiScaledHeight() / 2.0);
		return new Vec2(screenX, screenY);
	}
}
