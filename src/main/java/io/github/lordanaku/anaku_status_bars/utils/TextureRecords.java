package io.github.lordanaku.anaku_status_bars.utils;

import io.github.lordanaku.anaku_status_bars.AnakuStatusBarsCore;
import io.github.lordanaku.anaku_status_bars.utils.records.TextureRecord;
import net.minecraft.resources.ResourceLocation;


@SuppressWarnings("unused")
public class TextureRecords {
    public static ResourceLocation statusBarTextures = new ResourceLocation(AnakuStatusBarsCore.MOD_ID, "textures/hud/status_bars.png");
    // GUI icons location in 1.20.1
    public static ResourceLocation guiIconsLocation = new ResourceLocation("textures/gui/icons.png");

    public static final TextureRecord DEFAULT_BAR = new TextureRecord(statusBarTextures,0, 0, 81, 9, 256, 256);
    public static final TextureRecord PROGRESS_BAR = new TextureRecord(statusBarTextures, 0, 9, 81, 9, 256, 256);
    public static final TextureRecord STATUS_EFFECT_BAR = new TextureRecord(statusBarTextures,0, 18, 81, 9, 256, 256);
    public static final TextureRecord EXHAUSTION_BAR = new TextureRecord(statusBarTextures,0, 27, 81, 9, 256, 256);
    public static final TextureRecord EFFECT_BAR = new TextureRecord(statusBarTextures,0, 36, 81, 9, 256, 256);
    public static final TextureRecord SHIELD_ICON = new TextureRecord(statusBarTextures,83, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_OUTLINE_ICON = new TextureRecord(guiIconsLocation,16, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_ICON = new TextureRecord(guiIconsLocation,52, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_POISON_ICON = new TextureRecord(guiIconsLocation,88, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_WITHER_ICON = new TextureRecord(guiIconsLocation,124, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_ABSORPTION_ICON = new TextureRecord(guiIconsLocation,160, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_FROSTBITE_ICON = new TextureRecord(guiIconsLocation,178, 0, 9, 9, 256, 256);
    public static final TextureRecord HEART_MOUNT_ICON = new TextureRecord(guiIconsLocation,88, 9, 9, 9, 256, 256);
    public static final TextureRecord ARMOR_ICON = new TextureRecord(guiIconsLocation,43, 9, 9, 9, 256, 256);
    public static final TextureRecord BUBBLE_ICON = new TextureRecord(guiIconsLocation,16, 18, 9, 9, 256, 256);
    public static final TextureRecord BUBBLE_BURST_ICON = new TextureRecord(guiIconsLocation,25,18, 9, 9, 256, 256);
    public static final TextureRecord FOOD_OUTLINE_ICON = new TextureRecord(guiIconsLocation,16, 27, 9, 9, 256, 256);
    public static final TextureRecord FOOD_ICON = new TextureRecord(guiIconsLocation,52, 27, 9, 9, 256, 256);
    public static final TextureRecord HUNGER_ICON = new TextureRecord(guiIconsLocation,88, 27, 9, 9, 256, 256);

    public static void setDefaultTexture(ResourceLocation resourceLocation) {
        statusBarTextures = resourceLocation;
    }
}
