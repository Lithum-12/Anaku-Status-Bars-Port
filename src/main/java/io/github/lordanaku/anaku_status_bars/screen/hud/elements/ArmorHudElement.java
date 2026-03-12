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
import net.minecraft.world.item.ArmorItem;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.ARMOR;

public class ArmorHudElement implements IHudElement {
    private boolean renderSide = ARMOR.side();

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getArmorProgress(), Settings.colorSettings.get(ARMOR.name()), Settings.alphaSettings.get(ARMOR.name()));
    }
    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.ARMOR_ICON, 81);
    }
    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(getArmorDamageValue()), getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()), Settings.textColorSettings.get(ARMOR.name()), 81);
    }
    @Override public boolean getSide() { return this.renderSide; }
    @Override public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }
    @Override public boolean shouldRender() {
        if (!Settings.shouldRenderSettings.get(ARMOR.name())) return false;
        assert Minecraft.getInstance().player != null;
        return Minecraft.getInstance().player.getArmorValue() > 0;
    }
    @Override public boolean shouldRenderIcon() { return shouldRender() && Settings.shouldRenderIconSettings.get(ARMOR.name()); }
    @Override public boolean shouldRenderText() { return shouldRender() && Settings.shouldRenderTextSettings.get(ARMOR.name()); }
    @Override public String name() { return ARMOR.name(); }

    @Override
    public void registerSettings(List<Option<?>> mainOptions, List<Option<?>> iconOptions,
                                  List<Option<?>> textOptions, List<Option<?>> colorOptions,
                                  List<Option<?>> textColorOptions, List<Option<?>> alphaOptions) {
        mainOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_armor_bar"))
                .binding(ARMOR.shouldRender(), () -> Settings.shouldRenderSettings.get(ARMOR.name()), v -> Settings.shouldRenderSettings.replace(ARMOR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        iconOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_armor_icon"))
                .binding(ARMOR.shouldRenderIcon(), () -> Settings.shouldRenderIconSettings.get(ARMOR.name()), v -> Settings.shouldRenderIconSettings.replace(ARMOR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        textOptions.add(Option.<Boolean>createBuilder().name(Component.translatable("option.anaku_status_bars.enable_armor_text"))
                .binding(ARMOR.shouldRenderText(), () -> Settings.shouldRenderTextSettings.get(ARMOR.name()), v -> Settings.shouldRenderTextSettings.replace(ARMOR.name(), v)).controller(TickBoxControllerBuilder::create).build());
        colorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.color_armor"))
                .binding(new Color(ARMOR.color(), false), () -> new Color(Settings.colorSettings.get(ARMOR.name()), false), v -> Settings.colorSettings.replace(ARMOR.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        textColorOptions.add(Option.<Color>createBuilder().name(Component.translatable("option.anaku_status_bars.armor_text_color"))
                .binding(new Color(ARMOR.color(), false), () -> new Color(Settings.textColorSettings.get(ARMOR.name()), false), v -> Settings.textColorSettings.replace(ARMOR.name(), v.getRGB() & 0xFFFFFF)).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false)).build());
        alphaOptions.add(Option.<Float>createBuilder().name(Component.translatable("option.anaku_status_bars.alpha_armor"))
                .binding(ARMOR.alpha(), () -> Settings.alphaSettings.get(ARMOR.name()), v -> Settings.alphaSettings.replace(ARMOR.name(), v)).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f)).build());
    }

    private int getArmorProgress() {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        final float[] d = {0, 0};
        player.getArmorSlots().forEach(s -> { d[0] += s.getMaxDamage() - s.getDamageValue(); d[1] += s.getMaxDamage(); });
        float ratio = d[1] == 0 ? 0 : Math.min(1, Math.max(0, d[0] / d[1]));
        return (int) Math.min(81, Math.ceil(ratio * 81));
    }
    private int getArmorDamageValue() {
        assert Minecraft.getInstance().player != null; Player player = Minecraft.getInstance().player;
        AtomicInteger v = new AtomicInteger();
        player.getArmorSlots().forEach(s -> { if (s.getItem() instanceof ArmorItem) v.addAndGet(s.getMaxDamage() - s.getDamageValue()); });
        return v.get();
    }
}
