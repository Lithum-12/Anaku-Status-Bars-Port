package io.github.lordanaku.anaku_status_bars.utils.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.Option;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public interface IHudElement {
    void renderBar(GuiGraphics guiGraphics, PoseStack poseStack);
    void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack);
    void renderText(GuiGraphics guiGraphics, PoseStack poseStack);
    boolean getSide();
    IHudElement setRenderSide(boolean side);
    boolean shouldRender();
    boolean shouldRenderIcon();
    boolean shouldRenderText();

    /**
     * 向各分类的 Option 列表中添加本元素的配置项。
     * Config.java 会将这些 Option 汇总后构建 YACL 配置界面。
     *
     * @param mainOptions      主开关列表
     * @param iconOptions      图标开关列表
     * @param textOptions      文字开关列表
     * @param colorOptions     进度条颜色列表
     * @param textColorOptions 文字颜色列表
     * @param alphaOptions     透明度列表
     */
    void registerSettings(
            List<Option<?>> mainOptions,
            List<Option<?>> iconOptions,
            List<Option<?>> textOptions,
            List<Option<?>> colorOptions,
            List<Option<?>> textColorOptions,
            List<Option<?>> alphaOptions
    );

    String name();
}
