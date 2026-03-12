package io.github.lordanaku.anaku_status_bars.compat;

import homeostatic.common.capabilities.IWater;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Homeostatic 兼容辅助类。
 *
 * IWater 方法直接调用（编译期类型安全）。
 * Capability 字段通过反射一次性查找，兼容字段名不确定的情况。
 * JVM 懒加载保证：只要调用前通过 ModList 确认 Homeostatic 已加载，
 * 未安装时此类永远不会被加载。
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class HomeostaticHelper {

    private static Capability<IWater> cachedCap = null;

    /** 反射查找 WaterCapability 中的 Capability<IWater> 静态字段，成功后永久缓存。 */
    private static Optional<Capability<IWater>> resolveCapability() {
        if (cachedCap != null) return Optional.of(cachedCap);
        try {
            Class<?> cls = Class.forName("homeostatic.common.capabilities.WaterCapability");
            for (Field f : cls.getDeclaredFields()) {
                if (Capability.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    Capability<?> found = (Capability<?>) f.get(null);
                    if (found != null) {
                        cachedCap = (Capability<IWater>) found;
                        return Optional.of(cachedCap);
                    }
                }
            }
        } catch (Throwable ignored) {}
        return Optional.empty();
    }

    private static Optional<IWater> getData(Player player) {
        return resolveCapability().flatMap(cap -> {
            LazyOptional<IWater> lazy = player.getCapability(cap);
            return lazy.resolve();
        });
    }

    /** 获取玩家当前水分值（0-20）。仅在确认 Homeostatic 已加载后调用。 */
    public static int getWaterLevel(Player player) {
        return getData(player).map(IWater::getWaterLevel).orElse(0);
    }

    /** 获取玩家当前水分饱和度。仅在确认 Homeostatic 已加载后调用。 */
    public static float getWaterSaturationLevel(Player player) {
        return getData(player).map(IWater::getWaterSaturationLevel).orElse(0f);
    }
}