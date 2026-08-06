package xyz.kohara.scarletlib.impl.prompt;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import xyz.kohara.scarletlib.ScarletLib;
import xyz.kohara.scarletlib.ScarletLibClient;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;
import xyz.kohara.scarletlib.impl.mixin.client.GameRendererAccessor;

public class ProximityPromptRenderer {

	private static final ResourceLocation BACKGROUND_TEXTURE = ScarletLib.of("textures/gui/prompt/background.png");
	private static final ResourceLocation KEY_BUTTON_TEXTURE = ScarletLib.of("textures/gui/prompt/button.png");
	private static final ResourceLocation KEY_PRESSED_BUTTON_TEXTURE = ScarletLib.of("textures/gui/prompt/button.png");

	public static void render(GuiGraphics guiGraphics, float partialTicks) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || Minecraft.getInstance().screen != null) return;

		Vec2 screenPos = projectToScreen(ScarletLibClient.ACTIVE_PROMPT.getLocation(), partialTicks);
		if (Float.isNaN(screenPos.x) || Float.isNaN(screenPos.y)) return;

		int x = (guiGraphics.guiWidth() / 2) + (int) screenPos.x;
		int y = (guiGraphics.guiHeight() / 2) + (int) screenPos.y;

		renderPromptUI(guiGraphics, x, y, ScarletLibClient.ACTIVE_PROMPT);
	}

	private static void renderPromptUI(GuiGraphics graphics, int x, int y, ProximityPromptClientData prompt) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;

		Component keyText = ScarletLibClient.keybinds().INTERACT_WITH_PROMPT.getKey().getDisplayName();
		Component objectText = prompt.getObjectText();
		Component actionText = prompt.getActionText();

		int keyWidth = font.width(keyText);
		int objectWidth = font.width(objectText);
		int actionWidth = font.width(actionText);

		int textBlockWidth = Math.max(objectWidth, actionWidth);
		int padding = 6;
		int keyBoxSize = 16;
		int spacing = 6;

		int contentWidth = keyBoxSize + spacing + textBlockWidth;
		int contentHeight = Math.max(keyBoxSize, font.lineHeight * 2 + 2);

		int boxWidth = contentWidth + (padding * 2);
		int boxHeight = contentHeight + (padding * 2);

		int drawX = x - (boxWidth / 2);
		int drawY = y - (boxHeight / 2);

		graphics.blitNineSlicedSized(BACKGROUND_TEXTURE, drawX, drawY, boxWidth, boxHeight, 3, 9, 9, 0, 0, 9, 9);

		if (prompt.holdingTicks > 0 && prompt.holdTimeTicks() > 0) {
			float progressFactor = Math.min(1.0f, (float) prompt.holdingTicks / prompt.holdTimeTicks());
			int progressBarWidth = (int) ((boxWidth - 6) * progressFactor);
			graphics.fill(drawX, drawY + boxHeight - 3, drawX + progressBarWidth, drawY + boxHeight, 0xFF55FF55);
		}

		int keyX = drawX + padding;
		int keyY = drawY + padding + ((contentHeight - keyBoxSize) / 2);
		graphics.blit((prompt.isBeingHeld()) ? KEY_PRESSED_BUTTON_TEXTURE : KEY_BUTTON_TEXTURE, keyX, keyY, 0, 0, keyBoxSize, keyBoxSize, 16, 16);

		int keyTextX = keyX + (keyBoxSize - keyWidth) / 2;
		int keyTextY = keyY + (keyBoxSize - font.lineHeight) / 2 + 1 - (((prompt.isBeingHeld()) ? 5 : 0));
		graphics.drawString(font, keyText, keyTextX, keyTextY, 0xFFFFFF, true);

		int textX = keyX + keyBoxSize + spacing;
		int textY = drawY + padding;

		graphics.drawString(font, objectText, textX, textY, 0xAAAAAA, true);

		graphics.drawString(font, actionText, textX, textY + font.lineHeight + 2, 0xFFFFFF, true);
	}

	private static Vec2 projectToScreen(Vec3 worldPos, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.getMainCamera();

		Vec3 cameraPos = camera.getPosition();

		double relX = worldPos.x - cameraPos.x;
		double relY = worldPos.y - cameraPos.y;
		double relZ = worldPos.z - cameraPos.z;

		Vector3f look = camera.getLookVector();
		Vector3f up = camera.getUpVector();
		Vector3f left = camera.getLeftVector();

		double cameraX = -(relX * left.x + relY * left.y + relZ * left.z);
		double cameraY = relX * up.x + relY * up.y + relZ * up.z;
		double cameraZ = relX * look.x + relY * look.y + relZ * look.z;

		if (cameraZ <= 0.01) {
			return new Vec2(Float.NaN, Float.NaN);
		}

		double fov = Math.toRadians(((GameRendererAccessor) mc.gameRenderer).scarletlib$getFov(camera, partialTicks, true));
		double scale = 1.0 / Math.tan(fov / 2.0);

		double scaledWidth = mc.getWindow().getGuiScaledWidth();
		double scaledHeight = mc.getWindow().getGuiScaledHeight();
		double aspect = scaledWidth / scaledHeight;

		double normalizedX = (cameraX / cameraZ) * scale / aspect;
		double normalizedY = (cameraY / cameraZ) * scale;

		float screenX = (float) (normalizedX * scaledWidth / 2.0);
		float screenY = (float) (-normalizedY * scaledHeight / 2.0);
		return new Vec2(screenX, screenY);
	}
}
