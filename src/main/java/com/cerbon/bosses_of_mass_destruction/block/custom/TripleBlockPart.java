package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TripleBlockPart implements StringRepresentable {
    TOP,
    MIDDLE,
    BOTTOM;

    @Override
    public String toString() {
        return getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        return switch (this){
            case TOP -> "top";
            case MIDDLE -> "middle";
            case BOTTOM -> "bottom";
        };
    }
}
