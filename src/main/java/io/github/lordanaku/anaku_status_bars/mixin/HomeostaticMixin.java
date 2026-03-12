package io.github.lordanaku.anaku_status_bars.mixin;

import io.github.lordanaku.anaku_status_bars.utils.Settings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截 Homeostatic 原版水分栏的渲染，避免与本 mod 水分栏重叠。
 *
 * @Pseudo        —— Homeostatic 未加载时目标类不存在，Mixin 系统跳过而不报错
 * targets=字符串  —— 避免直接引用 WaterHelper.class，防止类加载期 ClassNotFoundException
 *
 * ⚠️ 必须在 anaku_status_bars.mixin.json 的 "client" 列表中声明此类：
 * {
 *   "client": [ "HomeostaticMixin" ]
 * }
 */
@Pseudo
@Mixin(targets = "homeostatic.util.WaterHelper", remap = false)
public class HomeostaticMixin {

    @Inject(method = "drawWaterBar", at = @At("HEAD"), cancellable = true)
    private static void drawWaterBar(
            ResourceLocation sprite,
            int scaledWidth,
            int scaledHeight,
            MobEffectInstance effectInstance,
            Gui gui,
            GuiGraphics guiGraphics,
            float waterSaturationLevel,
            int waterLevel,
            int tickCount,
            CallbackInfo ci) {

        if (Settings.shouldRenderSettings != null
                && Boolean.TRUE.equals(Settings.shouldRenderSettings.get("water"))) {
            ci.cancel();
        }
    }
}
