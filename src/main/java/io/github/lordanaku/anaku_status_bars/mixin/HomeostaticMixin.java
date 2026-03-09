package io.github.lordanaku.anaku_status_bars.mixin;

import io.github.lordanaku.anaku_status_bars.utils.Settings;
import homeostatic.util.WaterHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaterHelper.class, remap = false)
public class HomeostaticMixin {
    @Inject(method = "drawWaterBar", at = @At("HEAD"), cancellable = true)
    private static void drawWaterBar(ResourceLocation sprite, int scaledWidth, int scaledHeight, MobEffectInstance effectInstance, Gui gui, GuiGraphics guiGraphics, float waterSaturationLevel, int waterLevel, int tickCount, CallbackInfo ci) {
        // If water bar is enabled in our mod, cancel the original water bar rendering
        if (Settings.shouldRenderSettings.get("water")) {
            ci.cancel();
        }
    }
}
