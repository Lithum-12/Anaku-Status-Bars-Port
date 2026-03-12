package io.github.lordanaku.anaku_status_bars.screen.gui;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.lordanaku.anaku_status_bars.AnakuStatusBarsCore;
import io.github.lordanaku.anaku_status_bars.screen.hud.RenderHudHelper;
import io.github.lordanaku.anaku_status_bars.utils.Settings;
import io.github.lordanaku.anaku_status_bars.utils.interfaces.IHudElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static Screen CreateConfigScreen(Screen parent) {

        // ── 收集各分类 Option ────────────────────────────────────────────────
        List<Option<?>> mainOptions    = new ArrayList<>();
        List<Option<?>> iconOptions    = new ArrayList<>();
        List<Option<?>> textOptions    = new ArrayList<>();
        List<Option<?>> colorOptions   = new ArrayList<>();
        List<Option<?>> textColorOptions = new ArrayList<>();
        List<Option<?>> alphaOptions   = new ArrayList<>();

        // 原版贴图总开关
        mainOptions.add(
                Option.<Boolean>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.vanilla_textures"))
                        .binding(false,
                                () -> Settings.shouldUseVanillaTextures,
                                val -> Settings.shouldUseVanillaTextures = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );

        // 各 HUD 元素注册自己的配置项
        for (IHudElement hudElement : RenderHudHelper.getHudElementRegistry()) {
            hudElement.registerSettings(mainOptions, iconOptions, textOptions,
                    colorOptions, textColorOptions, alphaOptions);
            AnakuStatusBarsCore.LOGGER.info("Registered settings for " + hudElement.name());
        }

        // ── 构建 YACL 界面 ───────────────────────────────────────────────────
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("title.anaku_status_bars.config"))
                // General
                .category(buildCategory("category.anaku_status_bars.general", mainOptions))
                // Icon
                .category(buildCategory("category.anaku_status_bars.icon", iconOptions))
                // Text
                .category(buildCategory("category.anaku_status_bars.text", textOptions))
                // Color
                .category(buildCategory("category.anaku_status_bars.color", colorOptions))
                // Text Color
                .category(buildCategory("category.anaku_status_bars.text_color", textColorOptions))
                // Alpha
                .category(buildCategory("category.anaku_status_bars.alpha", alphaOptions))
                // Position
                .category(ConfigValues.buildPositionCategory())
                // 保存时写入配置文件并刷新 HUD
                .save(ConfigFileHandler::writeToConfig)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildCategory(String translationKey, List<Option<?>> options) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Component.translatable(translationKey));
        options.forEach(builder::option);
        return builder.build();
    }
}
