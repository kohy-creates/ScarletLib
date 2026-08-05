package xyz.kohara.scarletlib.prompt;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import xyz.kohara.scarletlib.ScarletLibClient;

public class ProximityPromptRenderer {

	public static void render(GuiGraphics guiGraphics, float partialTicks) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		// 2. Project world pos to screen
		Matrix4f modelView = RenderSystem.getModelViewMatrix();
		Matrix4f projection = RenderSystem.getProjectionMatrix();
		Vec2 screenPos = projectToScreen(ScarletLibClient.ACTIVE_PROMPT.location(), modelView, projection);

		if (screenPos == null) return;

		renderPromptUI(guiGraphics, (int) screenPos.x, (int) screenPos.y, ScarletLibClient.ACTIVE_PROMPT, ScarletLibClient.ACTIVE_PROMPT.holdTimeTicks());
	}

	private static void renderPromptUI(GuiGraphics graphics, int x, int y, ProximityPromptData prompt, int progress) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;

		Component keyText = Component.literal("F");
		Component objectText = prompt.objectText();
		Component actionText = prompt.actionText();

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

		int backgroundColor = 0xD0000000;
		int borderColor = 0x40FFFFFF;

		graphics.fill(drawX, drawY, drawX + boxWidth, drawY + boxHeight, backgroundColor);
		graphics.renderOutline(drawX, drawY, boxWidth, boxHeight, borderColor);

		if (progress > 0 && prompt.holdTimeTicks() > 0) {
			float progressFactor = Math.min(1.0f, (float) progress / prompt.holdTimeTicks());
			int progressBarWidth = (int) (boxWidth * progressFactor);
			graphics.fill(drawX, drawY + boxHeight - 2, drawX + progressBarWidth, drawY + boxHeight, 0xFF55FF55); // Green progress
		}

		int keyX = drawX + padding;
		int keyY = drawY + padding + ((contentHeight - keyBoxSize) / 2);

		int keyBgColor = 0xFF222222;
		int keyBorderColor = 0xFFAAAAAA;

		graphics.fill(keyX, keyY, keyX + keyBoxSize, keyY + keyBoxSize, keyBgColor);
		graphics.renderOutline(keyX, keyY, keyBoxSize, keyBoxSize, keyBorderColor);

		int keyTextX = keyX + (keyBoxSize - keyWidth) / 2;
		int keyTextY = keyY + (keyBoxSize - font.lineHeight) / 2 + 1;
		graphics.drawString(font, keyText, keyTextX, keyTextY, 0xFFFFFF, false);

		int textX = keyX + keyBoxSize + spacing;
		int textY = drawY + padding;

		graphics.drawString(font, objectText, textX, textY, 0xAAAAAA, true);

		graphics.drawString(font, actionText, textX, textY + font.lineHeight + 2, 0xFFFFFF, true);
	}

	private static Vec2 projectToScreen(Vec3 worldPos, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 camPos = camera.getPosition();

		Vector4f target = new Vector4f(
				(float) (worldPos.x - camPos.x),
				(float) (worldPos.y - camPos.y),
				(float) (worldPos.z - camPos.z),
				1.0f
		);

		target.mul(modelViewMatrix);
		target.mul(projectionMatrix);

		if (target.w() <= 0) return null;

		float ndcX = target.x() / target.w();
		float ndcY = target.y() / target.w();

		Window window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getGuiScaledWidth();
		int screenHeight = window.getGuiScaledHeight();

		float screenX = (ndcX + 1.0f) / 2.0f * screenWidth;
		float screenY = (1.0f - ndcY) / 2.0f * screenHeight;

		return new Vec2(screenX, screenY);
	}
}
