package io.github.lordanaku.anaku_status_bars.utils.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.GuiGraphics;

public interface IHudElement {
    void renderBar(GuiGraphics guiGraphics, PoseStack poseStack);
    void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack);
    void renderText(GuiGraphics guiGraphics, PoseStack poseStack);
    boolean getSide();
    IHudElement setRenderSide(boolean side);
    boolean shouldRender();
    boolean shouldRenderIcon();
    boolean shouldRenderText();
    void registerSettings(ConfigCategory mainCategory, ConfigCategory iconCategory, ConfigCategory textCategory, ConfigCategory colorCategory, ConfigCategory textColorSettings, ConfigCategory alphaCategory, ConfigEntryBuilder builder);
    String name();
}
