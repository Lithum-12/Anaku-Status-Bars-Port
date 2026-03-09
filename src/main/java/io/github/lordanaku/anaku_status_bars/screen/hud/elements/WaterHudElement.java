package io.github.lordanaku.anaku_status_bars.screen.hud.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.lordanaku.anaku_status_bars.api.RenderHudFunctions;
import io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper;
import io.github.lordanaku.anaku_status_bars.utils.Settings;
import io.github.lordanaku.anaku_status_bars.utils.TextureRecords;
import io.github.lordanaku.anaku_status_bars.utils.interfaces.IHudElement;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;

import java.lang.reflect.Field;
import java.util.Optional;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.WATER;

public class WaterHudElement implements IHudElement {
    private boolean renderSide = WATER.side();

    // ── Capability 缓存（只反射一次）────────────────────────────────────────
    // 用 Object 存，避免编译期直接依赖 IWater 泛型
    private static Capability<?> cachedCapability = null;
    private static boolean capabilityResolved = false; // 是否已尝试过查找

    /**
     * 通过反射找到 WaterCapability 中的 Capability<IWater> 静态字段。
     * 只执行一次，结果缓存在 cachedCapability。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Optional<Capability> resolveCapabilityField() {
        if (capabilityResolved) {
            return Optional.ofNullable(cachedCapability);
        }
        capabilityResolved = true;
        try {
            Class<?> capClass = Class.forName("homeostatic.common.capabilities.WaterCapability");
            // 遍历所有静态字段，找到类型为 Capability 的那个
            for (Field field : capClass.getDeclaredFields()) {
                if (Capability.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    cachedCapability = (Capability<?>) field.get(null);
                    return Optional.ofNullable(cachedCapability);
                }
            }
        } catch (Throwable ignored) {
            // Homeostatic 未加载，静默处理
        }
        return Optional.empty();
    }

    // ── 获取水分数据 ──────────────────────────────────────────────────────────

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Optional<Object> getWaterData(Player player) {
        return resolveCapabilityField().flatMap(cap -> {
            try {
                // player.getCapability(cap) 返回 LazyOptional<IWater>
                Object lazyOptional = player.getCapability((Capability) cap);
                // LazyOptional.resolve() → Optional<IWater>
                java.lang.reflect.Method resolve = lazyOptional.getClass().getMethod("resolve");
                return (Optional<Object>) resolve.invoke(lazyOptional);
            } catch (Throwable e) {
                return Optional.empty();
            }
        });
    }

    private int getWaterLevel(Player player) {
        return getWaterData(player).map(data -> {
            try {
                // IWater.getWaterLevel()
                return (Integer) data.getClass().getMethod("getWaterLevel").invoke(data);
            } catch (Throwable e) {
                return 20;
            }
        }).orElse(20);
    }

    private float getWaterSaturationLevel(Player player) {
        return getWaterData(player).map(data -> {
            try {
                // IWater.getWaterSaturationLevel()
                return (Float) data.getClass().getMethod("getWaterSaturationLevel").invoke(data);
            } catch (Throwable e) {
                return 0f;
            }
        }).orElse(0f);
    }

    // ── 是否已加载 Homeostatic ───────────────────────────────────────────────

    private boolean isHomeostaticLoaded() {
        return resolveCapabilityField().isPresent();
    }

    // ── IHudElement 实现 ─────────────────────────────────────────────────────

    @Override
    public void renderBar(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null;
        Player player = Minecraft.getInstance().player;
        int waterLevel = getWaterLevel(player);

        RenderHudFunctions.drawDefaultBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.DEFAULT_BAR);
        RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getWaterProgress(waterLevel),
                Settings.colorSettings.get(WATER.name()), Settings.alphaSettings.get(WATER.name()));

        if (Settings.shouldRenderSettings.get(WATER.name() + "_saturation")) {
            RenderHudFunctions.drawProgressBar(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.PROGRESS_BAR, getSaturationProgress(player),
                    Settings.colorSettings.get(WATER.name() + "_saturation"), Settings.alphaSettings.get(WATER.name() + "_saturation"));
        }
    }

    @Override
    public void renderIcon(GuiGraphics guiGraphics, PoseStack poseStack) {
        RenderHudFunctions.drawIcon(guiGraphics, poseStack, getSide(), RenderHudHelper.getPosYMod(getSide()), TextureRecords.BUBBLE_ICON, 81);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, PoseStack poseStack) {
        assert Minecraft.getInstance().player != null;
        Player player = Minecraft.getInstance().player;
        int waterLevel = getWaterLevel(player);
        float waterSaturation = getWaterSaturationLevel(player);

        if (Settings.shouldRenderSettings.get(WATER.name() + "_saturation") && waterSaturation > 0) {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(Math.round(waterLevel + waterSaturation)),
                    getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()),
                    Settings.textColorSettings.get(WATER.name() + "_saturation"), 81);
        } else {
            RenderHudFunctions.drawText(guiGraphics, poseStack, String.valueOf(waterLevel),
                    getSide(), shouldRenderIcon(), RenderHudHelper.getPosYMod(getSide()),
                    Settings.textColorSettings.get(WATER.name()), 81);
        }
    }

    @Override
    public boolean getSide() { return this.renderSide; }

    @Override
    public IHudElement setRenderSide(boolean side) { this.renderSide = side; return this; }

    @Override
    public boolean shouldRender() {
        return Settings.shouldRenderSettings.get(WATER.name()) && isHomeostaticLoaded();
    }

    @Override
    public boolean shouldRenderIcon() {
        return shouldRender() && Settings.shouldRenderIconSettings.get(WATER.name());
    }

    @Override
    public boolean shouldRenderText() {
        return shouldRender() && Settings.shouldRenderTextSettings.get(WATER.name());
    }

    @Override
    public void registerSettings(ConfigCategory mainCategory, ConfigCategory iconCategory, ConfigCategory textCategory,
                                 ConfigCategory colorCategory, ConfigCategory textColorSettings,
                                 ConfigCategory alphaCategory, ConfigEntryBuilder builder) {

        BooleanListEntry enableWaterBar = builder.startBooleanToggle(Component.translatable("option.anaku_status_bars.enable_water_bar"), Settings.shouldRenderSettings.get(WATER.name()))
                .setDefaultValue(WATER.shouldRender())
                .setSaveConsumer(value -> Settings.shouldRenderSettings.replace(WATER.name(), value)).build();
        mainCategory.addEntry(enableWaterBar);

        BooleanListEntry enableWaterIcon = builder.startBooleanToggle(Component.translatable("option.anaku_status_bars.enable_water_icon"), Settings.shouldRenderIconSettings.get(WATER.name()))
                .setDefaultValue(WATER.shouldRenderIcon())
                .setSaveConsumer(value -> Settings.shouldRenderIconSettings.replace(WATER.name(), value)).build();
        iconCategory.addEntry(enableWaterIcon);

        BooleanListEntry enableWaterText = builder.startBooleanToggle(Component.translatable("option.anaku_status_bars.enable_water_text"), Settings.shouldRenderTextSettings.get(WATER.name()))
                .setDefaultValue(WATER.shouldRenderText())
                .setSaveConsumer(value -> Settings.shouldRenderTextSettings.replace(WATER.name(), value)).build();
        textCategory.addEntry(enableWaterText);

        ColorEntry waterColor = builder.startColorField(Component.translatable("option.anaku_status_bars.water_color"), Settings.colorSettings.get(WATER.name()))
                .setDefaultValue(WATER.color())
                .setSaveConsumer(value -> Settings.colorSettings.replace(WATER.name(), value)).build();
        colorCategory.addEntry(waterColor);

        ColorEntry waterTextColor = builder.startColorField(Component.translatable("option.anaku_status_bars.water_text_color"), Settings.textColorSettings.get(WATER.name()))
                .setDefaultValue(WATER.color())
                .setSaveConsumer(value -> Settings.textColorSettings.replace(WATER.name(), value)).build();
        textColorSettings.addEntry(waterTextColor);

        FloatListEntry waterAlpha = builder.startFloatField(Component.translatable("option.anaku_status_bars.water_alpha"), Settings.alphaSettings.get(WATER.name()))
                .setDefaultValue(WATER.alpha()).setMin(0.0f).setMax(1.0f)
                .setTooltip(Component.translatable("option.anakus_status_bars.alpha_tooltip"))
                .setSaveConsumer(value -> Settings.alphaSettings.replace(WATER.name(), value)).build();
        alphaCategory.addEntry(waterAlpha);

        BooleanListEntry enableSaturationBar = builder.startBooleanToggle(Component.translatable("option.anaku_status_bars.enable_water_saturation_bar"), Settings.shouldRenderSettings.get(WATER.name() + "_saturation"))
                .setDefaultValue(WATER.shouldRender())
                .setSaveConsumer(value -> Settings.shouldRenderSettings.replace(WATER.name() + "_saturation", value)).build();
        mainCategory.addEntry(enableSaturationBar);

        ColorEntry saturationColor = builder.startColorField(Component.translatable("option.anaku_status_bars.water_saturation_color"), Settings.colorSettings.get(WATER.name() + "_saturation"))
                .setDefaultValue(Settings.WATER_SATURATION_COLOR_DEFAULT)
                .setSaveConsumer(value -> Settings.colorSettings.replace(WATER.name() + "_saturation", value)).build();
        colorCategory.addEntry(saturationColor);

        ColorEntry saturationTextColor = builder.startColorField(Component.translatable("option.anaku_status_bars.water_saturation_text_color"), Settings.textColorSettings.get(WATER.name() + "_saturation"))
                .setDefaultValue(Settings.WATER_SATURATION_COLOR_DEFAULT)
                .setSaveConsumer(value -> Settings.textColorSettings.replace(WATER.name() + "_saturation", value)).build();
        textColorSettings.addEntry(saturationTextColor);

        FloatListEntry saturationAlpha = builder.startFloatField(Component.translatable("option.anaku_status_bars.water_saturation_alpha"), Settings.alphaSettings.get(WATER.name() + "_saturation"))
                .setDefaultValue(WATER.alpha()).setMin(0.0f).setMax(1.0f)
                .setTooltip(Component.translatable("option.anakus_status_bars.alpha_tooltip"))
                .setSaveConsumer(value -> Settings.alphaSettings.replace(WATER.name() + "_saturation", value)).build();
        alphaCategory.addEntry(saturationAlpha);
    }

    @Override
    public String name() { return WATER.name(); }

    // ── 进度计算 ─────────────────────────────────────────────────────────────

    private int getWaterProgress(int waterLevel) {
        float ratio = Math.min(1, Math.max(0, waterLevel / 20f));
        return (int) Math.min(81, Math.ceil(81 * ratio));
    }

    private int getSaturationProgress(Player player) {
        float saturation = getWaterSaturationLevel(player);
        float ratio = Math.min(1, Math.max(0, saturation / 5.0f));
        return (int) Math.min(81, Math.ceil(81 * ratio));
    }
}