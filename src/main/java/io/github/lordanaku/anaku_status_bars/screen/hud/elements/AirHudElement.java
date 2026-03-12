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
import net.minecraft.world.entity.player.Player;

import java.awt.Color;
import java.util.List;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.AIR;

public class AirHudElement implements IHudElement {
    private boolean renderSide = AIR.side();

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        if (getAirProgress() <= 0) {
            RenderHudFunctions.drawStatusEffectBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR, Settings.colorSettings.get(Settings.HEALTH.name() + "_hurt"));
        } else {
            RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        }
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getAirProgress(), Settings.colorSettings.get(AIR.name()), Settings.alphaSettings.get(AIR.name()));
    }
    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        if (getAirProgress() <= 0) {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.BUBBLE_BURST_ICON, 81);
        } else {
            RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.BUBBLE_ICON, 81);
        }
    }
    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(player.getAirSupply())), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(AIR.name()), 81);
    }
    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() {
        if (!Settings.shouldRenderSettings.get(AIR.name())) return false;
        assert Minecraft.getInstance().player != null;
        return Minecraft.getInstance().player.getAirSupply() < 300;
    }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(AIR.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(AIR.name()); }
    @Override public String name() { return AIR.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_air_bar"))
                .binding(AIR.shouldRender(), () -> Settings.shouldRenderSettings.get(AIR.name()), v -> Settings.shouldRenderSettings.replace(AIR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        iconOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_air_icon"))
                .binding(AIR.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(AIR.name()), v -> Settings.shouldRenderIconSettings.replace(AIR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        textOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_air_text"))
                .binding(AIR.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(AIR.name()), v -> Settings.shouldRenderTextSettings.replace(AIR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.air_color"))
                .binding(new Color(AIR.color(), false), () -> new Color(Settings.colorSettings.get(AIR.name()), false), v -> Settings.colorSettings.replace(AIR.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.air_text_color"))
                .binding(new Color(AIR.color(), false), () -> new Color(Settings.textColorSettings.get(AIR.name()), false), v -> Settings.textColorSettings.replace(AIR.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.air_alpha"))
                .binding(AIR.alpha(), () -> Settings.alphaSettings.get(AIR.name()), v -> Settings.alphaSettings.replace(AIR.name(), v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private int getAirProgress() {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        float ratio = Math.min(1, Math.max(0, player.getAirSupply() / 300f));
        return (int) Math.min(81, Math.ceil(ratio * 81));
    }
}
