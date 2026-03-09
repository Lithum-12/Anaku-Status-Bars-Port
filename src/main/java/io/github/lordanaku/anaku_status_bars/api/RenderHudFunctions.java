package io.github.lordanaku.anaku_status_bars.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper;
import io.github.lordanaku.anaku_status_bars.utils.ColorUtils;
import io.github.lordanaku.anaku_status_bars.utils.Settings;
import io.github.lordanaku.anaku_status_bars.utils.interfaces.IHudElement;
import io.github.lordanaku.anaku_status_bars.utils.records.HudElementType;
import io.github.lordanaku.anaku_status_bars.utils.records.TextureRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class RenderHudFunctions {

    /**
     * Registers the HUD element for your mod.
     * @param type HudElementType Record that holds default values for your HUD element.
     * @param hudElement HudElement Class that implements IHudElement.
     */
    @SuppressWarnings("unused")
    public static void registerModHudElement(HudElementType type, IHudElement hudElement) {
        if(!RenderHudHelper.getHudElementRegistry().contains(hudElement)) {
            RenderHudHelper.registerHudElements(hudElement);
            if (!Settings.shouldRenderSettings.containsKey(type.name())) {
                Settings.shouldRenderSettings.put(type.name(), type.shouldRender());
                Settings.shouldRenderIconSettings.put(type.name(), type.shouldRenderIcon());
                Settings.shouldRenderTextSettings.put(type.name(), type.shouldRenderText());
                Settings.colorSettings.put(type.name(), type.color());
                Settings.textColorSettings.put(type.name(), type.color());
                Settings.alphaSettings.put(type.name(), type.alpha());
            }
            if (type.side()) {
                if (!Settings.sideOrderSettings.get("left").contains(type.name()) && !Settings.sideOrderSettings.get("right").contains(type.name())) {
                    Settings.sideOrderSettings.get("left").add(type.name());
                }
                Settings.LEFT_ORDER_DEFAULT.add(type.name());
            } else {
                if (!Settings.sideOrderSettings.get("left").contains(type.name()) && !Settings.sideOrderSettings.get("right").contains(type.name())) {
                    Settings.sideOrderSettings.get("right").add(type.name());
                }
                Settings.RIGHT_ORDER_DEFAULT.add(type.name());
            }
            RenderHudHelper.setupHudElements();
        }
    }

    /**
     * Draws the default background bar for the HUD.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param textureRecord - the texture record for the bar.
     */
    public static void drawDefaultBar(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord) {
        RenderSystem.setShaderTexture(0, textureRecord.texture());
        int finalX = (side) ? RenderHudHelper.getPosX(true) : RenderHudHelper.getPosX(false);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.blit(textureRecord.texture(), finalX, RenderHudHelper.getPosY() + posYMod,
                textureRecord.startX(), textureRecord.startY(),
                textureRecord.width(), textureRecord.height(),
                textureRecord.maxWidth(), textureRecord.maxHeight());
    }

    /**
     * Draws an overlay on the bar for an alternative way of showing info.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param textureRecord - the texture record for the bar.
     * @param progress - the progress of the bar.
     * @param alpha - the color of the bar. (Hex Value)
     */
    public static void drawExhaustBar(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord, int progress, float alpha) {
        RenderSystem.setShaderTexture(0, textureRecord.texture());
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        drawProgress(guiGraphics, poseStack, side, posYMod, textureRecord, progress);
    }

    /**
     * Draws the progress bar for the HUD.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param textureRecord - the texture record for the bar.
     * @param progress - the progress of the bar.
     * @param color - the color of the bar. (Hex Value)
     * @param alpha - the alpha of the bar.
     */
    public static void drawProgressBar(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord, int progress, int color, float alpha) {
        RenderSystem.setShaderTexture(0, textureRecord.texture());
        RenderSystem.setShaderColor(ColorUtils.fromHex(color).getRedF(), ColorUtils.fromHex(color).getGreenF(), ColorUtils.fromHex(color).getBlueF(), alpha);
        drawProgress(guiGraphics, poseStack, side, posYMod, textureRecord, progress);
    }

    /**
     * Draws Colorized bar for when status effect is applied.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param textureRecord - the texture record for the bar.
     * @param color - the color of the bar. (Hex Value)
     */
    public static void drawStatusEffectBar(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord, int color) {
        RenderSystem.setShaderTexture(0, textureRecord.texture());
        RenderSystem.setShaderColor(ColorUtils.fromHex(color).getRedF(), ColorUtils.fromHex(color).getGreenF(), ColorUtils.fromHex(color).getBlueF(), 1);
        int finalX = (side) ? RenderHudHelper.getPosX(true) : RenderHudHelper.getPosX(false);
        guiGraphics.blit(textureRecord.texture(), finalX, RenderHudHelper.getPosY() + posYMod,
                textureRecord.startX(), textureRecord.startY(),
                textureRecord.width(), textureRecord.height(),
                textureRecord.maxWidth(), textureRecord.maxHeight());
    }

    /**
     * Draws the icon for the bar.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param textureRecord - the texture record for the bar.
     * @param barWidth - the width of the bar so method can determine offset.
     */
    public static void drawIcon(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord, int barWidth) {
        RenderSystem.setShaderTexture(0, textureRecord.texture());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        if (side) {
            guiGraphics.blit(textureRecord.texture(),
                    RenderHudHelper.getPosX(true) - (textureRecord.width() + 1), RenderHudHelper.getPosY() + posYMod,
                    textureRecord.startX(), textureRecord.startY(),
                    textureRecord.width(), textureRecord.height(),
                    textureRecord.maxWidth(), textureRecord.maxHeight());
        } else {
            guiGraphics.blit(textureRecord.texture(),
                    RenderHudHelper.getPosX(false) + (barWidth + 1), RenderHudHelper.getPosY() + posYMod,
                    textureRecord.startX(), textureRecord.startY(),
                    textureRecord.width(), textureRecord.height(),
                    textureRecord.maxWidth(), textureRecord.maxHeight());
        }
    }

    /**
     * Draws the text for the Hud Element.
     * @param text - the text to draw.
     * @param side - true if the bar is on the left side of the screen, false if on the right side.
     * @param icon - rather or not the icon is being drawn.
     * @param posYMod - the amount you want to add to the base -40 y position.
     * @param color - the color of the text. (Hex Value)
     * @param barWidth - the width of the bar so method can determine offset.
     */
    public static void drawText(GuiGraphics guiGraphics, PoseStack poseStack, String text, boolean side, boolean icon, int posYMod, int color, int barWidth) {
        int finalX = (side) ? RenderHudHelper.getPosX(true) - (Minecraft.getInstance().font.width(text) + 1) : RenderHudHelper.getPosX(false) + (barWidth + 1);

        if (icon) {
            finalX = (side) ? finalX - 10 : finalX + 10;
        }

        guiGraphics.drawString(Minecraft.getInstance().font, text, finalX, RenderHudHelper.getPosY() + posYMod + 1, color);
    }

    private static void drawProgress(GuiGraphics guiGraphics, PoseStack poseStack, boolean side, int posYMod, TextureRecord textureRecord, int progress) {
        if (side) {
            guiGraphics.blit(textureRecord.texture(),
                    RenderHudHelper.getPosX(true), RenderHudHelper.getPosY() + posYMod,
                    textureRecord.startX(), textureRecord.startY(),
                    progress, textureRecord.height(),
                    textureRecord.maxWidth(), textureRecord.maxHeight());
        } else {
            guiGraphics.blit(textureRecord.texture(),
                    RenderHudHelper.getPosX(false) + (textureRecord.width() - progress), RenderHudHelper.getPosY() + posYMod,
                    textureRecord.startX() + (textureRecord.width() - progress), textureRecord.startY(),
                    textureRecord.width(), textureRecord.height(),
                    textureRecord.maxWidth(), textureRecord.maxHeight());
        }
    }
}
