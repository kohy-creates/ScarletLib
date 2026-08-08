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
	private static final ResourceLocation KEY_PRESSED_BUTTON_TEXTURE = ScarletLib.of("textures/gui/prompt/button_pressed.png");

	private static final long ANIMATION_DURATION_MS = 120L;
	private static final float MIN_SCALE = 0.70f;
	private static ProximityPromptClientData displayedPrompt = null;
	private static Vec3 displayedLocation = null;
	private static long animationStartTime = 0L;
	private static boolean animatingIn = false;
	private static boolean animatingOut = false;
	private static float animationProgress = 1.0f;

	public static void render(GuiGraphics guiGraphics, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) {
			return;
		}

		ProximityPromptClientData activePrompt = PromptClientHandler.ACTIVE_PROMPT;
		if (activePrompt != null) {
			if (displayedPrompt == null || !displayedPrompt.getUuid().equals(activePrompt.getUuid())) {
				displayedPrompt = activePrompt;
				displayedLocation = activePrompt.getLocation();
				startAnimation(true);
			} else {
				displayedLocation = activePrompt.getLocation();
			}
		}

		if (activePrompt == null && displayedPrompt != null && !animatingOut) {
			startAnimation(false);
		}

		if (displayedPrompt == null || displayedLocation == null) {
			return;
		}

		updateAnimation();

		Vec2 screenPos = projectToScreen(displayedLocation, partialTicks);
		if (Float.isNaN(screenPos.x) || Float.isNaN(screenPos.y)) {
			return;
		}

		int x = (guiGraphics.guiWidth() / 2) + (int) screenPos.x;
		int y = (guiGraphics.guiHeight() / 2) + (int) screenPos.y;
		renderPromptUI(guiGraphics, x, y, displayedPrompt, animationProgress);

		if (animatingOut && animationProgress <= 0.0f) {
			displayedPrompt = null;
			displayedLocation = null;
			animatingOut = false;
		}
	}

	private static void startAnimation(boolean entering) {
		animationStartTime = System.currentTimeMillis();
		animatingIn = entering;
		animatingOut = !entering;
		if (entering) {
			animationProgress = 0.0f;
		} else {
			animationProgress = 1.0f;
		}
	}

	private static void updateAnimation() {
		long elapsed = System.currentTimeMillis() - animationStartTime;
		float progress = Math.min(1.0f, (float) elapsed / ANIMATION_DURATION_MS);
		float easedProgress = 1.0f - (float) Math.pow(1.0f - progress, 3);
		if (animatingIn) {
			animationProgress = easedProgress;
		} else if (animatingOut) {
			animationProgress = 1.0f - easedProgress;
		}
	}

	private static void renderPromptUI(
			GuiGraphics graphics,
			int x, int y,
			ProximityPromptClientData prompt,
			float animationProgress
	) {
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

		float scale = MIN_SCALE + ((1.0f - MIN_SCALE) * animationProgress);
		int alpha = (int) (255.0f * animationProgress);

		alpha = Math.max(0, Math.min(255, alpha));

		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		graphics.pose().scale(scale, scale, 1.0f);
		graphics.pose().translate(-boxWidth / 2.0f, -boxHeight / 2.0f, 0);

		float alphaFloat = alpha / 255.0f;
		graphics.setColor(1.0f, 1.0f, 1.0f, alphaFloat);

		graphics.blitNineSlicedSized(BACKGROUND_TEXTURE, 0, 0, boxWidth, boxHeight, 3, 9, 9, 0, 0, 9, 9);

		if (prompt.holdingTicks > 0 && prompt.holdTimeTicks() > 0) {
			float progressFactor = Math.min(1.0f, (float) prompt.holdingTicks / prompt.holdTimeTicks());
			int progressBarWidth = (int) ((boxWidth - 6) * progressFactor);
			int progressColor = (alpha << 24) | 0x55FF55;
			graphics.fill(2, boxHeight - 4, 3 + progressBarWidth, boxHeight - 2, progressColor);
		}

		var keyY = padding + ((contentHeight - keyBoxSize) / 2);
		graphics.blit(
				prompt.isBeingHeld() ? KEY_PRESSED_BUTTON_TEXTURE : KEY_BUTTON_TEXTURE,
				padding, keyY,
				0, 0,
				keyBoxSize, keyBoxSize,
				16, 16
		);

		int white = (alpha << 24) | 0xFFFFFF;

		int keyTextX = padding + (keyBoxSize - keyWidth) / 2;
		int keyTextY = keyY + (keyBoxSize - font.lineHeight) / 2 + (prompt.isBeingHeld() ? 2 : 1);

		graphics.drawString(font, keyText, keyTextX, keyTextY, white, true);

		int textX = padding + keyBoxSize + spacing;

		graphics.drawString(font, objectText, textX, padding, (alpha << 24) | 0xAAAAAA, true);
		graphics.drawString(font, actionText, textX, padding + font.lineHeight + 2, white, true);
		graphics.pose().popPose();
		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
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
