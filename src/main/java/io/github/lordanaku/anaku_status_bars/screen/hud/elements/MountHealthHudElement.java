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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;
import java.util.List;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.MOUNT_HEALTH;

public class MountHealthHudElement implements IHudElement {
    private boolean renderSide = MOUNT_HEALTH.side();

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getMountHealthProgress(), Settings.colorSettings.get(MOUNT_HEALTH.name()), Settings.alphaSettings.get(MOUNT_HEALTH.name()));
    }
    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_OUTLINE_ICON, 81);
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.HEART_MOUNT_ICON, 81);
    }
    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        LivingEntity mount = getRiddenEntity(); assert mount != null;
        RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(mount.getHealth())), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(MOUNT_HEALTH.name()), 81);
    }
    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() {
        if (!Settings.shouldRenderSettings.get(MOUNT_HEALTH.name())) return false;
        assert Minecraft.getInstance().player != null;
        return getRiddenEntity() != null;
    }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(MOUNT_HEALTH.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(MOUNT_HEALTH.name()); }
    @Override public String name() { return MOUNT_HEALTH.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_mount_health_bar"))
                .binding(MOUNT_HEALTH.shouldRender(), () -> Settings.shouldRenderSettings.get(MOUNT_HEALTH.name()), v -> Settings.shouldRenderSettings.replace(MOUNT_HEALTH.name(), v)).controller(TickBoxControllerBuilder::create).build());
        iconOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_mount_health_icon"))
                .binding(MOUNT_HEALTH.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(MOUNT_HEALTH.name()), v -> Settings.shouldRenderIconSettings.replace(MOUNT_HEALTH.name(), v)).controller(TickBoxControllerBuilder::create).build());
        textOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_mount_health_text"))
                .binding(MOUNT_HEALTH.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(MOUNT_HEALTH.name()), v -> Settings.shouldRenderTextSettings.replace(MOUNT_HEALTH.name(), v)).controller(TickBoxControllerBuilder::create).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.mount_health_color"))
                .binding(new Color(MOUNT_HEALTH.color(), false), () -> new Color(Settings.colorSettings.get(MOUNT_HEALTH.name()), false), v -> Settings.colorSettings.replace(MOUNT_HEALTH.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.mount_health_text_color"))
                .binding(new Color(MOUNT_HEALTH.color(), false), () -> new Color(Settings.textColorSettings.get(MOUNT_HEALTH.name()), false), v -> Settings.textColorSettings.replace(MOUNT_HEALTH.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.mount_health_alpha"))
                .binding(MOUNT_HEALTH.alpha(), () -> Settings.alphaSettings.get(MOUNT_HEALTH.name()), v -> Settings.alphaSettings.replace(MOUNT_HEALTH.name(), v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private static LivingEntity getRiddenEntity() {
        Player player = Minecraft.getInstance().player; assert player != null;
        Entity mount = player.getVehicle();
        return (mount instanceof LivingEntity) ? (LivingEntity) mount : null;
    }
    private static int getMountHealthProgress() {
        LivingEntity mount = getRiddenEntity(); assert mount != null;
        float ratio = Math.min(1, Math.max(0, mount.getHealth() / mount.getMaxHealth()));
        return (int) Math.min(81, Math.ceil(ratio * 81));
    }
}
