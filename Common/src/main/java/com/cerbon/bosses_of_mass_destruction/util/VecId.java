package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.item.custom.BrimstoneNectarItemEffects;

import java.util.Arrays;
import java.util.function.Supplier;

public enum VecId {
    BrimstoneParticleEffect(BrimstoneNectarItemEffects::new);

    public final Supplier<Vec3Receiver> effectHandler;

    VecId(Supplier<Vec3Receiver> effectHandler) {
        this.effectHandler = effectHandler;
    }

    public static VecId fromInt(int value){
        return Arrays.stream(values()).filter(vecId -> vecId.ordinal() == value).findFirst().orElse(null);
    }
}
