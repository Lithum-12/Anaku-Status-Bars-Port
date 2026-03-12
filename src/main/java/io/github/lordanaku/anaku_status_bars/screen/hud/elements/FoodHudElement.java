package io.github.lordanaku.anaku_status_bars.screen.hud.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.lordanaku.anaku_status_bars.api.RenderHudFunctions;
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

import static io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper.getPosYMod;
import static io.github.lordanaku.anaku_status_bars.utils.Settings.FOOD;

public class FoodHudElement implements IHudElement {
    private boolean renderSide = FOOD.side();
    private final int MAX_PROGRESS = 81;

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        if (player.hasEffect(MobEffects.HUNGER)) {
            RenderHudFunctions.drawStatusEffectBar(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.DEFAULT_BAR, Settings.colorSettings.get(FOOD.name() + "_hunger"));
        } else {
            RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        }
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getFoodProgress(), Settings.colorSettings.get(FOOD.name()), Settings.alphaSettings.get(FOOD.name()));
        if (Settings.shouldRenderSettings.get(FOOD.name() + "_saturation")) {
            RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getSaturationProgress(), Settings.colorSettings.get(FOOD.name() + "_saturation"), Settings.alphaSettings.get(FOOD.name() + "_saturation"));
        }
        if (Settings.shouldRenderSettings.get(FOOD.name() + "_exhaustion")) {
            RenderHudFunctions.drawExhaustBar(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.EXHAUSTION_BAR, getExhaustionProgress(), Settings.alphaSettings.get(FOOD.name() + "_exhaustion"));
        }
    }

    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.FOOD_OUTLINE_ICON, 81);
        if (player.hasEffect(MobEffects.HUNGER)) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.HUNGER_ICON, 81);
        } else {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), getPosYMod(getSide()), TextureRecords.FOOD_ICON, 81);
        }
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        if (Settings.shouldRenderSettings.get(FOOD.name() + "_saturation") && player.getFoodData().getSaturationLevel() > 0) {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(player.getFoodData().getFoodLevel() + player.getFoodData().getSaturationLevel())), getSide(), shouldRenderIcon(), getPosYMod(getSide()), Settings.textColorSettings.get(FOOD.name() + "_saturation"), 81);
        } else {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(player.getFoodData().getFoodLevel()), getSide(), shouldRenderIcon(), getPosYMod(getSide()), Settings.textColorSettings.get(FOOD.name()), 81);
        }
    }

    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() { return Settings.shouldRenderSettings.get(FOOD.name()); }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(FOOD.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(FOOD.name()); }
    @Override public String name() { return FOOD.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_food_bar"))
                .binding(FOOD.shouldRender(), () -> Settings.shouldRenderSettings.get(FOOD.name()), v -> Settings.shouldRenderSettings.replace(FOOD.name(), v)).controller(TickBoxControllerBuilder::create).build());
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_saturation_bar"))
                .binding(FOOD.shouldRender(), () -> Settings.shouldRenderSettings.get(FOOD.name() + "_saturation"), v -> Settings.shouldRenderSettings.replace(FOOD.name() + "_saturation", v)).controller(TickBoxControllerBuilder::create).build());
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_exhaustion_bar"))
                .binding(FOOD.shouldRender(), () -> Settings.shouldRenderSettings.get(FOOD.name() + "_exhaustion"), v -> Settings.shouldRenderSettings.replace(FOOD.name() + "_exhaustion", v)).controller(TickBoxControllerBuilder::create).build());
        iconOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_food_icon"))
                .binding(FOOD.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(FOOD.name()), v -> Settings.shouldRenderIconSettings.replace(FOOD.name(), v)).controller(TickBoxControllerBuilder::create).build());
        textOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_food_text"))
                .binding(FOOD.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(FOOD.name()), v -> Settings.shouldRenderTextSettings.replace(FOOD.name(), v)).controller(TickBoxControllerBuilder::create).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.food_color"))
                .binding(new Color(FOOD.color(), false), () -> new Color(Settings.colorSettings.get(FOOD.name()), false), v -> Settings.colorSettings.replace(FOOD.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.saturation_color"))
                .binding(new Color(Settings.SATURATION_COLOR_DEFAULT, false), () -> new Color(Settings.colorSettings.get(FOOD.name() + "_saturation"), false), v -> Settings.colorSettings.replace(FOOD.name() + "_saturation", v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.food_text_color"))
                .binding(new Color(FOOD.color(), false), () -> new Color(Settings.textColorSettings.get(FOOD.name()), false), v -> Settings.textColorSettings.replace(FOOD.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.saturation_text_color"))
                .binding(new Color(Settings.SATURATION_COLOR_DEFAULT, false), () -> new Color(Settings.textColorSettings.get(FOOD.name() + "_saturation"), false), v -> Settings.textColorSettings.replace(FOOD.name() + "_saturation", v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.food_alpha"))
                .binding(FOOD.alpha(), () -> Settings.alphaSettings.get(FOOD.name()), v -> Settings.alphaSettings.replace(FOOD.name(), v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.saturation_alpha"))
                .binding(FOOD.alpha(), () -> Settings.alphaSettings.get(FOOD.name() + "_saturation"), v -> Settings.alphaSettings.replace(FOOD.name() + "_saturation", v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.exhaustion_alpha"))
                .binding(Settings.ALPHA_DEFAULT, () -> Settings.alphaSettings.get(FOOD.name() + "_exhaustion"), v -> Settings.alphaSettings.replace(FOOD.name() + "_exhaustion", v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private int getFoodProgress() {
        assert Minecraft.getInstance().player != null; Player p = Minecraft.getInstance().player;
        return (int) Math.min(MAX_PROGRESS, Math.ceil(MAX_PROGRESS * Math.min(1, Math.max(0, p.getFoodData().getFoodLevel() / 20f))));
    }
    private int getSaturationProgress() {
        assert Minecraft.getInstance().player != null; Player p = Minecraft.getInstance().player;
        return (int) Math.min(MAX_PROGRESS, Math.ceil(MAX_PROGRESS * Math.min(1, Math.max(0, p.getFoodData().getSaturationLevel() / 20f))));
    }
    private int getExhaustionProgress() {
        assert Minecraft.getInstance().player != null; Player p = Minecraft.getInstance().player;
        return (int) Math.min(MAX_PROGRESS, Math.ceil(MAX_PROGRESS * Math.min(1, Math.max(0, p.getFoodData().getExhaustionLevel() / 4f))));
    }
}
