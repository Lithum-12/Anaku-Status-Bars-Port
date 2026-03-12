package io.github.lordanaku.anaku_status_bars.screen.hud.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.lordanaku.anaku_status_bars.api.RenderHudFunctions;
import io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper;
import io.github.lordanaku.anaku_status_bars.utils.Settings;
import io.github.lordanaku.anaku_status_bars.utils.TextureRecords;
import io.github.lordanaku.anaku_status_bars.utils.interfaces.IHudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;
import java.util.List;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.HEALTH;

public class HealthHudElement implements IHudElement {
    private boolean renderSide = HEALTH.side();

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        if (player.hasEffect(MobEffects.POISON)) {
            RenderHudFunctions.drawStatusEffectBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR, Settings.colorSettings.get(HEALTH.name() + "_poison"));
        } else if (player.hasEffect(MobEffects.WITHER)) {
            RenderHudFunctions.drawStatusEffectBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR, Settings.colorSettings.get(HEALTH.name() + "_wither"));
        } else if (player.isFreezing()) {
            RenderHudFunctions.drawStatusEffectBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR, Settings.colorSettings.get(HEALTH.name() + "_frostbite"));
        } else {
            RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        }
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getHealthProgress(), Settings.colorSettings.get(HEALTH.name()), Settings.alphaSettings.get(HEALTH.name()));
        if (Settings.shouldRenderSettings.get(HEALTH.name() + "_absorption")) {
            RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getAbsorptionProgress(), Settings.colorSettings.get(HEALTH.name() + "_absorption"), Settings.alphaSettings.get(HEALTH.name() + "_absorption"));
        }
    }

    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_OUTLINE_ICON, 81);
        if (player.hasEffect(MobEffects.POISON)) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_POISON_ICON, 81);
        } else if (player.hasEffect(MobEffects.WITHER)) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_WITHER_ICON, 81);
        } else if (player.isFreezing()) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_FROSTBITE_ICON, 81);
        } else if (player.hasEffect(MobEffects.ABSORPTION)) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_ABSORPTION_ICON, 81);
        } else {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_ICON, 81);
        }
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        if (player.hasEffect(MobEffects.ABSORPTION) && player.getAbsorptionAmount() > 0 && Settings.shouldRenderSettings.get(HEALTH.name() + "_absorption")) {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(player.getHealth() + player.getAbsorptionAmount())), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(HEALTH.name() + "_absorption"), 81);
        } else {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(player.getHealth())), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(HEALTH.name()), 81);
        }
    }

    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() { return Settings.shouldRenderSettings.get(HEALTH.name()); }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(HEALTH.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(HEALTH.name()); }
    @Override public String name() { return HEALTH.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        // Main
        mainOptions.add(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.enable_health_bar"))
                .binding(HEALTH.shouldRender(), () -> Settings.shouldRenderSettings.get(HEALTH.name()), v -> Settings.shouldRenderSettings.replace(HEALTH.name(), v))
                .controller(TickBoxControllerBuilder::create).build());
        mainOptions.add(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.enable_health_absorption"))
                .binding(HEALTH.shouldRender(), () -> Settings.shouldRenderSettings.get(HEALTH.name() + "_absorption"), v -> Settings.shouldRenderSettings.replace(HEALTH.name() + "_absorption", v))
                .controller(TickBoxControllerBuilder::create).build());
        // Icon
        iconOptions.add(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.enable_health_icon"))
                .binding(HEALTH.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(HEALTH.name()), v -> Settings.shouldRenderIconSettings.replace(HEALTH.name(), v))
                .controller(TickBoxControllerBuilder::create).build());
        // Text
        textOptions.add(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.enable_health_text"))
                .binding(HEALTH.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(HEALTH.name()), v -> Settings.shouldRenderTextSettings.replace(HEALTH.name(), v))
                .controller(TickBoxControllerBuilder::create).build());
        // Color
        colorOptions.add(Option.<Color>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_bar_color"))
                .binding(new Color(HEALTH.color(), false), () -> new Color(Settings.colorSettings.get(HEALTH.name()), false), v -> Settings.colorSettings.replace(HEALTH.name(), v.getRGB() & 0xFFFFFF))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        colorOptions.add(Option.<Color>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_absorption_bar_color"))
                .binding(new Color(Settings.ABSORPTION_COLOR_DEFAULT, false), () -> new Color(Settings.colorSettings.get(HEALTH.name() + "_absorption"), false), v -> Settings.colorSettings.replace(HEALTH.name() + "_absorption", v.getRGB() & 0xFFFFFF))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        // Text Color
        textColorOptions.add(Option.<Color>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_text_color"))
                .binding(new Color(HEALTH.color(), false), () -> new Color(Settings.textColorSettings.get(HEALTH.name()), false), v -> Settings.textColorSettings.replace(HEALTH.name(), v.getRGB() & 0xFFFFFF))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_absorption_text_color"))
                .binding(new Color(Settings.ABSORPTION_COLOR_DEFAULT, false), () -> new Color(Settings.textColorSettings.get(HEALTH.name() + "_absorption"), false), v -> Settings.textColorSettings.replace(HEALTH.name() + "_absorption", v.getRGB() & 0xFFFFFF))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        // Alpha
        alphaOptions.add(Option.<Float>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_bar_alpha"))
                .binding(HEALTH.alpha(), () -> Settings.alphaSettings.get(HEALTH.name()), v -> Settings.alphaSettings.replace(HEALTH.name(), v))
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
        alphaOptions.add(Option.<Float>createBuilder()
                .name(Component.translatable("option.anaku_status_bars.health_absorption_bar_alpha"))
                .binding(HEALTH.alpha(), () -> Settings.alphaSettings.get(HEALTH.name() + "_absorption"), v -> Settings.alphaSettings.replace(HEALTH.name() + "_absorption", v))
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private int getHealthProgress() {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        float progress = Math.min(1, Math.max(0, player.getHealth() / player.getMaxHealth()));
        return (int) Math.min(81, Math.ceil(progress * 81));
    }
    private int getAbsorptionProgress() {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        float ratio = Math.min(1, Math.max(0, player.getAbsorptionAmount() / player.getMaxHealth()));
        return (int) Math.min(81, Math.ceil(ratio * 81));
    }
}
