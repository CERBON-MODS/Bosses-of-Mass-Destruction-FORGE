package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum TripleBlockPart implements IStringSerializable {
    TOP,
    MIDDLE,
    BOTTOM;

    @Override
    public String toString() {
        return getSerializedName();
    }

    @Override
    public @Nonnull String getSerializedName() {
        switch (this){
            case TOP: return  "top";
            case MIDDLE: return  "middle";
            case BOTTOM: return  "bottom";
        }
        return "";
    }
}
