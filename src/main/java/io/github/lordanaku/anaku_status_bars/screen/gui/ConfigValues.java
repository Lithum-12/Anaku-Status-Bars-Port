package io.github.lordanaku.anaku_status_bars.screen.gui;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

import static io.github.lordanaku.anaku_status_bars.utils.Settings.*;

public class ConfigValues {

    public static ConfigCategory buildPositionCategory() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("category.anaku_status_bars.position"))
                // 左侧顺序
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.left_order"))
                        .binding(new ArrayList<>(LEFT_ORDER_DEFAULT),
                                 () -> new ArrayList<>(sideOrderSettings.get("left")),
                                 val -> sideOrderSettings.replace("left", new ArrayList<>(val)))
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                // 右侧顺序
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.right_order"))
                        .binding(new ArrayList<>(RIGHT_ORDER_DEFAULT),
                                 () -> new ArrayList<>(sideOrderSettings.get("right")),
                                 val -> sideOrderSettings.replace("right", new ArrayList<>(val)))
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                // Y 偏移
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.left_y_offset"))
                        .binding(40,
                                 () -> positionSettings.get("left_y_offset"),
                                 val -> positionSettings.replace("left_y_offset", val))
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.right_y_offset"))
                        .binding(40,
                                 () -> positionSettings.get("right_y_offset"),
                                 val -> positionSettings.replace("right_y_offset", val))
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                // X 偏移
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.left_x_offset"))
                        .binding(0,
                                 () -> positionSettings.get("left_x_offset"),
                                 val -> positionSettings.replace("left_x_offset", val))
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("option.anaku_status_bars.right_x_offset"))
                        .binding(0,
                                 () -> positionSettings.get("right_x_offset"),
                                 val -> positionSettings.replace("right_x_offset", val))
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                .build();
    }
}
