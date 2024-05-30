package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import net.minecraft.util.math.vector.Vector3d;

public class BMDColors {
    private static final double colorFactor = 1 / 255.0;
    public static final Vector3d WHITE = VecUtils.unit;
    public static final Vector3d COMET_BLUE = new Vector3d(0.0, 1.0, 1.0);
    public static final Vector3d FADED_COMET_BLUE = new Vector3d(0.0, 0.3, 0.3);
    public static final Vector3d TELEPORT_PURPLE = new Vector3d(1.0, 0.5, 1.0);
    public static final Vector3d ORANGE = new Vector3d(1.0, 0.65, 0.1);
    public static final Vector3d RUNIC_BROWN = new Vector3d(0.66, 0.34, 0.0);
    public static final Vector3d RED = new Vector3d(0.8, 0.2, 0.4);
    public static final Vector3d DARK_RED = new Vector3d(0.4, 0.0, 0.0);
    public static final Vector3d ENDER_PURPLE = new Vector3d(158 / 255.0, 66 / 255.0, 245 / 255.0);
    public static final Vector3d VOID_PURPLE = new Vector3d(0.7, 0.3, 0.6);
    public static final Vector3d LASER_RED = new Vector3d(0.8, 0.1, 0.1);
    public static final Vector3d GREY = VecUtils.unit.multiply(0.5, 0.5, 0.5);
    public static final Vector3d GREEN = new Vector3d(0.3, 0.8, 0.3);
    public static final Vector3d DARK_GREEN = new Vector3d(0.0, 0.5, 0.1);
    public static final Vector3d PINK = new Vector3d(0.9, 0.6, 0.8);
    public static final Vector3d ULTRA_DARK_PURPLE = new Vector3d(0.3, 0.0, 0.2);
    public static final Vector3d DARK_GREY = new Vector3d(0.3, 0.3, 0.3);
    public static final Vector3d LIGHT_ENDER_PEARL = new Vector3d(140.0, 244.0, 226.0).scale(colorFactor);
    public static final Vector3d DARK_ENDER_PEARL = new Vector3d(3.0, 38.0, 32.0).scale(colorFactor);
    public static final Vector3d GOLD = new Vector3d(255.0, 211.0, 125.0).scale(colorFactor);
}
