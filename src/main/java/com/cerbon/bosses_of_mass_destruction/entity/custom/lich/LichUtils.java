package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityStats;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LichUtils {
    public static final List<Float> hpPercentRageModes = Arrays.asList(0.0f, 0.25f, 0.5f, 0.75f, 1.0f);
    public static final Function<Float, Vec3> blueColorFade = f -> MathUtils.lerpVec(f, BMDColors.COMET_BLUE, BMDColors.FADED_COMET_BLUE);
    public static final int textureSize = 256;

    public static long timeToNighttime(long currentTime) {
        long dayLength = 24000L;
        long midnight = 16000L;
        return (currentTime - (currentTime % dayLength)) + midnight;
    }

    public static void cappedHeal(IEntity iEntity, IEntityStats stats, List<Float> hpPercentRageModes, float healingStrength, Consumer<Float> heal) {
        if (iEntity.isAlive()) {
            float targetHealthRatio = MathUtils.roundedStep(stats.getHealth() / stats.getMaxHealth(), hpPercentRageModes, false);
            float healAmt = Mth.clamp(targetHealthRatio * stats.getMaxHealth() - stats.getHealth() - 1, 0f, healingStrength);

            if (healAmt > 0)
                heal.accept(healAmt);
        }
    }
}

