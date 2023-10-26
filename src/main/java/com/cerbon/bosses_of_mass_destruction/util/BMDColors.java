package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.VecUtils;
import net.minecraft.world.phys.Vec3;

public class BMDColors {
    private static final double colorFactor = 1 / 255.0;
    public static final Vec3 WHITE = VecUtils.unit;
    public static final Vec3 COMET_BLUE = new Vec3(0.0, 1.0, 1.0);
    public static final Vec3 FADED_COMET_BLUE = new Vec3(0.0, 0.3, 0.3);
    public static final Vec3 TELEPORT_PURPLE = new Vec3(1.0, 0.5, 1.0);
    public static final Vec3 ORANGE = new Vec3(1.0, 0.65, 0.1);
    public static final Vec3 RUNIC_BROWN = new Vec3(0.66, 0.34, 0.0);
    public static final Vec3 RED = new Vec3(0.8, 0.2, 0.4);
    public static final Vec3 DARK_RED = new Vec3(0.4, 0.0, 0.0);
    public static final Vec3 ENDER_PURPLE = new Vec3(158 / 255.0, 66 / 255.0, 245 / 255.0);
    public static final Vec3 VOID_PURPLE = new Vec3(0.7, 0.3, 0.6);
    public static final Vec3 LASER_RED = new Vec3(0.8, 0.1, 0.1);
    public static final Vec3 GREY = VecUtils.unit.multiply(0.5, 0.5, 0.5);
    public static final Vec3 GREEN = new Vec3(0.3, 0.8, 0.3);
    public static final Vec3 DARK_GREEN = new Vec3(0.0, 0.5, 0.1);
    public static final Vec3 PINK = new Vec3(0.9, 0.6, 0.8);
    public static final Vec3 ULTRA_DARK_PURPLE = new Vec3(0.3, 0.0, 0.2);
    public static final Vec3 DARK_GREY = new Vec3(0.3, 0.3, 0.3);
    public static final Vec3 LIGHT_ENDER_PEARL = new Vec3(140.0, 244.0, 226.0).multiply(colorFactor, colorFactor, colorFactor);
    public static final Vec3 DARK_ENDER_PEARL = new Vec3(3.0, 38.0, 32.0).multiply(colorFactor, colorFactor, colorFactor);
    public static final Vec3 GOLD = new Vec3(255.0, 211.0, 125.0).multiply(colorFactor, colorFactor, colorFactor);
}
