package io.github.lordanaku.anaku_status_bars.screen.hud.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.lordanaku.anaku_status_bars.api.RenderHudFunctions;
import io.github.lordanaku.anaku_status_bars.compat.HomeostaticHelper;
import io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper;
import io.github.lordanaku.anaku_status_bars.utils.Settings;
import io.github.lordanaku.anaku_status_bars.utils.TextureRecords;
import io.github.lordanaku.anaku_status_bars.utils.interfaces.IHudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.awt.Color;
import java.util.List;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.WATER;

public class WaterHudElement implements IHudElement {
    private boolean renderSide = WATER.side();

    private static boolean isHomeostaticLoaded() {
        return ModList.get().isLoaded("homeostatic");
    }

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        int waterLevel = HomeostaticHelper.getWaterLevel(player);
        RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getWaterProgress(waterLevel), Settings.colorSettings.get(WATER.name()), Settings.alphaSettings.get(WATER.name()));
        if (Settings.shouldRenderSettings.get(WATER.name() + "_saturation")) {
            RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getSaturationProgress(HomeostaticHelper.getWaterSaturationLevel(player)), Settings.colorSettings.get(WATER.name() + "_saturation"), Settings.alphaSettings.get(WATER.name() + "_saturation"));
        }
    }
    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.BUBBLE_ICON, 81);
    }
    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        int waterLevel = HomeostaticHelper.getWaterLevel(player);
        float saturation = HomeostaticHelper.getWaterSaturationLevel(player);
        if (Settings.shouldRenderSettings.get(WATER.name() + "_saturation") && saturation > 0) {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(waterLevel + saturation)), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(WATER.name() + "_saturation"), 81);
        } else {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(waterLevel), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(WATER.name()), 81);
        }
    }
    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() { return Settings.shouldRenderSettings.get(WATER.name()) && isHomeostaticLoaded(); }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(WATER.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(WATER.name()); }
    @Override public String name() { return WATER.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_water_bar"))
                .binding(WATER.shouldRender(), () -> Settings.shouldRenderSettings.get(WATER.name()), v -> Settings.shouldRenderSettings.replace(WATER.name(), v)).controller(TickBoxControllerBuilder::create).build());
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_water_saturation_bar"))
                .binding(WATER.shouldRender(), () -> Settings.shouldRenderSettings.get(WATER.name() + "_saturation"), v -> Settings.shouldRenderSettings.replace(WATER.name() + "_saturation", v)).controller(TickBoxControllerBuilder::create).build());
        iconOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_water_icon"))
                .binding(WATER.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(WATER.name()), v -> Settings.shouldRenderIconSettings.replace(WATER.name(), v)).controller(TickBoxControllerBuilder::create).build());
        textOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_water_text"))
                .binding(WATER.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(WATER.name()), v -> Settings.shouldRenderTextSettings.replace(WATER.name(), v)).controller(TickBoxControllerBuilder::create).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.water_color"))
                .binding(new Color(WATER.color(), false), () -> new Color(Settings.colorSettings.get(WATER.name()), false), v -> Settings.colorSettings.replace(WATER.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.water_saturation_color"))
                .binding(new Color(Settings.WATER_SATURATION_COLOR_DEFAULT, false), () -> new Color(Settings.colorSettings.get(WATER.name() + "_saturation"), false), v -> Settings.colorSettings.replace(WATER.name() + "_saturation", v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.water_text_color"))
                .binding(new Color(WATER.color(), false), () -> new Color(Settings.textColorSettings.get(WATER.name()), false), v -> Settings.textColorSettings.replace(WATER.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.water_saturation_text_color"))
                .binding(new Color(Settings.WATER_SATURATION_COLOR_DEFAULT, false), () -> new Color(Settings.textColorSettings.get(WATER.name() + "_saturation"), false), v -> Settings.textColorSettings.replace(WATER.name() + "_saturation", v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.water_alpha"))
                .binding(WATER.alpha(), () -> Settings.alphaSettings.get(WATER.name()), v -> Settings.alphaSettings.replace(WATER.name(), v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.water_saturation_alpha"))
                .binding(WATER.alpha(), () -> Settings.alphaSettings.get(WATER.name() + "_saturation"), v -> Settings.alphaSettings.replace(WATER.name() + "_saturation", v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private int getWaterProgress(int level) {
        return (int) Math.min(81, Math.ceil(81 * Math.min(1f, Math.max(0f, level / 20f))));
    }
    private int getSaturationProgress(float saturation) {
        return (int) Math.min(81, Math.ceil(81 * Math.min(1f, Math.max(0f, saturation / 5f))));
    }
}
